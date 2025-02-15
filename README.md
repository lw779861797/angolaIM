

<div align="center">  

<img src="http://193.112.161.26:88/imooc/M00/00/00/rBAABF86obuAUMTLAD7UQh42qar596_80x80.png"  /> 
<br/>

[![Build Status](https://img.shields.io/badge/aim-angola--im-green)](https://github.com/lw779861797/angolaIM/tree/dev)
[![Build Status](https://img.shields.io/badge/java-v1.8-blue)](https://github.com/lw779861797/angolaIM/tree/dev)
[![Build Status](https://img.shields.io/badge/platform-linux%7Cwindow-orange)](https://github.com/lw779861797/angolaIM/tree/dev)
[![Build Status](https://img.shields.io/badge/contact-779861797%40qq.com-yellowgreen)](https://github.com/lw779861797/angolaIM/tree/dev) 

📘[介绍](#介绍) | 🏖[TODO LIST](#todo-list) | 🌈[系统架构](#系统架构) |💡[流程图](#流程图)|🌁[快速启动](#快速启动)|👨🏻‍✈️[内置命令](#客户端内置命令)|🎤[通信](#群聊私聊)


</div>
<br/>


## 介绍

`AIM(AngolaIM)` 一款面向开发者的 `IM(即时通讯)`系统；同时提供了一些组件帮助开发者构建一款属于自己可水平扩展的 `IM` 。

借助 `AIM` 你可以实现以下需求：

- `IM` 即时通讯系统。
- 适用于 `APP` 的消息推送中间件。


## TODO LIST

* [x] [群聊](#群聊)
* [x] [私聊](#私聊)
* [x] [内置命令](#客户端内置命令)
* [x] [聊天记录查询](#聊天记录查询)。
* [x] [一键开启价值 2 亿的 `AI` 模式](#ai-模式)
* [x] 使用 `Google Protocol Buffer` 高效编解码
* [x] 根据实际情况灵活的水平扩容、缩容
* [x] 服务端自动剔除离线客户端
* [x] 客户端自动重连
* [x] [延时消息](#延时消息)
* [ ] 分组群聊
* [ ] SDK 开发包
* [ ] 离线消息
* [ ] 协议支持消息加密



## 系统架构

![](https://i.loli.net/2019/05/08/5cd1d45a156f1.jpg)

- `AIM` 中的各个组件均采用 `SpringBoot` 构建。
-  采用 `Netty` 构建底层通信。
-  `Redis` 存放各个客户端的路由信息、账号信息、在线状态等。
-  `Zookeeper` 用于 `IM-server` 服务的注册与发现。


### aim-server

`IM` 服务端；用于接收 `client` 连接、消息透传、消息推送等功能。

**支持集群部署。**

### aim-forward-route

消息路由服务器；用于处理消息路由、消息转发、用户登录、用户下线以及一些运营工具（获取在线用户数等）。

### aim-client

`IM` 客户端；给用户使用的消息终端，一个命令即可启动并向其他人发起通讯（群聊、私聊）。

## 流程图

![](https://i.loli.net/2019/05/08/5cd1d45b982b3.jpg)

- 客户端向 `route` 发起登录。
- 登录成功从 `Zookeeper` 中选择可用 `IM-server` 返回给客户端，并保存登录、路由信息到 `Redis`。
- 客户端向 `IM-server` 发起长连接，成功后保持心跳。
- 客户端下线时通过 `route` 清除状态信息。


## 快速启动

首先需要安装 `Zookeeper、Redis` 并保证网络通畅。

```shell
git clone https://github.com/lw779861797/angolaIM.git
cd aim
mvn -Dmaven.test.skip=true clean package
```

### 部署 IM-server(aim-server)

```shell
cp /aim/aim-server/target/aim-server-1.0-SNAPSHOT.jar /work/server/
cd /work/server/
nohup java -jar  /root/server/aim-server-1.0-SNAPSHOT.jar --aim.server.port=9000 --app.zk.addr=zk地址  > /root/server/log.file 2>&1 &
```

> aim-server 集群部署同理，只要保证 Zookeeper 地址相同即可。

### 部署路由服务器(aim-forward-route)

```shell
cp /aim/aim-server/aim-forward-route/target/aim-forward-route-1.0-SNAPSHOT.jar /work/route/
cd /work/route/
nohup java -jar  /root/route/aim-forward-route-1.0-SNAPSHOT.jar --app.zk.addr=zk地址 --spring.redis.host=redis地址 --spring.redis.port=6379  > /root/route/log.file 2>&1 &
```

> aim-forward-route 本身就是无状态，可以部署多台；使用 Nginx 代理即可。


### 启动客户端

```shell
cp /aim/aim-client/target/aim-client-1.0-SNAPSHOT.jar /work/route/
cd /work/route/
java -jar aim-client-1.0-SNAPSHOT.jar --server.port=8084 --aim.user.id=唯一客户端ID --aim.user.userName=用户名 --aim.route.url=http://路由服务器:8083/
```

![](https://ws2.sinaimg.cn/large/006tNbRwly1fylgxjgshfj31vo04m7p9.jpg)
![](https://ws1.sinaimg.cn/large/006tNbRwly1fylgxu0x4uj31hy04q75z.jpg)

如上图，启动两个客户端可以互相通信即可。

### 本地启动客户端

#### 注册账号
```shell
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{
  "reqNo": "1234567890",
  "timeStamp": 0,
  "userName": "zhangsan"
}' 'http://路由服务器:8083/registerAccount'
```

从返回结果中获取 `userId`

```json
{
    "code":"9000",
    "message":"成功",
    "reqNo":null,
    "dataBody":{
        "userId":1547028929407,
        "userName":"test"
    }
}
```

#### 启动本地客户端
```shell
# 启动本地客户端
cp /aim/aim-client/target/aim-client-1.0-SNAPSHOT.jar /work/route/
cd /work/route/
java -jar aim-client-1.0-SNAPSHOT.jar --server.port=8084 --aim.user.id=上方返回的userId --aim.user.userName=用户名 --aim.route.url=http://路由服务器:8083/
```

## 客户端内置命令

| 命令 | 描述|
| ------ | ------ | 
| `:q!` | 退出客户端| 
| `:olu` | 获取所有在线用户信息 | 
| `:all` | 获取所有命令 | 
| `:q [option]` | 【:q 关键字】查询聊天记录 | 
| `:ai` | 开启 AI 模式 | 
| `:qai` | 关闭 AI 模式 | 
| `:pu` | 模糊匹配用户 | 
| `:info` | 获取客户端信息 | 
| `:emoji [option]` | 查询表情包 [option:页码] | 
| `:delay [msg] [delayTime]` | 发送延时消息 | 
| `:` | 更多命令正在开发中。。 | 

![](https://ws3.sinaimg.cn/large/006tNbRwly1fylh7bdlo6g30go01shdt.gif)

### 聊天记录查询

![](https://s1.ax1x.com/2020/09/22/wOI7Yq.png)

使用命令 `:q 关键字` 即可查询与个人相关的聊天记录。

> 客户端聊天记录默认存放在 `/opt/logs/aim/`，所以需要这个目录的写入权限。也可在启动命令中加入 `--aim.msg.logger.path = /自定义` 参数自定义目录。



### AI 模式

![](https://s1.ax1x.com/2020/09/22/wOoDjU.png)

使用命令 `:ai` 开启 AI 模式，之后所有的消息都会由 `AI` 响应。

`:qai` 退出 AI 模式。

### 前缀匹配用户名

![](https://s1.ax1x.com/2020/09/22/wOT88x.png)

使用命令 `:qu prefix` 可以按照前缀的方式搜索用户信息。

> 该功能主要用于在移动端中的输入框中搜索用户。 

### 群聊/私聊

#### 群聊

![](https://s1.ax1x.com/2020/09/22/wO7Eod.png)
![](https://s1.ax1x.com/2020/09/22/wO7tWq.png)

群聊只需要在控制台里输入消息回车后即可发送，同时所有在线客户端都可收到消息。

#### 私聊

私聊首先需要知道对方的 `userID` 才能进行。

输入命令 `:olu` 可列出所有在线用户。

![](https://s1.ax1x.com/2020/09/22/wO7x0g.png)

接着使用 `userId;;消息内容` 的格式即可发送私聊消息。

![](https://s1.ax1x.com/2020/09/22/wOHsu8.png)
![](https://s1.ax1x.com/2020/09/22/wOb9bD.png)

### emoji 表情支持

使用命令 `:emoji 1` 查询出所有表情列表，使用表情别名即可发送表情。

![](https://s1.ax1x.com/2020/09/22/wObMVg.png)
![](https://s1.ax1x.com/2020/09/22/wOb8Gn.png)
 
### 延时消息

发送 10s 的延时消息：

```shell
:delay delayMsg 10
```

![](https://s1.ax1x.com/2020/09/22/wObqL8.png)
![](https://s1.ax1x.com/2020/09/22/wObzJs.png)



