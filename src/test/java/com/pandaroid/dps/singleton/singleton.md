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

# 3. 反射暴力攻击单例解决方案及原理分析

单例类的构造方法虽然私有化，但是反射是可以霸王硬上弓的。在官方比较建议的静态内部类单例（以及前面的不如静态内部类建议的单例写法）中，虽然构造方法私有化 private 了，但是反射一样可以拿到并使用，直接获取到其构造方法。

```java
@Test
void testReflectSingleton() {
    Class<?> clazz = LazyInnerClassSingleton.class;
    try {
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        ILazySingleton lazyInnerClassSingletonReflect = (LazyInnerClassSingleton) constructor.newInstance();
        System.out.println("[LazySingletonTests testReflectSingleton] lazyInnerClassSingletonReflect.toString(): " + lazyInnerClassSingletonReflect.toString());
    } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
        e.printStackTrace();
    }
    ILazySingleton lazyInnerClassSingleton = LazyInnerClassSingleton.getInstance();
    System.out.println("[LazySingletonTests testReflectSingleton] lazyInnerClassSingleton.toString(): " + lazyInnerClassSingleton.toString());
    // [LazySingletonTests testReflectSingleton] lazyInnerClassSingletonReflect.toString(): com.pandaroid.dps.singleton.lazy.LazyInnerClassSingleton@2698dc7
    // [LazySingletonTests testReflectSingleton] lazyInnerClassSingleton.toString(): com.pandaroid.dps.singleton.lazy.LazyInnerClassSingleton@43d7741f
    // 可以看到单例被破坏了
}
```

如何简单的解决这个问题？在构造方法中进行判断和抛出异常：

```java
private LazyInnerClassSingleton() {
    if(null != LazyInnerClassSingletonInstanceHolder.lazyInnerClassSingletonInstance) {
        throw new RuntimeException("[LazyInnerClassSingleton] 单例类只能创建一个实例，不允许创建多个实例");
    }
}
```

```java
java.lang.reflect.InvocationTargetException
	at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
	at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
	at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
	at com.pandaroid.dps.singleton.LazySingletonTests.testReflectSingleton(LazySingletonTests.java:38)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:675)
	at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:125)
	at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:132)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestableMethod(TimeoutExtension.java:124)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestMethod(TimeoutExtension.java:74)
	at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:104)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:62)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:43)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:35)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:202)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:198)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:135)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:69)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:135)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)
[LazySingletonTests testReflectSingleton] lazyInnerClassSingleton.toString(): com.pandaroid.dps.singleton.lazy.LazyInnerClassSingleton@17baae6e
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:32)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:51)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:229)
	at org.junit.platform.launcher.core.DefaultLauncher.lambda$execute$6(DefaultLauncher.java:197)
	at org.junit.platform.launcher.core.DefaultLauncher.withInterceptedStreams(DefaultLauncher.java:211)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:191)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:128)
	at com.intellij.junit5.JUnit5IdeaTestRunner.startRunnerWithArgs(JUnit5IdeaTestRunner.java:69)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:33)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:230)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:58)
Caused by: java.lang.RuntimeException: [LazyInnerClassSingleton] 单例类只能创建一个实例，不允许创建多个实例
	at com.pandaroid.dps.singleton.lazy.LazyInnerClassSingleton.<init>(LazyInnerClassSingleton.java:14)
```

当有想要通过反射霸王硬上弓的意图时，我们会抛出非捕获性的 RuntimeException 异常，告诉调用者请正常访问。这样我们就防止了反射攻击，破坏单例。

# 4. 序列化破坏单例的原理及解决方案

通过序列化将单例写入磁盘，然后通过反序列化将单例读取回来，就能产生另一个单例对象，破坏单例。

