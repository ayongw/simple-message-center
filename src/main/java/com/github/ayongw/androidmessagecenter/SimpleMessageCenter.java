package com.github.ayongw.androidmessagecenter;

import com.github.ayongw.androidmessagecenter.inner.MyComboObserver;
import com.github.ayongw.androidmessagecenter.inner.MyMessageObserverWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 消息通知中心类
 */
public class SimpleMessageCenter {
    // 事件-->事件监听列表
    private Map<String, MyComboObserver> messageObservers = new HashMap<>();

    private static SimpleMessageCenter defaultCenter;

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

        MyComboObserver comboObserver = messageObservers.get(key);
        if (null == comboObserver) {
            comboObserver = new MyComboObserver(mmow);
            messageObservers.put(key, comboObserver);
        } else {
            comboObserver.addObserver(mmow);
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
        MyComboObserver comboObserver = messageObservers.get(key);
        if (null == comboObserver) {
            return false;
        }
        return comboObserver.removeObserver(observerId);
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
        MyComboObserver remove = messageObservers.remove(key);
        return null != remove;
    }


    /**
     * 发送一个消息
     *
     * @param msgName 消息名称
     * @param holder  消息发送者标识 可以为null
     * @return true发送成功，并有消费者处理
     */
    public boolean postMessage(String msgName, Object holder) {
        String key = getObserverKey(msgName, holder);
        MyComboObserver directObserver = messageObservers.get(key);

        if (null == directObserver) {
            return false;
        }

        return directObserver.postMessage(msgName, holder, new HashMap());
    }

    /**
     * 发送一个消息
     *
     * @param msgName  消息名称
     * @param holder   消息发送者标识 可以为null
     * @param userInfo 附加信息
     * @return true发送成功，并有消费者处理
     */
    public boolean postMessage(String msgName, Object holder, Map userInfo) {
        String key = getObserverKey(msgName, holder);
        MyComboObserver directObserver = messageObservers.get(key);
        if (null == directObserver) {
            return false;
        }

        return directObserver.postMessage(msgName, holder, userInfo);
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
        sb.append("sok:").append(msgName).append("@");
        if (null == holder) {
            sb.append("*");
        } else {

            if (Class.class.isAssignableFrom(holder.getClass())) {
                Class clazz = (Class) holder;
                sb.append(clazz.getName());
            } else if (CharSequence.class.isAssignableFrom(holder.getClass())) {
                String str = holder.toString();
                if (SimpleUtils.isBlank(str)) {
                    sb.append("*");
                } else {
                    sb.append(str);
                }
            } else {
                throw new IllegalArgumentException("holder只能是class或者string");
            }
        }
        return sb.toString();
    }

}
