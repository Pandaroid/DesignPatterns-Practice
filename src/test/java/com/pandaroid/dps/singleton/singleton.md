# 1. 单例模式的应用场景

单例模式（Singleton Pattern）是指确保一个类在任何情况下都绝对只有一个实例，并提供一个全局访问点。

- 隐藏其所有构造方法：构造方法私有
- 提供一个全局访问点，入口
- 属于创建型模式

单例应用场景：

- ServletContext 单实例、多线程
- ServletConfig 配置只能一个
- ApplicationContext
- DBPool

# 2. 常见的单例模式写法

常见写法：

- 饿汉式单例
- 懒汉式单例
- 注册式单例
- ThreadLocal 单例

## 2.1 饿汉式单例

在单例类首次加载时就创建实例。

- 不管用不用，先吃饱再说

缺点：

- 浪费内存空间
  - 如果初始化了，却没有使用
  - 这样的单例数量越多，浪费越大
- 只适合小范围、大概率会使用此单例的情况下使用

## 2.2 懒汉式单例

被外部类调用时才创建实例。单例类加载时，并不初始化。

DCL 对 instance 进行了两次 null 判断，第一层判断主要是为了避免不必要的同步，第二层的判断则是为了在 null 的情况下创建实例。 

DCL 单例优点：

- 资源利用率高：不执行 getInstance 就不会被实例化
- 多线程下效率高：双重检查加锁（Double Check Lock）避免每次调用 getInstance 方法时都同步

DCL 单例缺点：

- 第一次初始化时比较慢，并行时后面的线程需要排队等待第一次初始化
- 有 bug ：DCL 失效问题
  - 主要是因为 JLS 规范，编译器为了提高效率而进行了指令重排，**只要认为单线程下没问题**，就可以进行乱序写入，以保证不让 CPU 指令流水线中断
  - 因此，**在多线程的情况下**，DCL 下 lazySimpleSingletonIns 可能得到一个不为 null 但构造不完全的对象，有序性无法保证

为了提高代码的执行效率，JVM 会将执行频率高的代码编译成机器码，而对于频率不高的代码则仍然采用解释执行。常见的编译优化方式有：

- 方法内联：免去参数、返回值传递过程

- 去虚拟化：接口的方法只有一个实现类，进行方法内联

- 冗余消除：运行时去掉无用代码

- 还有一些编译优化根据“逃逸分析”技术
  - 标量替换：User u = new User("zhang3", 18) 
    - String n = "zhang3" int age = 18 ，节省内存
  - 栈上分配：逃逸对象直接在栈上分配，快速，GC 及时
  - 锁消除：去掉不必要的锁同步

```java
lazyDCLSingletonIns = new LazyDCLSingleton();
```

上述代码运行到底发生了什么？

- memory = allocate(); 	                     // 1：分配对象的内存空间
- ctorInstance(memory);                      // 2：初始化对象：实例化单例对象，初始化属性参数等

- lazyDCLSingletonIns = memory;      // 3：设置 lazyDCLSingletonIns 指向刚分配的内存地址

然后用户的代码会对 lazyDCLSingletonIns 进行初次访问，极小的概率会出现 DCL 失效 bug 。

上面的伪代码中 2 、3 步可能发生指令重排，变为 3 、2 ，导致用户初次访问时，获得一个不为 null 但是未初始化完全的对象。

解决方案：可以通过内部类单例或 volatile 保证有序性。

- 官方比较推荐内部类单例方案
  - 原理：一个类只有在被使用时才会初始化，而类初始化过程是非并行的，这些都由 JLS 能保证。
  - 优点：
    - 没有重量级锁 synchronized ，性能更高
    - 用到的是类加载初始化的特性
      - 外部类加载时，会去加载小类，当 `getInstance()` 方法被调用时，内部类被使用，此时，JVM 对内部类进行非并行的初始化
      - 初始化时，`LazyInnerClassSingletonInstanceHolder.lazyInnerClassSingletonInstance` 是 `private static final LazyInnerClassSingleton` 的，所以用 LazyInnerClassSingleton 的实例进行了初始化
      - 由 JLS 保证的非并行初始化完毕以后，`getInstance()` 方法继续返回单例对象，多线程调用 `getInstance()` 在这里也是由 JLS 保证线程安全的
      - 符合懒汉式单例特征，并且利用 JVM 类加载初始化的特性，保证了线程安全
- volatile 关键字
  - Java 5 以后的版本，可以利用 volatile 关键字。
    - Why ？
    - 在 Java 5 以前，volatile 原语不怎么强大，只能保证对象的可见性
    - 但在 Java 5 之后，volatile 语义加强了，被 volatile 修饰的对象，将禁止该对象上的读写指令重排序
    - 这样，就保证了线程 B 读对象时，已经初始化完全了
  - 由于 JVM 存在乱序执行功能，所以可能在上面第 2 步还没执行时就先执行了第 3 步，如果此时再被切换到其他线程上，由于执行了 3 ，lazyDCLSingletonIns 已经非空了，会被直接拿出来用，但此时 lazyDCLSingletonIns 中的对象未完成 2 中的正常初始化，这样的话，就会出现异常。这个就是著名的DCL失效问题。

    - 不过在 JDK 1.5 之后，官方也发现了这个问题，故而具体化了 volatile ，即在 JDK 1.6 及以后，只要定义为 `private volatile static LazyDCLSingleton lazyDCLSingletonIns = null;` 就可解决 DCL 失效问题。
    - volatile 确保 lazyDCLSingletonIns 每次均在主内存中读取，这样虽然会牺牲一点效率，但也无伤大雅。

