package com.github.ayongw.test;

import com.github.ayongw.simplemessagecenter.SimpleMessage;
import com.github.ayongw.simplemessagecenter.SimpleMessageCenter;
import com.github.ayongw.simplemessagecenter.SimpleMessageObserver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jiangguangtao
 */
public class SimpleMessageCenterOperateTest {
    private static final Logger logger = LoggerFactory.getLogger(SimpleMessageCenterOperateTest.class);

    String holder = "SimpleMessageCenterOperateTest.demo.holder";
    String msgName = "hello.world";
    String msgName2 = "hello.another";

    @Before
    public void testAddOrRemove() {

        SimpleMessageCenter defaultCenter = SimpleMessageCenter.getDefaultCenter();

        defaultCenter.addObserver(msgName, holder, new SimpleMessageObserver() {
            @Override
            public void onMessage(SimpleMessage message) {
                logger.info("接受到消息1：{}", message);
            }
        });

        defaultCenter.addObserver(msgName, holder, new SimpleMessageObserver() {
            @Override
            public void onMessage(SimpleMessage message) {
                logger.info("接受到消息2：{}", message);
            }
        });


        defaultCenter.addObserver(msgName2, holder, new SimpleMessageObserver() {
            @Override
            public void onMessage(SimpleMessage message) {
                logger.info("another接收到消息{}", message);
            }
        });
    }


    @Test
    public void testSendMsg() {
        SimpleMessageCenter defaultCenter = SimpleMessageCenter.getDefaultCenter();

        int count = defaultCenter.postMessage(msgName, holder);
        Assert.assertEquals(2, count);

        count = defaultCenter.postMessage(msgName2, holder);
        Assert.assertEquals(1, count);

        int removeCount = defaultCenter.removeAllObserver(msgName, holder);
        Assert.assertEquals(2, removeCount);

        count = defaultCenter.postMessage(msgName, holder);
        Assert.assertEquals(0, count);
    }


    @Test
    public void testRemoveObserver() {
        SimpleMessageCenter defaultCenter = SimpleMessageCenter.getDefaultCenter();

        int count = defaultCenter.postMessage(msgName, holder);
        Assert.assertEquals(2, count);

        count = defaultCenter.postMessage(msgName2, holder);
        Assert.assertEquals(1, count);

        int removeCount = defaultCenter.removeObserversByHolder(holder);
        Assert.assertEquals(3, removeCount);

    }
}
