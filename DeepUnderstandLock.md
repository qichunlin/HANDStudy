# 深入理解乐观锁和悲观锁

## 1 “锁”的定义
```
	当多个线程修改共享变量时,我们可以给修改操作上锁(synchronize)
	当多个用户修改表中同一个数据时,我们可以给该行数据上锁(行锁).因此,所锁其实是在并发下控制多个操作的顺序执行，以此来保证数据安全的变动。并且锁是一种保证数据安全的机制和手段,而不是特定与某项技术的.
```

## 2 悲观锁(Pessimistic Concurrency Control)
### 2.1 定义
```
	悲观锁认为被它保护的数据是极不安全的,每时每刻都有可能变动,一个事务拿到悲观锁后(理解为一个用户),其他任何事务不能对该数据进行修改,只能等待锁被释放才可以执行。
```

### 2.2 实现方式
```
	数据库中的行锁,表锁,读锁,写锁以及synchronize实现的锁均为悲观锁。
```

>注意：我们经常使用的是mysql,mysql中最常用的引擎是Innodb,Innodb默认使用的是行锁,而行锁是基于索引的，因此要想加上行锁，在加锁时必须命中索引，否则将使用表锁

- 表锁
![](https://img2018.cnblogs.com/blog/1231979/201907/1231979-20190702015801769-880770624.png)

- 行锁
![](https://img2018.cnblogs.com/blog/1231979/201907/1231979-20190702015707150-246884935.png)


## 3.乐观锁(Optimistic Concurrency Control)

### 3.1 定义
```
	它认为数据的变动不会太频繁，因此，它允许多个事务同时对数据进行变动
```

### 3.2 实现方式
```
	乐观锁通常是通过在表中增加一个版本(version)或时间戳(timestamp)来实现,其中，版本最为常用
	事务在从数据库中取数据时,会将该数据的版本也取出来(v1)，当事务对数据变动完毕想要将其更新到表中时,会将之前取出的版本v1与数据中最新的版本v2相对比,如果v1=v2，那么说明在数据变动期间,没有其他事务对数据进行修改,此时,就允许事务对表中的数据进行修改，并且修改时version加1，以此来表明已经被变动。
```


## 4 两种锁实现两种不同环境
场景：A和B用户最近都想吃猪肉脯，于是他们打开了购物网站，并且找到了同一家卖猪肉脯的>店铺。下面是这个店铺的商品表goods结构和表中的数据。

![](https://img2018.cnblogs.com/blog/1231979/201907/1231979-20190702021422227-1954463022.png)
从表中可以看到猪肉脯目前的数量只有1个了。在不加锁的情况下，如果A，B同时下单，就有可能导致超卖。

### 4.1 悲观锁解决方案
```
	利用悲观锁的解决思路是，我们认为数据修改产生冲突的概率比较大，所以在更新之前，我们显示的对要修改的记录进行加锁，直到自己修改完再释放锁。加锁期间只有自己可以进行读写，其他事务只能读不能写。
```
A下单前先给猪肉脯这行数据（id=1）加上悲观锁（行锁）。此时这行数据只能A来操作，也就是只有A能买。B想买就必须一直等待。

当A买好后，B再想去买的时候会发现数量已经为0，那么B看到后就会放弃购买。

那么如何给猪肉脯也就是id=1这条数据加上悲观锁呢？我们可以通过以下语句给id=1的这行数据加上悲观锁
```
select num from lock_goods where id = 1 for update;
```

### 4.2 悲观锁的加锁图解
![](https://img2018.cnblogs.com/blog/1231979/201907/1231979-20190702022319230-1422680573.png)


### 4.3 命令行演示
- 事务A执行命令给id=1的数据上悲观锁准备更新数据
- 事务B也去给id=1的数据上悲观锁准备更新数据
![](https://img2018.cnblogs.com/blog/1231979/201907/1231979-20190702022651572-685190599.png)
>这里之所以要以begin开始，是因为mysql是自提交的，所以要以begin开启事务，否则所有修改将被mysql自动提交。

![](https://img2018.cnblogs.com/blog/1231979/201907/1231979-20190702022703404-1281251.png)

>我们可以看到此时事务B再一直等待A释放锁。如果A长期不释放锁，那么最终事务B将会报错，这有兴趣的可以去尝试一下。

- 接着让事务A执行命令去修改数据，让猪肉脯的数量减一，然后查看修改后的数据，最后commit,结束事务。
![](https://img2018.cnblogs.com/blog/1231979/201907/1231979-20190703101713201-1202014754.png)
>经过第三步之后id=1的商品的数量已经为0

>当事务A执行完提交事务后,事务B中出现了什么
![](https://img2018.cnblogs.com/blog/1231979/201907/1231979-20190703101912378-1571546942.png)


### 4.4 乐观锁解决方案
```
	乐观锁的解决思路是:我们认为数据修改产生冲突的概率并不大，多个事务在修改数据的之前先查出版本号，在修改时把当前版本号作为修改条件，只会有一个事务可以修改成功，其他事务则会失败。
```

>乐观锁是通过版本号version来实现的.所以,我们需要给lock_goods表加上version字段,表变动后的结构如下:
![](https://img2018.cnblogs.com/blog/1231979/201907/1231979-20190703102143069-932124960.png)


A和B同时将id=1的数据查出来,然后A先买，A将id=1和version=0作为条件进行数据更新,即将数量-1，并且将版本号+1.
	此时版本号变为1，A完成了购买，最后B开始买，B也将id=1和version=0作为条件进行数据更新，但是更新完后，发现更新的数据行数为0，此时就说明已经有人改动过数据，此时就应该提示用户重新查询最新数据进行购买


### 4.5 悲观锁的加锁图解
![](https://img2018.cnblogs.com/blog/1231979/201907/1231979-20190703103912137-571150985.png)

### 4.6 命令行演示
- 事务A执行查询命令,事务B执行查询命令,两者查询的结果相同,此时A和B获取的是相同的数据
![](https://img2018.cnblogs.com/blog/1231979/201907/1231979-20190703104302677-1934086925.png)

- 事务A进行购买更新数据,然后再查询更新后的数据
![](https://img2018.cnblogs.com/blog/1231979/201907/1231979-20190703104536337-948161970.png)

- 事务B再进行购买更新数据，然后我们看影响行数和更新后的数据  
![](https://img2018.cnblogs.com/blog/1231979/201907/1231979-20190703104822006-871083146.png)

>可以看到最终修改行数为0，数据没有改变。此时就需要我们告知用户重新处理。


## 5 优缺点比较
### 5.1 悲观锁
- 优点：悲观锁利用数据库中的锁机制来实现数据变化的顺序执行,这是最有效的办法
- 缺点：一个事务用悲观锁对数据加锁之后其他事务将不能对加锁的数据进行除了查询以外的所有操作,如果该事务执行时间很长,那么其他事务将一直等待会影响我们系统的吞吐量。

### 5.2 乐观锁
- 优点：乐观锁不在数据库上加锁，任何事物都可以都对数据进行操作，在更新时才校验，这样就避免了悲观锁造成的吞吐量下降的劣势。
- 缺点：乐观锁因为是通过我们人为实现的，它仅仅适用于我们自己业务中，如果有外来事务插入，那么就可能发生错误。


## 6 应用场景
- 悲观锁：因为悲观锁会影响系统吞吐的性能，所以适合应用在写为居多的场景下
- 乐观锁：因为乐观锁就说为了避免悲观锁的弊端出现的，所以使用在读为居多的场景下

## 7.参考资料
[参考1](https://chenzhou123520.iteye.com/blog/1860954)
[参考2](https://baike.baidu.com/item/乐观锁/7146502)
