## 认识JVM
### 一、JVM的基本介绍
JVM是 `Java Virtual Machine` 的缩写它是一个虚构出来的计算机通过在实际的计算机上仿真模拟各类计算机功能实现···

JVM其实就类似于一台小电脑运行Windows或者Linux这些操作系统环境下即可。它直接和操作系统进行交互，与硬件不直接交互，可操作系统可以帮我们完成和硬件进行交互的工作。
![](https://img2018.cnblogs.com/blog/1231979/202002/1231979-20200205170026039-1407116399.png)

#### 1.1 Java文件是如何被运行的
比如我们现在写了一个 `HelloWorld.java`,那是不是就类似于一个文本文件，只是这个文本文件它写的都是英文，而且有一定的缩进而已。

那我们的 JVM 是不认识文本文件的，所以它需要一个 `编译` ，让其成为一个它会读`二进制`文件的` HelloWorld.class`


- ① 类加载器

如果JVM 想要执行这个 `.class ` 文件，我们需要将其装进一步类加载器中，它就像一个搬运工一样，会把所有的 .class 文件全部搬进JVM里面来。
![](https://img2018.cnblogs.com/blog/1231979/202002/1231979-20200205170216015-1677463509.png)


- ② 方法区
`方法区`是用于存放类似于元数据信息方面的数据的，比如类信息，常量，静态变量，编译后代码···等

类加载器将 .class 文件搬过来就是先丢到这一块上


- ③ 堆
`堆`主要放了一些存储的数据，比如对象实例，数组...等，它和方法区都同属于`线程共享区域`，也就是说它们都是`线程不安全`的。


- ④ 栈
`栈`这是我们的代码运行空间。我们编写的每一个方法都会放到`栈`里面运行。


- ⑤ 程序计数器

主要就是完成一个加载工作，类似于一个指针一样的，指向下一行我们需要执行的代码。和栈一样，都是 `线程独享` 的，就是说每一个线程都会有自己对应的一块区域而不会存在并发和多线程的问题。

![](https://img2018.cnblogs.com/blog/1231979/202002/1231979-20200205171448685-1777235616.png)


#### 小总结
- Java文件经过编译后变成 .class 字节码文件
- 字节码文件通过类加载器被搬运到JVM虚拟机中
- 虚拟机主要的 5 大块：方法区，堆都为线程共享区域，有线程安全问题，栈和本地方法栈和计数器都是独享区域，不能存在线程安全问题，而JVM 的调优主要就是围绕堆，栈两大块进行


#### 1.2 简单的代码例子
一个简单的学生类(包括name 属性 和有参构造方法)

一个main 方法

##### 执行main 方法的步骤如下：
- 编译好 Test.java 后得到 Test.class 后，执行 Test.class，系统会启动一个 JVM 进程，从 classpath 路径中找到一个名为 Test.class 的二进制文件，将 Test 的类信息加载到运行时数据区的方法区内，这个过程叫做 Test 类的加载

- JVM 找到 Test 的主程序入口，执行main方法

- 这个main中的第一条语句为 `Student student = new Student("tellLegend");`，就是让 JVM 创建一个Student对象，但是这个时候方法区中是没有 Student 类的信息的，所以 JVM 马上加载 Student 类，把 Student 类的信息放到方法区中

- 加载完Student 类后，JVM 在堆中为一个新的 Student 实例分配内存，然后调用构造函数初始化 Student 实例，这个 Student 实例持有 `指向方法区中的 Student 类的类型信息` 的引用

- 执行`student.sayName();`时，JVM 根据 student 的引用找到 student 对象，然后根据 student 对象持有的引用定位到方法区中 student 类的类型信息的方法表，获得` sayName() `的字节码地址。

- 执行`sayName()`方法



### 二、类加载器的介绍
之前也提到了它是负责加载.class文件的，它们在文件开头会有特定的文件标示，将class文件字节码内容加载到内存中，并将这些内容转换成方法区中的运行时数据结构，并且`ClassLoader`只负责class文件的加载，而是否能够运行则由 `Execution Engine` 来决定



#### 2.1 类加载器的流程
从类被加载到虚拟机内存中开始，到释放内存总共有7个步骤：`加载，验证，准备，解析，初始化，使用，卸载`。其中**验证，准备，解析**三个部分统称为**连接**。


##### 2.1.1 加载
- 将class文件加载到内存
- 将静态数据结构转化成方法区中运行的数据结构
- 在堆中生成一个代表这个类的 ` java.lang.Class` 对象作为数据访问的入口。


##### 2.1.2 连接
- **验证:** 确保加载的类符合JVM 规范和安全，保证被校验类的方法在运行时不会做出危害虚拟机的事件，其实就是一个安全检查
- **准备**:为static 变量在方法区中分配内存空间，设置变量的初始值，例如:`static int a = 3; `

>注意：准备阶段只设置类中的静态变量（方法区中），不包括实例变量（堆内存中），实例变量是对象初始化时赋值的


- **解析:**虚拟机将常量池内的符号引用替换为直接引用的过程（符号引用比如我现在import java.util.ArrayList这就算符号引用，直接引用就是指针或者对象地址，注意引用对象一定是在内存进行）


##### 2.1.3 初始化
初始化其实就是一个赋值的操作，它会执行一个类构造器的`<clinit>()`方法。由编译器自动收集类中所有变量的赋值动作，此时准备阶段时的那个 static int a = 3 的例子，在这个时候就正式赋值为3


##### 2.1.4 卸载
GC将无用对象从内存中卸载


![](https://img2018.cnblogs.com/blog/1231979/202002/1231979-20200204131111842-1434916267.png)
