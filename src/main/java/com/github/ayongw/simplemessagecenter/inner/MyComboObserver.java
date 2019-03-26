package com.github.ayongw.simplemessagecenter.inner;

import com.github.ayongw.simplemessagecenter.SimpleMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 组合消息监听对象
 * <p>
 * 用于处理一个事件上的多个事件监听对象
 *
 * @author jiangguangtao
 */
public class MyComboObserver {
    private List<MyMessageObserverWrapper> observers;

    public MyComboObserver(MyMessageObserverWrapper messageObserver) {
        observers = new ArrayList<>();
        observers.add(messageObserver);
    }

    public void addObserver(MyMessageObserverWrapper messageObserver) {
        this.observers.add(messageObserver);
    }

    /**
     * 移除监听对象
     *
     * @param observerId 监听对象id
     * @return 1有此监听对象，并成功移除；0没有此监听对象
     */
    public int removeObserver(String observerId) {
        Iterator<MyMessageObserverWrapper> iterator = observers.iterator();
        while (iterator.hasNext()) {
            MyMessageObserverWrapper next = iterator.next();
            if (null != next) {
                if (observerId.equals(next.getId())) {
                    iterator.remove();
                    return 1;
                }
            }
        }
        return 0;
    }

    /**
     * 只记录是否提交成功
     *
     * @param msgName  消息名称
     * @param holder   消息所属对象
     * @param userInfo 附加信息
     * @return 成功发送到监听者数量
     */
    public int postMessage(String msgName, Object holder, Map<String, Object> userInfo) {
        if (null == observers || observers.isEmpty()) {
            return 0;
        }
        final SimpleMessage myMessage = new SimpleMessage(msgName, holder, userInfo);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (MyMessageObserverWrapper observerWrapper : observers) {
                    if (null != observerWrapper) {
                        observerWrapper.getMessageObserver().onMessage(myMessage);
                    }
                }
            }
        }).start();

        return observers.size();
    }

    /**
     * 当前监听对象数
     *
     * @return
     */
    public int size() {
        return observers.size();
    }

    /**
     * 清空所有监听
     */
    public void clear() {
        this.observers.clear();
    }
}
