# 服务存活检测并发送通知

服务存活检测并发送通知

## 编译部署

### 修改配置参数

修改[application.yml](./src/main/resources/application.yml) 或者复制出一份重命名为`application-prod.yml`配合脚本[startup.sh](./startup.sh)，修改其中的参数后再编译

```yaml
server:
  port: 9003 # 端口

message:
  period: 180 # 消息频率，最小60, 默认180, 单位: 秒
  suffix: server break down # 消息内容后缀,最终消息内容: [*服务名称* + 后缀]
  channels:
    - platform: telegram # 消息平台,支持`telegram`,`feishu`,`lark`
      open-flag: 1 # 是否开启, 0:关闭, 1:开启
      app-id: 
      app-secret: telegram bot token # 如果`platform`是`telegram`,`app-secret`则为`botToken`
      chat-id: "-100123"

remote:
  servers:
    - name: server1 # 服务名称
      open-flag: 1 # 是否开启, 0:关闭, 1:开启
      addr: http://127.0.0.1:8080/hello # 检测地址,只支持Http
      period: 10000 # 请求间隔,毫秒
      timeout: 5000 # 请求超时,毫秒
      fail-count: 5 # 连续失败次数
```

### Windows

 ```shell
 cd .\server-keepalive-java\
 mvn clean -DskipTests package
 java -Xms40M -Xmx40M -Xss896k -XX:MetaspaceSize=50M -XX:MaxMetaspaceSize=50M -XX:+UnlockExperimentalVMOptions -XX:G1NewSizePercent=60 -XX:G1MaxNewSizePercent=72 -jar .\target\server-keepalive-java-1.0.0.jar 
 ```

### Linux

 ```shell
 cd server-keepalive-java/
 mvn clean -DskipTests package
 java -Xms40M -Xmx40M -Xss896k -XX:MetaspaceSize=50M -XX:MaxMetaspaceSize=50M -XX:+UnlockExperimentalVMOptions -XX:G1NewSizePercent=60 -XX:G1MaxNewSizePercent=72 -jar target/server-keepalive-java-1.0.0.jar
 ```
 或者配合脚本[startup.sh](./startup.sh), 默认使用`application-prod.yml`配置
 ```shell
 cd server-keepalive-java/
 mvn clean -DskipTests package && chmod +x startup.sh
 ./startup.sh
 ```