# simple-message-center
    一个简单的模拟消息中心的jar
    
    发布Jar没有任何第三方依赖，简单直接使用
    
    当前版本号 1.1.0    
    
    
## 使用方法
### 1.添加依赖
maven方式
```
<dependency>
  <groupId>com.github.ayongw</groupId>
  <artifactId>simple-message-center</artifactId>
  <version>1.1.0</version>
</dependency>
```
Gradle Groovy DSL
```
implementation 'com.github.ayongw:simple-message-center:1.1.0'
```
其它使用方式请在<https://search.maven.org>查询后使用。

### 2. 定义监听类
```
SimpleMessageCenter.getDefaultCenter().addObserver("demo.msg", null, new SimpleMessageObserver() {
    @Override
    public void onMessage(SimpleMessage message) {
        logger.info("holder NULL 接收到消息 {}", message);
    }
});

```
### 3. 发送消息
```
SimpleMessageCenter
    .getDefaultCenter()
    .postMessage("demo.msg", null, new HashMap());
```
