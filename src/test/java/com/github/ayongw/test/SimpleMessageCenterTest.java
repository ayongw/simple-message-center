package com.github.ayongw.test;

import com.github.ayongw.simplemessagecenter.SimpleMessage;
import com.github.ayongw.simplemessagecenter.SimpleMessageCenter;
import com.github.ayongw.simplemessagecenter.SimpleMessageObserver;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiangguangtao
 */
public class SimpleMessageCenterTest {
    public static final String MSG_NAME = "demo.msg";
    public static final String DEMO_HOLDER = "com.demo.service";
    private static final Logger logger = LoggerFactory.getLogger(SimpleMessageCenterTest.class);

    @Test
    public void testSendAndConsumeNoHolder() throws InterruptedException {
        final SimpleMessageCenter defaultCenter = SimpleMessageCenter.getDefaultCenter();
        final AtomicInteger count = new AtomicInteger(0);
        defaultCenter.addObserver(MSG_NAME, null, new SimpleMessageObserver() {
            @Override
            public void onMessage(SimpleMessage message) {
                logger.info("holder NULL 接收到消息 {}", message);
                count.incrementAndGet();
            }
        });

        defaultCenter.addObserver(MSG_NAME, DEMO_HOLDER, new SimpleMessageObserver() {
            @Override
            public void onMessage(SimpleMessage message) {
                logger.info("with holder 接收到消息 {}", message);
                count.incrementAndGet();
            }
        });

        defaultCenter.postMessage(MSG_NAME, null, new HashMap());
        Thread.sleep(1000);

        Assert.assertEquals(1, count.get());
    }

    @Test
    public void testSendAndConsumeHolder() throws InterruptedException {
        final SimpleMessageCenter defaultCenter = SimpleMessageCenter.getDefaultCenter();
        final AtomicInteger count = new AtomicInteger(0);
        defaultCenter.addObserver(MSG_NAME, null, new SimpleMessageObserver() {
            @Override
            public void onMessage(SimpleMessage message) {
                logger.info("holder NULL 接收到消息 {}", message);
                count.incrementAndGet();
            }
        });

        defaultCenter.addObserver(MSG_NAME, DEMO_HOLDER, new SimpleMessageObserver() {
            @Override
            public void onMessage(SimpleMessage message) {
                logger.info("with holder 接收到消息 {}", message);
                count.incrementAndGet();
            }
        });

        defaultCenter.postMessage(MSG_NAME, DEMO_HOLDER, new HashMap());
        Thread.sleep(1000);

        Assert.assertEquals(2, count.get());
    }

}