```java
@Test
void testSerializableSingleton() {
    SerializableSingleton serializableSingleton2Read = null;
    SerializableSingleton serializableSingleton2Write = SerializableSingleton.getInstance();

    String fileName = "SerializableSingleton.obj";
    FileOutputStream fos = null;
    try {
        // 序列化写出
        fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(serializableSingleton2Write);
        oos.flush();
        oos.close();

        // 序列化读入
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        serializableSingleton2Read = (SerializableSingleton) ois.readObject();
        ois.close();
        
        // 输出结果
        System.out.println("[SingletonTests testSerializableSingleton] serializableSingleton2Write.toString(): " + serializableSingleton2Write.toString());
        System.out.println("[SingletonTests testSerializableSingleton] serializableSingleton2Read.toString(): " + serializableSingleton2Read.toString());
        System.out.println("[SingletonTests testSerializableSingleton] serializableSingleton2Write == serializableSingleton2Read : " + (serializableSingleton2Write == serializableSingleton2Read));
        // [SingletonTests testSerializableSingleton] serializableSingleton2Write.toString(): com.pandaroid.dps.singleton.serializable.SerializableSingleton@69379752
        // [SingletonTests testSerializableSingleton] serializableSingleton2Read.toString(): com.pandaroid.dps.singleton.serializable.SerializableSingleton@133e16fd
        // [SingletonTests testSerializableSingleton] serializableSingleton2Write == serializableSingleton2Read : false
        // 可以看到，通过序列化，我们破坏了官方比较推荐的单例写法。但是，这种破坏也是有解决方案的：readResolve
        // private Object readResolve() {
        //     return getInstance();
        // }
        // 再次执行，可以看到序列化无法破坏我们的单例了
        // [SingletonTests testSerializableSingleton] serializableSingleton2Write.toString(): com.pandaroid.dps.singleton.serializable.SerializableSingleton@69379752
        // [SingletonTests testSerializableSingleton] serializableSingleton2Read.toString(): com.pandaroid.dps.singleton.serializable.SerializableSingleton@69379752
        // [SingletonTests testSerializableSingleton] serializableSingleton2Write == serializableSingleton2Read : true
    } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
    }
}
```

解决方案：readResolve 。为什么 readResolve 可以解决序列化破坏单例的问题？

- `serializableSingleton2Read = (SerializableSingleton) ois.readObject();` 这段代码读入进来强转后的对象不是前面 `oos.writeObject(serializableSingleton2Write);` 写出到硬盘的单例对象了，那么读取的过程，我们进入 `readObject()` 源码看看

- 进去以后发现 `Object obj = readObject0(false);` 

- 进入 `readObject0` ，经过前面一系列判断后，有以下代码：

- ```java
  case TC_OBJECT:
      return checkResolve(readOrdinaryObject(unshared));
  ```

- `checkResolve` 这个方法，检查我们的对象 `SerializableSingleton` 是否有 readResolve 方法？在 `checkResolve` 之前，先 `readOrdinaryObject`

- ```java
  /**
   * Reads and returns "ordinary" (i.e., not a String, Class,
   * ObjectStreamClass, array, or enum constant) object, or null if object's
   * class is unresolvable (in which case a ClassNotFoundException will be
   * associated with object's handle).  Sets passHandle to object's assigned
   * handle.
   */
  private Object readOrdinaryObject(boolean unshared)
      throws IOException
  ```

- `readOrdinaryObject` 里面，有一段初始化？

- ```java
  obj = desc.isInstantiable() ? desc.newInstance() : null;
  ```

- 相当于是 class 的构造方法直接 new 一个对象

- ```java
  ObjectStreamClass desc = readClassDesc(false);
  ```

- 所以，我们的对象是否需要 new 一个实例，是否要看 `desc.isInstantiable()` 是否返回了 true ，所以导致 `desc.isInstantiable()` 新建了一个实例对象呢？在这里我向下看了一眼，发现下面有一句：

- ```java
  Object rep = desc.invokeReadResolve(obj);
  ```

- 好，先看一下 `desc.isInstantiable()` 

- ```java
  /**
   * Returns true if represented class is serializable/externalizable and can
   * be instantiated by the serialization runtime--i.e., if it is
   * externalizable and defines a public no-arg constructor, or if it is
   * non-externalizable and its first non-serializable superclass defines an
   * accessible no-arg constructor.  Otherwise, returns false.
   */
  boolean isInstantiable() {
      requireInitialized();
      return (cons != null);
  }
  ```

- 如果构造方法 cons 不为空，就给我们返回 true ，那么就会 `desc.isInstantiable()` 新建一个实例对象

  - 什么情况下返回 true ？
    - represented class 是 serializable / externalizable 并且能够被 serialization runtime 初始化
      - 比如能 externalizable 并且定义了 public 的无参构造方法
      - 或者 non-externalizable 并且它的第一个 non-serializable superclass 定义了一个 accessible no-arg constructor
  - 其他情况，返回 false

- 这里通过 debug ，确实执行了 `desc.isInstantiable()` ，重新分配内存创建对象 `SerializableSingleton@1859` 

