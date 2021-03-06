## Synchronized 有几种用法
我们都知道Synchronized 是线程安全同步用的，大部分程序可能只会用到同步方法上面。其实Synchronized 可以用到其他场合


### 同步普通方法
这个也是我们用的很多的，只要涉及线程安全，上来就是给方法来个同步锁。这种方法使用虽然最简单，但是只能作用在单例上面，如果不是单例，同步方法锁将失效。

```
/**
 * 用在普通方法
 */
private synchronized void synchronizedMethod() {
    System.out.println("synchronizedMethod");
    try {
        Thread.sleep(2000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```

>此时，同一个实例只有一个线程能获取锁进入这个方法。


### 同步静态方法

同步静态方法，不管你有多少个实例，同事只有一个线程能获取锁进入这个方法。

```
/**
 * 用在静态方法
 */
private synchronized static void synchronizedStaticMethod() {
    System.out.println("synchronizedStaticMethod");
    try {
        Thread.sleep(2000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```

>同步静态方法是类级别的锁，一旦任何一个线程进入这个方法，其他所有线程将无法访问这个类的任何同步类锁的方法。


### 同步类
下面提供了两种同步类的方法，锁住效果和同步静态方法一样，都是类级别的锁，同时只有一个线程能访问带有同步类锁的方法。

```
/**
 * 用在类
 */
private void synchronizedClass() {
    synchronized (TestSynchronized.class) {
        System.out.println("synchronizedClass");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 用在类
 */
private void synchronizedGetClass() {
    synchronized (this.getClass()) {
        System.out.println("synchronizedGetClass");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

>这里的两种用法是同步块的用法，这里表示只有获取到这个类锁才能进入这个代码块。


### 同步this实例
这也是同步块的用法，表示锁住整个当前对象实例，只有获取到这个实例的锁才能进入这个方法。

```
/**
 * 用在this
 */
private void synchronizedThis() {
    synchronized (this) {
        System.out.println("synchronizedThis");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

>用法和同步普通方法锁一样，都是锁住整个当前实例。


### 同步对象实例

这也是同步块的用法，和上面的锁住当前实例一样，这里表示锁住整个 LOCK 对象实例，只有获取到这个 LOCK 实例的锁才能进入这个方法。


```
/**
 * 用在对象
 */
private void synchronizedInstance() {
    synchronized (LOCK) {
        System.out.println("synchronizedInstance");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

>另外，类锁与实例锁不相互阻塞，但相同的类锁，相同的当前实例锁，相同的对象锁会相互阻塞。