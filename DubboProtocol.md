## Dubbo 序列化协议

说说dubbo的基本工作原理，那是你必须知道的，至少要知道dubbo分哪些层，然后平时怎么发起rpc请求的，注册、发现、调用，这些是基本的。

接着就哭针对底层进行深入问问   比如第一步就可以问问序列化协议这块，就是平时PRC的时候怎么走的？
 
 
### 常见问题解析
序列化就是把数据结构或者是一些对象，转换为二进制串的过程，而反序列化是将在序列化过程中所生成的二进制串转换成数据结构或者对象的过程。

![](https://img2018.cnblogs.com/blog/1231979/201912/1231979-20191222143813085-1362198131.png)


### Dubbo支持哪些通信协议?
#### dubbo协议
默认就是走dubbo 协议，单一长连接，进行的是NIO 异步通信，基于hessian 作为序列化协议。使用的场景是：传输数据量小（每次请求在 10kb以内），但是并发量很高。


为了要支持高并发场景，一般是服务提供者就几台机器，但是服务消费者有上百台，可能每天调用达到上亿次！此时用长连接是最合适的，就是跟每个服务消费者维持一个长连接就可以，可能总共就100个连接。然后后后面直接基于连接NIO 异步通信，可以支撑高并发请求。


长连接，通俗点说，就是连接过后可以持续发送请求，无须再建立连接。
![](https://img2018.cnblogs.com/blog/1231979/201912/1231979-20191222144828807-564774701.png)


![](https://img2018.cnblogs.com/blog/1231979/201912/1231979-20191222144912619-235397600.png)


#### RMI 协议
Java 二进制序列化，多个短连接，适合消费者和提供者数量差不多的情况，适合用于为你文件的传输，一般较少用。


#### hessian协议
hessian 序列化协议，多个短连接，适用于提供者数量比消费者数量还多的情况，适用于文件的传输，一般较少用。


#### http协议
json协议 


#### webservice
SOAP 文本  (SimpleObjectaccessPRotocal,简单对象访问协议)



### Dubbo支持哪些序列化协议

dubbo支持hession 、Java二进制序列化、json、SOAP文本序列化多种序列化协议。但是hessian是其默认的序列化协议。



### 说一下Hessian的数据结构

`Hessian的对象序列化机制有8种原始类型`

- 原始二进制数据
- boolean
- 64-bit date (64位毫秒值的日期)
- 64-bit double
- 32-bit int
- 64-bit long
- null
- UTF-8 编码的String

>另外还包括 3 种 递归类型： list for lists and arrays 、map for maps and dictionaries、object for objects

>还有一种特殊的类型：ref：用来表示对共享对象的引用




### 为什么PB的效率是最高的
可能有一些同学比较习惯于JSON or XML 数据存储格式，对于Protocol Buffer 还比较陌生。


Protocol Buffer 其实是Google 出品的一种轻量并且搞笑的结构化数据存储格式，性能比JSON、XML 要高很多。


其实PB 之所以性能如此好，主要得益于两个：
第一：它使用protol编译器，自动进行序列化和反序列化，速度非常快，应该比XML和JSON 快上 20-100倍


第二：它的数据压缩效果好，就是说它序列化后的数据量体积小。因为体积小，传输起来带宽和速度上会有优化。