- ```java
  if (obj != null &&
      handles.lookupException(passHandle) == null &&
      desc.hasReadResolveMethod())
  ```

- 上面是前面提到的 `Object rep = desc.invokeReadResolve(obj);` 代码进入的判断，也就是判断读进来的 desc 是否有 readResolve 方法

  - 如果有的话，就会执行 `desc.invokeReadResolve(obj)` ，这里调用的，就是我们前面写的 readResolve 方法

    - 通过反射调用

    - ```java
      readResolveMethod = getInheritableMethod(
          cl, "readResolve", null, Object.class);
      ```
    ```
    
    - 获取 readResolve 方法，null 表示没有任何参数，返回类型为 Object.class
    
    - ```java
      /**
       * Returns non-static, non-abstract method with given signature provided it
       * is defined by or accessible (via inheritance) by the given class, or
       * null if no match found.  Access checks are disabled on the returned
       * method (if any).
       */
      private static Method getInheritableMethod(Class<?> cl, String name,
                                                 Class<?>[] argTypes,
                                                 Class<?> returnType)
    ```
  
    - 调用 readResolve 
  
    - ```java
      /**
       * Invokes the readResolve method of the represented serializable class and
       * returns the result.  Throws UnsupportedOperationException if this class
       * descriptor is not associated with a class, or if the class is
       * non-serializable or does not define readResolve.
       */
      Object invokeReadResolve(Object obj)
          throws IOException, UnsupportedOperationException
      ```
  
    - ```java
      return readResolveMethod.invoke(obj, (Object[]) null);
      ```
  
    - invokeReadResolve 直接在 readResolveMethod 不为空的情况下 `return readResolveMethod.invoke(obj, (Object[]) null)`
  
  - 虽然我们上面初始化了，但是只要我们的单例类里面有 readResolve 方法，就会被调用，返回 readResolve 方法 return 的对象 rep 为 `SerializableSingleton@1795` ，此时 obj 是 `SerializableSingleton@1859` ，通过
  
  - ```java
    handles.setObject(passHandle, obj = rep);
    ```
  
  - 就将 obj 1859 赋值为了 rep 1795 ，也就是我们当前的单例实例对象

JDK 的设计充分考虑到了单例被破坏的情况，让我们可以在 readResolve 中按实际需要自定义提供读入的对象。

- readResolve 是 JDK 后面增加的一种机制
- 重写 readResolve 方法，从源码上看，只不过是覆盖了反序列化出来的对象
  - 即上面将 obj 1859 赋值为了 rep 1795 ，也就是将反序列化出来的新的实例，覆盖为乐我们当前的单例实例对象
  - 所以，单例对象还是被创建了两次，只不过第二次发生在 JVM 层面，并且被 JDK 提供的 readResolve 机制覆盖为了我们正确的单例对象
  - 发生在 JVM 层面，相对来说，比较安全
  - 而之前反序列化出来的对象会被 GC 回收

# 5. 《Effective Java》推荐的单例写法 Enum

枚举式单例，属于注册式单例。另一种注册式单例是使用容器缓存。

- 将每一个实例都缓存到统一的容器中，使用唯一标识获取实例
- 因为序列化会破坏单例，反射也会破坏单例，而 Enum 枚举式单例可以防止这些问题

为什么枚举式单例如此神奇？如何探究原理？

- 在 idea 的 plugin 中搜索反编译工具 Jad ，找到 IdeaJad ，并 install

  - 在 idea 的 target 目录下找到 EnumSingleton.class ，双击，进行反编译
  - 遗憾的是，在这里我发现并没有正确使用 Jad 反编译；换了其他 idea Jad 插件，也无法正常使用

- 于是，我老老实实在 Mac 上下载 Jad ，加入 PATH ，运行

  - bad cpu 出现了，不兼容我当前的 MacOS ？

- 好吧，我只能在 PD 里打开 Windows ，下载 Jad ，执行命令：

  - `.\jad Z:\Dev\GitRepos\MyGitHub\DesignPatterns
     -Practice\target\test-classes\com\pandaroid\dps\singleton\regist\EnumSingleton.class
     Parsing Z:\Dev\GitRepos\MyGitHub\DesignPatterns-Practice\target\test-classes\com\pandaroid\dps\singleton\regist\EnumSing
     leton.class... Generating EnumSingleton.jad`

  - ```powershell
    ls
    
    
       目录: \\Mac\Home\Downloads\jad158g.win
    
    
     Mode        LastWriteTime     Length Name
    
    ----        -------------     ------ ----
    
     ------     2006/7/2   16:12     495616 jad.exe
     ------    2020/3/28   1:44      1322 EnumSingleton.jad
     --r---    2006/5/21   3:05      6677 Readme.txt
    ```

  - 可以看到 EnumSingleton.jad 已经反编译出来了

- 反编译后的 EnumSingleton.jad 文件中，可以看到

- ```java
  // Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
  // Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
  // Source File Name:   EnumSingleton.java
  
  package com.pandaroid.dps.singleton.regist;
  
  import java.io.Serializable;
  
  public final class EnumSingleton extends Enum
      implements Serializable
  {
  
      public static EnumSingleton[] values()
      {
          return (EnumSingleton[])$VALUES.clone();
      }
  
      public static EnumSingleton valueOf(String name)
      {
          return (EnumSingleton)Enum.valueOf(com/pandaroid/dps/singleton/regist/EnumSingleton, name);
      }
  
      private EnumSingleton(String s, int i)
      {
          super(s, i);
      }
  
      public static EnumSingleton getInstance()
      {
          return ENUM_SINGLETON_INSTANCE;
      }
  
      public Object getData()
      {
          return data;
      }
  
      public void setData(Object data)
      {
          this.data = data;
      }
  
      public static final EnumSingleton ENUM_SINGLETON_INSTANCE;
      private Object data;
      private static final EnumSingleton $VALUES[];
  
      static 
      {
          ENUM_SINGLETON_INSTANCE = new EnumSingleton("ENUM_SINGLETON_INSTANCE", 0);
          $VALUES = (new EnumSingleton[] {
              ENUM_SINGLETON_INSTANCE
          });
      }
  }
  ```

- Jad 1.5.8g 就是最新版本，从 `Jad home page: http://www.kpdus.com/jad.html` 可以看到

  > Jul 02, 2006: Minor update to version 1.5.8g for Windows and Mac OS X (Intel).

