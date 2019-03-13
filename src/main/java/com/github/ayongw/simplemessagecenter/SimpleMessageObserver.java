package com.github.ayongw.simplemessagecenter;

/**
 * 消息回调
 */
public interface SimpleMessageObserver {
    /**
     * 当指定的消息到达时触发的事件
     *
     * @param message 接收到的消息对象
     */
    void onMessage(SimpleMessage message);
}
