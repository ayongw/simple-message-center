package com.github.ayongw.simplemessagecenter.inner;


import com.github.ayongw.simplemessagecenter.SimpleMessageObserver;

/**
 * 内部使用的类
 */
public class MyMessageObserverWrapper {
    private String id;
    private SimpleMessageObserver messageObserver;

    public MyMessageObserverWrapper(String id, SimpleMessageObserver messageObserver) {
        this.id = id;
        this.messageObserver = messageObserver;
    }

    public String getId() {
        return id;
    }

    public SimpleMessageObserver getMessageObserver() {
        return messageObserver;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null == obj) {
            return false;
        }

        if (obj instanceof MyMessageObserverWrapper) {
            MyMessageObserverWrapper mmow = (MyMessageObserverWrapper) obj;

            return this.id.equals(mmow.getId());
        }

        return false;
    }
}