- Mac 遇到 bad cpu 问题，还好 Windows 的版本能正常用

- 从反编译的 Jad 源码中，可以看到：

  - 并没有无参的私有的构造方法

  - ```java
    private EnumSingleton(String s, int i)
    {
        super(s, i);
    }
    ```

  - 单例实例是直接在 static 代码块中 new 出来的

  - ``````java
    static 
    {
        ENUM_SINGLETON_INSTANCE = new EnumSingleton("ENUM_SINGLETON_INSTANCE", 0);
        $VALUES = (new EnumSingleton[] {
            ENUM_SINGLETON_INSTANCE
        });
    }
    ``````

  - 这是饿汉式的写法，是线程安全的。那么它是如何避免序列化破坏单例的呢？还是从序列化反序列化的源码入手
  
  - ```java
    enumSingleton2Read = (EnumSingleton) ois.readObject();
    ```
  
  - 还是从 readObject() 入手
  
  - ```java
    Object obj = readObject0(false);
    ```
  
  - readObject 中，调用 readObject0 ，其中，有一个 TC_ENUM 分支
  
  - ```java
    case TC_ENUM:
        return checkResolve(readEnum(unshared));
    ```
  
  - readEnum 方法
  
  - ```java
    /**
     * Reads in and returns enum constant, or null if enum type is
     * unresolvable.  Sets passHandle to enum constant's assigned handle.
     */
    private Enum<?> readEnum(boolean unshared) throws IOException
    ```
  
  - 其中的关键代码：
  
  - ```java
    Enum<?> en = Enum.valueOf((Class)cl, name);
    result = en;
    ```
  
  - 最后 return result 。这里 Enum.valueOf 通过 cl 和 name 唯一确定了一个 Enum
  
  - ```java
    ObjectStreamClass desc = readClassDesc(false);
    if (!desc.isEnum()) {
        throw new InvalidClassException("non-enum class: " + desc);
    }
    ...
    Class<?> cl = desc.forClass();
    ```
  
  - 这里 cl 为 `class com.pandaroid.dps.singleton.regist.EnumSingleton` ，即 EnumSingleton.class
  
  - ``````java
    String name = readString(false);
    ``````
  
  - name 为 `ENUM_SINGLETON_INSTANCE` ，即单例的 name
  
  - ``````java
    enumSingleton2Write.name(): ENUM_SINGLETON_INSTANCE
    ``````
  
  - ```java
    /**
     * Returns the enum constant of the specified enum type with the
     * specified name.  The name must match exactly an identifier used
     * to declare an enum constant in this type.  (Extraneous whitespace
     * characters are not permitted.)
     *
     * <p>Note that for a particular enum type {@code T}, the
     * implicitly declared {@code public static T valueOf(String)}
     * method on that enum may be used instead of this method to map
     * from a name to the corresponding enum constant.  All the
     * constants of an enum type can be obtained by calling the
     * implicit {@code public static T[] values()} method of that
     * type.
     *
     * @param <T> The enum type whose constant is to be returned
     * @param enumType the {@code Class} object of the enum type from which
     *      to return a constant
     * @param name the name of the constant to return
     * @return the enum constant of the specified enum type with the
     *      specified name
     * @throws IllegalArgumentException if the specified enum type has
     *         no constant with the specified name, or the specified
     *         class object does not represent an enum type
     * @throws NullPointerException if {@code enumType} or {@code name}
     *         is null
     * @since 1.5
     */
    public static <T extends Enum<T>> T valueOf(Class<T> enumType,
                                                String name) {
        T result = enumType.enumConstantDirectory().get(name);
        if (result != null)
            return result;
        if (name == null)
            throw new NullPointerException("Name is null");
        throw new IllegalArgumentException(
            "No enum constant " + enumType.getCanonicalName() + "." + name);
    }
    ```
  
  - 通过枚举类 cl + 枚举的名字 name ，唯一确定了一个枚举的值，而这个枚举值被 JVM 保存着。通过 cl 和 name 就能确定被 JVM 保存着的这个枚举对象
  
  - 枚举类型单例不会被 newInstance 加载多次
  
  - 可以理解一下注册式单例的特点
  
    - ``````java
      T result = enumType.enumConstantDirectory().get(name);
      ``````
  
    - 如上，我已经注册在一个容器里了，直接通过 `Class<T> enumType,  String name` 唯一确定并拿出来之前注册的即可，不用再 newInstance 了，TC_ENUM 的反序列化有自己的流程逻辑

