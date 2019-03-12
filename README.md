# simple-message-center
    一个简单的模拟消息中心的jar
## 使用方法
1. 定义监听类
```java
SimpleMessageCenter.getDefaultCenter().addObserver("demo.msg", null, new SimpleMessageObserver() {
    @Override
    public void onMessage(SimpleMessage message) {
        logger.info("holder NULL 接收到消息 {}", message);
    }
});

```
2. 发送消息
```java
SimpleMessageCenter.getDefaultCenter().postMessage("demo.msg", null, new HashMap());
```
