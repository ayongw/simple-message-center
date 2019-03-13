package com.github.ayongw.simplemessagecenter.inner;

import com.github.ayongw.simplemessagecenter.SimpleMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 组合消息监听对象
 * <p>
 * 用于处理一个事件上的多个事件监听对象
 */
public class MyComboObserver {
    private ReentrantLock lock = new ReentrantLock();

    private List<MyMessageObserverWrapper> observers;

    public MyComboObserver(MyMessageObserverWrapper messageObserver) {
        observers = new ArrayList<>();
//        SoftReference<MyMessageObserverWrapper> reference = new SoftReference<>(messageObserver);
//        observers.add(reference);
        observers.add(messageObserver);
    }

    public void addObserver(MyMessageObserverWrapper messageObserver) {
        lock.lock();
        try {
//            this.observers.add(new SoftReference<>(messageObserver));
            this.observers.add(messageObserver);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 移除监听对象
     *
     * @param observerId 监听对象id
     * @return true有此监听对象，并成功移除；false没有此监听对象
     */
    public boolean removeObserver(String observerId) {
        lock.lock();
        try {
//            Iterator<SoftReference<MyMessageObserverWrapper>> iterator = observers.iterator();
            Iterator<MyMessageObserverWrapper> iterator = observers.iterator();
            while (iterator.hasNext()) {
                MyMessageObserverWrapper next = iterator.next();
                if (null != next) {
                    if (observerId.equals(next.getId())) {
                        iterator.remove();
                        return true;
                    }
                }
            }
            return false;
        } finally {
            lock.unlock();
        }

    }

    public boolean postMessage(String msgName, Object holder, Map userInfo) {
        if (null == observers || observers.isEmpty()) {
            return false;
        }
        final SimpleMessage myMessage = new SimpleMessage(msgName, holder, userInfo);

        new Thread(new Runnable() {
            @Override
            public void run() {
//                for (SoftReference<MyMessageObserverWrapper> observer : observers) {
                for (MyMessageObserverWrapper observerWrapper : observers) {
//                    MyMessageObserverWrapper observerWrapper = observer.get();
                    if (null != observerWrapper) {
                        observerWrapper.getMessageObserver().onMessage(myMessage);
                    }
                }
            }
        }).start();

        return true;
    }
}