- 反射能否破坏枚举式单例呢？

  - 答案是不能，见我写的测试代码和注释说明：

  - ```java
    @Test
        void testReflectEnumSingleton() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
            Class<?> clazz = EnumSingleton.class;
    
            // Start: 没有无参的 Constructor
            /*Constructor constructorNoArgs = clazz.getDeclaredConstructor();
            constructorNoArgs.setAccessible(true);
            constructorNoArgs.newInstance();*/
            // java.lang.NoSuchMethodException: com.pandaroid.dps.singleton.regist.EnumSingleton.<init>()
            // End  : 没有无参的 Constructor
    
            // Start: 从反编译的源码中，我们看到有一个 Constructor
            // private EnumSingleton(String s, int i)
            // {
            //     super(s, i);
            // }
            Constructor[] constructors = clazz.getDeclaredConstructors();
            for (Constructor constructor : constructors) {
                System.out.println("[SingletonTests testReflectEnumSingleton] constructor: " + constructor);
                // [SingletonTests testReflectEnumSingleton] constructor: private com.pandaroid.dps.singleton.regist.EnumSingleton(java.lang.String,int)
                // 只有这一个 Constructor ，我们尝试用它来 newInstance
            }
            Constructor constructorStringInt = clazz.getDeclaredConstructor(String.class, int.class);
            constructorStringInt.setAccessible(true);
            EnumSingleton enumSingleton = (EnumSingleton) constructorStringInt.newInstance("ENUM_SINGLETON_INSTANCE", 0);
            System.out.println("[SingletonTests testReflectEnumSingleton] enumSingleton.getData(): " + enumSingleton.getData());
            // java.lang.IllegalArgumentException: Cannot reflectively create enum objects
            // 上面异常表示：不能用反射来创建枚举类型。为什么？答案就在 newInstance 方法的源码中
            // if ((clazz.getModifiers() & Modifier.ENUM) != 0)
            //     throw new IllegalArgumentException("Cannot reflectively create enum objects");
            // 对枚举又是特殊对待，如果修饰符是 Modifier.ENUM 枚举类型，则直接抛出异常
            // 因为在 JDK 中枚举的特殊性，无论在反编译后的 jad 源码中，还是反序列化、反射的源码中，我们都可以看到枚举非常特殊
            // 正是因为这些特殊和优待保护，让枚举式单例十分优雅，成为《Effective Java》推荐的一种单例写法
            // End  : 从反编译的源码中，我们看到有一个 Constructor
        }
    ```