### 理解静态内部类的执行逻辑

静态内部类的优点是：外部类加载时并不需要立即加载内部类，内部类不被加载则不去初始化 LazyInnerClassSingletonInstanceHolder.lazyInnerClassSingletonInstance ，故而不占内存。即当 LazyInnerClassSingleton 第一次被加载时，并不需要去加载 LazyInnerClassSingletonInstanceHolder.lazyInnerClassSingletonInstance ，只有当 getInstance() 方法第一次被调用时，才会去初始化 LazyInnerClassSingletonInstanceHolder.lazyInnerClassSingletonInstance ，第一次调用 getInstance() 方法会导致虚拟机加载 LazyInnerClassSingletonInstanceHolder 类，这种方法不仅能确保线程安全，也能保证单例的唯一性，同时也延迟了单例的实例化。

那么，静态内部类又是如何实现线程安全的呢？首先，我们先了解下类的加载时机。

类加载时机：Java 虚拟机在有且仅有的 5 种场景下会对类进行初始化。

1. 遇到 new 、getstatic 、setstatic 或者 invokestatic 这 4 个字节码指令时，对应的 Java 代码场景为：
   1. new 一个关键字或者一个实例化对象时
   2. 读取或设置一个静态字段时（final 修饰、已在编译期把结果放入常量池的除外）
   3. 调用一个类的静态方法时
2. 使用 java.lang.reflect 包的方法对类进行反射调用的时候，如果类没进行初始化，需要先调用其初始化方法进行初始化。
3. 当初始化一个类时，如果其父类还未进行初始化，会先触发其父类的初始化。
4. 当虚拟机启动时，用户需要指定一个要执行的主类（包含 main() 方法的类），虚拟机会先初始化这个类。
5. 当使用 JDK 1.7 等动态语言支持时，如果一个 java.lang.invoke.MethodHandle 实例最后的解析结果 REF_getStatic 、REF_putStatic 、REF_invokeStatic 的方法句柄，并且这个方法句柄所对应的类没有进行过初始化，则需要先触发其初始化。

这 5 种情况被称为是类的主动引用，注意，这里《虚拟机规范》中使用的限定词是“有且仅有”，那么，除此之外的所有引用类都不会对类进行初始化，称为被动引用。静态内部类就属于被动引用的行列。

我们再回头看下 getInstance() 方法，调用的是 `LazyInnerClassSingletonInstanceHolder.lazyInnerClassSingletonInstance` ，取的是 LazyInnerClassSingletonInstanceHolder 里的 lazyInnerClassSingletonInstance 对象，跟上面那个 DCL 方法不同的是，getInstance() 方法并没有多次去 new 对象，故不管多少个线程去调用 getInstance() 方法，取的都是同一个 lazyInnerClassSingletonInstance 对象，而不用去重新创建。当 getInstance() 方法被调用时，LazyInnerClassSingletonInstanceHolder 才在 LazyInnerClassSingleton 的运行时常量池里，把符号引用替换为直接引用，这时静态对象 lazyInnerClassSingletonInstance 也真正被创建，然后再被 getInstance() 方法返回出去，这点同饿汉模式（但实际是懒汉模式）。那么 lazyInnerClassSingletonInstance 在创建过程中又是如何保证线程安全的呢？在《深入理解 JAVA 虚拟机》中，有这么一句话：

> 虚拟机会保证一个类的 <clinit>() 方法在多线程环境中被正确地加锁、同步，如果多个线程同时去初始化一个类，那么只会有一个线程去执行这个类的<clinit>()方法，其他线程都需要阻塞等待，直到活动线程执行<clinit>()方法完毕。如果在一个类的<clinit>()方法中有耗时很长的操作，就可能造成多个进程阻塞(需要注意的是，其他线程虽然会被阻塞，但如果执行<clinit>()方法后，其他线程唤醒之后不会再次进入<clinit>()方法。同一个加载器下，一个类型只会初始化一次。)，在实际应用中，这种阻塞往往是很隐蔽的。
>

故而，可以看出INSTANCE在创建过程中是线程安全的，所以说静态内部类形式的单例可保证线程安全，也能保证单例的唯一性，同时也延迟了单例的实例化。

那么，是不是可以说静态内部类单例就是最完美的单例模式了呢？其实不然，静态内部类也有着一个致命的缺点，就是传参的问题，由于是静态内部类的形式去创建单例的，故外部无法传递参数进去，例如Context这种参数，所以，我们创建单例时，可以在静态内部类与DCL模式里自己斟酌。

# 3. 保证线程安全的单例模式策略



# 4. 反射暴力攻击单例解决方案及原理分析



# 5. 序列化破坏单例的原理及解决方案

