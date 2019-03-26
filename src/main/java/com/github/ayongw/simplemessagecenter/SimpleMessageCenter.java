package com.github.ayongw.simplemessagecenter;

import com.github.ayongw.simplemessagecenter.inner.MyComboObserver;
import com.github.ayongw.simplemessagecenter.inner.MyMessageObserverWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 消息通知中心类
 * <p> msgName+null   表示接收所有的msgName标识的消息</p>
 * <p> msgName+holder 表示只接收发送者为holder的msgName的消息</p>
 */
public class SimpleMessageCenter {
    private static final String KEY_PREFIX = "sok:";

    // 事件-->事件监听列表
    private Map<String, MyComboObserver> messageObservers = new HashMap<>();

    private static SimpleMessageCenter defaultCenter;

    private ReentrantLock observerLock = new ReentrantLock();

    private SimpleMessageCenter() {

    }

    /**
     * 获取默认的消息通知中心
     *
     * @return 默认的消息中心对象
     */
    public static SimpleMessageCenter getDefaultCenter() {
        if (null == defaultCenter) {
            synchronized (SimpleMessageCenter.class) {
                if (null == defaultCenter) {
                    defaultCenter = new SimpleMessageCenter();
                }
            }
        }
        return defaultCenter;
    }

    /**
     * 获取一个新的消息中心，使用用此方法，可以区分默认的消息中心
     * <p>
     * 一般不使用此方法
     *
     * @return 新的消息中心对象
     */
    public static SimpleMessageCenter getNewCenter() {
        return new SimpleMessageCenter();
    }

    /**
     * 添加消费监听对象
     * <p>
     * 比如 msgName="click"，如果holder为null，则为监听所有click消息。
     * 如果holder=com.example.MainActivity，则只监听MainActivity发出来的消息
     *
     * @param msgName         消息名称
     * @param holder          消息所属对象 如果此为null，将监听所有事件，此可以作为同名事件的分组标识。
     * @param messageObserver 消息处理类对象
     * @return 处理类对象id，可用于移除
     */
    public String addObserver(String msgName, Object holder, SimpleMessageObserver messageObserver) {
        if (null == messageObserver) {
            throw new IllegalArgumentException("消息监听类为null");
        }

        String key = getObserverKey(msgName, holder);

        String id = UUID.randomUUID().toString();
        MyMessageObserverWrapper mmow = new MyMessageObserverWrapper(id, messageObserver);

        observerLock.lock();
        try {
            MyComboObserver comboObserver = messageObservers.get(key);
            if (null == comboObserver) {
                comboObserver = new MyComboObserver(mmow);
                messageObservers.put(key, comboObserver);
            } else {
                comboObserver.addObserver(mmow);
            }
        } finally {
            observerLock.unlock();
        }

        return id;
    }

    /**
     * 移除监听对象
     *
     * @param msgName    监听的消费类型
     * @param holder     消息发送者标识
     * @param observerId 监听对象id
     * @return true成功移除指定的监听对象；false没有此监听
     */
    public boolean removeObserver(String msgName, Object holder, String observerId) {
        String key = getObserverKey(msgName, holder);
        observerLock.lock();
        try {
            MyComboObserver comboObserver = messageObservers.get(key);
            if (null == comboObserver) {
                return false;
            }
            return comboObserver.removeObserver(observerId);
        } finally {
            observerLock.unlock();
        }
    }

    /**
     * 移除此消息名称下的所有监听对象
     *
     * @param msgName 消息名称
     * @param holder  消息发送者标识
     * @return true 有监听并且成功移除；false无监听
     */
    public boolean removeAllObserver(String msgName, Object holder) {
        String key = getObserverKey(msgName, holder);
        observerLock.lock();
        try {
            MyComboObserver remove = messageObservers.remove(key);
            return null != remove;
        } finally {
            observerLock.unlock();
        }
    }

    /**
     * 一次移除holder对象上的所有监听，但不支持移除全部（holder为null）
     *
     * @param holder
     * @return 是否成功移除了
     */
    public boolean removeObserversByHolder(Object holder) {
        String suffix = getHolderSuffix(holder);
        if ("*".equals(suffix)) {
            return false;
        }
        String keySuffix = "@" + suffix;
        boolean resultFlag = false;
        observerLock.lock();
        try {
            List<String> keySet = new ArrayList<>(messageObservers.keySet());
            for (String key : keySet) {
                if (key.startsWith(KEY_PREFIX) && key.endsWith(keySuffix)) {
                    if (null != messageObservers.remove(key)) {
                        resultFlag = true;
                    }
                }
            }
        } finally {
            observerLock.unlock();
        }

        return resultFlag;
    }


    /**
     * 发送一个消息
     *
     * @param msgName 消息名称
     * @param holder  消息发送者标识 可以为null
     * @return true发送成功，并有消费者处理
     */
    public boolean postMessage(String msgName, Object holder) {
        return postMessage(msgName, holder, new HashMap());
    }

    /**
     * 发送一个消息
     * <p>
     * msgName+null     只发送全局消息
     * msgName+holder   发送到holder下指定msgName的监听，同时发送到全局msgName监听下。
     *
     * @param msgName  消息名称
     * @param holder   消息发送者标识 可以为null
     * @param userInfo 附加信息
     * @return true发送成功，并有消费者处理
     */
    public boolean postMessage(String msgName, Object holder, Map userInfo) {
        String key = getObserverKey(msgName, holder);
        MyComboObserver directObserver = messageObservers.get(key);

        boolean directFlag = false;
        if (null != directObserver) {
            directFlag = directObserver.postMessage(msgName, holder, userInfo);
        }

        String fuzzyKey = getFuzzyObserverKey(msgName, holder);
        if (null == fuzzyKey) {
            return directFlag;
        }

        MyComboObserver fuzzyObserver = messageObservers.get(fuzzyKey);
        if (null == fuzzyObserver) {
            return directFlag;
        }

        boolean fuzzyFlag = fuzzyObserver.postMessage(msgName, holder, userInfo);

        return directFlag || fuzzyFlag;
    }

    /**
     * 发送消息时，获取模糊的key，用于发送消息到大的监听类上。
     *
     * @param msgName 消息名称
     * @param holder  消息发送者标识
     * @return null表示没有相应的模糊监听key
     */
    private String getFuzzyObserverKey(String msgName, Object holder) {
        if (null == holder) {
            return null;
        }

        if (CharSequence.class.isAssignableFrom(holder.getClass())) {
            if (SimpleUtils.isBlank(holder.toString())) {
                return null;
            }
        }

        return getObserverKey(msgName, null);
    }

    /**
     * 获取消息ey
     *
     * @param msgName 消息名称
     * @param holder  消息发送者标识
     * @return 消息key
     */
    private String getObserverKey(String msgName, Object holder) {
        StringBuilder sb = new StringBuilder();
        sb.append(KEY_PREFIX).append(msgName).append("@");
        sb.append(getHolderSuffix(holder));
        return sb.toString();
    }

    /**
     * 获取holder对象在监听key上的后缀名称
     *
     * @param holder
     * @return 如果为null或者空，返回*
     */
    private String getHolderSuffix(Object holder) {
        if (null == holder) {
            return "*";
        } else {
            if (Class.class.isAssignableFrom(holder.getClass())) {
                Class clazz = (Class) holder;
                return clazz.getName();
            } else if (CharSequence.class.isAssignableFrom(holder.getClass())) {
                String str = holder.toString();
                if (SimpleUtils.isBlank(str)) {
                    return "*";
                } else {
                    return str;
                }
            } else {
                throw new IllegalArgumentException("holder只能是class或者string");
            }
        }
    }

}