以上，我们看到枚举非常特殊，从 JDK 层面就为它不被反射和序列化反序列化破坏而保驾护航，从而让枚举单例实现十分优雅。这也是《Effective Java》推荐我们使用枚举实现单例的原因。

枚举类实现单例符合注册式单例的特点，它也是饿汉式单例，也存在资源浪费问题。

- 但是 JDK 会对枚举进行优化，而且是从 JDK 底层去优化的，JDK 的开发人员已经考虑到了这些问题
- 所以《Effective Java》才会推荐，否则直接推荐饿汉式写法就行了

# 6. 容器式单例

容器式单例是另一种注册式单例，也是 Spring 中 IOC 容器使用的单例方式。

- 方便管理大量单例对象。
- 属于懒加载，懒汉式
- 存在线程安全问题，解决方式 synchronized 不是很优雅，逼死强迫症
  - DCL 经实测有明显优化效果，高并发下

```java
@Test
void testContainerSingletonConcurrency() {
    ConcurrentExecutor.execute(() -> {
        Object bean = ContainerSingleton.getBean("com.pandaroid.dps.singleton.EtTest");
        System.out.println("[SingletonTests testContainerSingletonConcurrency] bean: " + bean);
        /*try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }, 1000, 6);
    // 非线程安全，执行结果：
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@24d606ed
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@412414b2
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@a2514f8
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@1dd14e16
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@1dd14e16
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@1dd14e16
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@1dd14e16
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@1dd14e16
    // [ConcurrentExecutor execute] executeTimeMillis: 4
    // 加上 synchronized 以后，没有明显增加执行时间，但执行结果都是保持单例的一致的了
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
    // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
    // [ConcurrentExecutor execute] executeTimeMillis: 3
    // 线程安全，executeCount 1000 ，耗时：
    // [ConcurrentExecutor execute] executeTimeMillis: 71
    // 线程安全，executeCount 1000 ，DCL ，耗时：
    // [ConcurrentExecutor execute] executeTimeMillis: 47
    // 明显 DCL 还是有作用的，逼死强迫症啊
}
```

```java
package com.pandaroid.dps.singleton.regist.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ConcurrentExecutor {
    /**
     * 用于并发执行 runHandler 任务，控制执行总数 executeCount ，和可同时进入执行任务的并发数 concurrentCount
     * @param runHandler
     * @param executeCount
     * @param concurrentCount
     */
    public static void execute(final RunHandler runHandler, int executeCount, int concurrentCount) {
        // startTimeMillis
        long startTimeMillis = System.currentTimeMillis();
        // executorService
        ExecutorService executorService = Executors.newCachedThreadPool();
        // 用 Semaphore 控制 concurrentCount 设置的并发数量
        final Semaphore semaphore = new Semaphore(concurrentCount);
        // 用 CountDownLaunch 来实现 executeCount 执行完毕后的 executorService 释放
        final CountDownLatch countDownLatch = new CountDownLatch(executeCount);
        for(int i = 0; i < executeCount; i++) {
            executorService.execute(() -> {
                try {
                    // 控制并发不超过 concurrentCount ，超过的会在此阻塞等待直到其他线程 release 并 acquire 到许可以后再执行
                    semaphore.acquire();
                    runHandler.handler();
                    // 释放许可
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        // 在此通过 countDownLatch 阻塞主线程，直到 executeCount 的子线程执行完毕后（闭锁值为 0 时），阻塞释放，继续向下执行 executorService 的 shutDown
        // The {@link #shutdown} method will allow previously submitted tasks to execute before terminating
        // 所以我认为这里使用 countDownLatch 是没有必要的，但是 shutDown 在某些场景下有必要，因为：
        // An unused {@code ExecutorService} should be shut down to allow reclamation of its resources.
        // 经测试，不使用 countDownLatch ，执行 shutdown ，任务不能执行完全部 executeCount ，如果使用 shutdownNow ，会抛出 java.lang.InterruptedException: sleep interrupted
        // 因为我们用 Thread.sleep() 来辅助测试
        // 所以，这里使用 countDownLatch 是有必要的，但我还是怀疑这里是因为主线程提前结束了，所以子线程没执行完导致异常，于是我试试主线程 sleep 足够长时间
        // 使用 shutdownNow 还是会 java.lang.InterruptedException: sleep interrupted
        // 但是，使用 shutdown 最终所有 executeCount 个线程都执行完了，得证
        // 所以，这里使用 countDownLatch 是更高效的解决了这个问题，而不用 sleep 足够长时间，executeCount 个都执行完了，阻塞解除，安全的 shutdown
        // 否则，主线程结束了，子线程还没执行完，就会抛出异常
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // shutDown executorService
        executorService.shutdown();
        // executorService.shutdownNow();
        /*try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        // 执行时间打印
        System.out.println("[ConcurrentExecutor execute] executeTimeMillis: " + (System.currentTimeMillis() - startTimeMillis));
    }
}
```

