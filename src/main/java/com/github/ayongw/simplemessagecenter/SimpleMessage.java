package com.github.ayongw.simplemessagecenter;

import java.util.Map;

/**
 * 消息实体对象
 */
public class SimpleMessage {
    private String name;
    private Object holder;
    private Map userInfo;

    public SimpleMessage() {
    }

    public SimpleMessage(String name, Object holder, Map userInfo) {
        this.name = name;
        this.holder = holder;
        this.userInfo = userInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getHolder() {
        return holder;
    }

    public void setHolder(Object holder) {
        this.holder = holder;
    }

    public Map getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(Map userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "SimpleMessage{" +
                "name='" + name + '\'' +
                ", holder=" + holder +
                ", userInfo=" + userInfo +
                '}';
    }
}