```java
package com.pandaroid.dps.singleton.regist.concurrency;

public interface RunHandler {
    void handler();
}
```

```java
package com.pandaroid.dps.singleton.regist;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContainerSingleton {
    private ContainerSingleton() {}

    private static Map<String, Object> ioc = new ConcurrentHashMap<>();

    public static Object getBean(String className) {
        if(!ioc.containsKey(className)) {
            synchronized (ioc) {
                if(!ioc.containsKey(className)) {
                    try {
                        Class clazz = Class.forName(className);
                        Object obj = clazz.newInstance();
                        ioc.put(className, obj);
                    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return ioc.get(className);
    }
}
```

# 7. ThreadLocal 单例

最后一种单例写法，用处也很广，即 ThreadLocal 单例。

- 保证线程内唯一
- 天生线程安全

比如用在数据库框架中，做 ThreadLocal 缓存、数据库连接 Holder 、单例 bean 基于线程隔离变量副本、Session 管理、多数据源动态切换等。

- ThreadLocal 的线程安全是通过每个线程自己的变量副本实现的，不是线程间的线程安全
- 是通过线程隔离达到的“伪线程安全”
- 根据查阅的资料，ThreadLocal 也是注册式单例，因为跟容器式单例类似，ThreadLocalMap 也是一个容器
- initialValue() 是一个 protected 方法，一般是用来在使用时进行重写的，它是一个延迟加载方法，所以 ThreadLocal 单例也是懒汉式

原理（ThreadLocal.java 源码）：

```java
/**
 * Returns the value in the current thread's copy of this
 * thread-local variable.  If the variable has no value for the
 * current thread, it is first initialized to the value returned
 * by an invocation of the {@link #initialValue} method.
 *
 * @return the current thread's value of this thread-local
 */
public T get() {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null) {
        ThreadLocalMap.Entry e = map.getEntry(this);
        if (e != null) {
            @SuppressWarnings("unchecked")
            T result = (T)e.value;
            return result;
        }
    }
    return setInitialValue();
}

/**
 * Get the map associated with a ThreadLocal. Overridden in
 * InheritableThreadLocal.
 *
 * @param  t the current thread
 * @return the map
 */
ThreadLocalMap getMap(Thread t) {
		return t.threadLocals;
}

/* ThreadLocal values pertaining to this thread. This map is maintained
 * by the ThreadLocal class. */
ThreadLocal.ThreadLocalMap threadLocals = null;
```

- ThreadLocalMap 是从当前线程 Thread.currentThread() 当中获取的 threadLocals ，我理解为每一个线程对象都有一个 `ThreadLocal.ThreadLocalMap threadLocals` ，用于保存自己的变量副本（maintaining thread local values）

- 如果获取成功，则返回 value 值。如果 map 为空，则调用 setInitialValue 方法返回 value ，这里返回的就是我们在 ThreadLocalSingleton 中覆盖的 new ThreadLocalSingleton()。

- ```java
  /*private static final ThreadLocal<ThreadLocalSingleton> threadLocalSingletonInstance = new ThreadLocal<ThreadLocalSingleton>() {
      @Override
      protected ThreadLocalSingleton initialValue() {
          return new ThreadLocalSingleton();
      }
  };*/
  private static final ThreadLocal<ThreadLocalSingleton> threadLocalSingletonInstance = new ThreadLocal<ThreadLocalSingleton>().withInitial(() -> {
      System.out.println("[ThreadLocalSingleton withInitial] Thread.currentThread(): " + Thread.currentThread());
      return new ThreadLocalSingleton();
  });
  ```

- ```java
  /**
   * Variant of set() to establish initialValue. Used instead
   * of set() in case user has overridden the set() method.
   *
   * @return the initial value
   */
  private T setInitialValue() {
      T value = initialValue();
      Thread t = Thread.currentThread();
      ThreadLocalMap map = getMap(t);
      if (map != null)
          map.set(this, value);
      else
          createMap(t, value);
      return value;
  }
  ```

- ```java
  /**
   * ThreadLocalMap is a customized hash map suitable only for
   * maintaining thread local values. No operations are exported
   * outside of the ThreadLocal class. The class is package private to
   * allow declaration of fields in class Thread.  To help deal with
   * very large and long-lived usages, the hash table entries use
   * WeakReferences for keys. However, since reference queues are not
   * used, stale entries are guaranteed to be removed only when
   * the table starts running out of space.
   */
  static class ThreadLocalMap {
  
  		/**
       * The entries in this hash map extend WeakReference, using
       * its main ref field as the key (which is always a
       * ThreadLocal object).  Note that null keys (i.e. entry.get()
       * == null) mean that the key is no longer referenced, so the
       * entry can be expunged from table.  Such entries are referred to
       * as "stale entries" in the code that follows.
       */
      static class Entry extends WeakReference<ThreadLocal<?>> {
      		/** The value associated with this ThreadLocal. */
          Object value;
  
          Entry(ThreadLocal<?> k, Object v) {
          		super(k);
            	value = v;
          }
      }
    
    	...
    
  		/**
   		 * Construct a new map initially containing (firstKey, firstValue).
   		 * ThreadLocalMaps are constructed lazily, so we only create
   		 * one when we have at least one entry to put in it.
   		 */
  		ThreadLocalMap(ThreadLocal<?> firstKey, Object firstValue) {
      		table = new Entry[INITIAL_CAPACITY];
      		int i = firstKey.threadLocalHashCode & (INITIAL_CAPACITY - 1);
      		table[i] = new Entry(firstKey, firstValue);
      		size = 1;
      		setThreshold(INITIAL_CAPACITY);
  		}
    
    	...
        
  }
  ```

- ThreadLocal 可以更简单、更高效的解决并发问题
- 注意当前线程的 `ThreadLocal.ThreadLocalMap threadLocals` 中的 Entry 数组，每一个 Entry 的 key 是当前 ThreadLocal<?> 对象
- ThreadLocal 类允许我们创建只能被同一个线程读写的变量。因此，如果一段代码含有一个 ThreadLocal 变量的引用，即使两个线程同时执行这段代码，它们也无法访问到对方的 ThreadLocal 变量

在每个线程 Thread 内部有一个 ThreadLocal.ThreadLocalMap 类型的成员变量 threadLocals ，这个 threadLocals 就是用来存储实际的变量副本的，键值为当前 ThreadLocal 变量，value 为变量副本（即 T 类型的变量）。初始时，在 Thread 里面，threadLocals 为空，当通过 ThreadLocal 变量调用 get() 方法或者 set() 方法，就会对 Thread 类中的 threadLocals 进行初始化，并且以当前 ThreadLocal 变量为键值，以 ThreadLocal 要保存的副本变量为 value ，存到 threadLocals 。 然后在当前线程里面，如果要使用副本变量，就可以通过 get 方法在 threadLocals 里面查找。

1. 实际的通过 ThreadLocal 创建的副本是存储在每个线程自己的 threadLocals 中的
2. 为何 threadLocals 的类型 ThreadLocalMap 的键值为 ThreadLocal 对象，因为每个线程中可有多个 threadLocal 变量
3. 在进行 get 之前，必须先 set ，否则会报空指针异常；如果想在 get 之前不需要调用 set 就能正常访问的话，必须重写 initialValue() 方法。 因为在上面的代码分析过程中，我们发现如果没有先 set 的话，即在 map 中查找不到对应的存储，则会通过调用 setInitialValue 方法返回 i ，而在 setInitialValue 方法中，有一个语句是 T value = initialValue() ， 而默认情况下，initialValue 方法返回的是 null 。

# 8. 总结

单例模式的优点：

- 在内存中只有一个实例，减少了内存开销，避免频繁创建回收 GC
- 可以避免对资源的多重占用
- 设置全局访问点，严格控制访问

单例模式的缺点：

- 没有接口，扩展困难
- 如果要扩展单例对象，只有修改代码，没有其他途径

单例模式的知识重点总结：

- 私有化构造器
- 保证线程安全
- 延迟加载
- 防止序列化和反序列化破坏单例
- 防御反射攻击单例