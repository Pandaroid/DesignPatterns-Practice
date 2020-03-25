package com.pandaroid.dps.singleton.lazy;

// 懒汉式内部类单例，没有用到 synchronized ，性能最优的一种写法，官方比较推荐的方式，解决 DCL 问题
public class LazyInnerClassSingleton implements ILazySingleton {

    // 测试内部类的加载顺序：如何理解内部类的执行逻辑呢？
    // 测试内部类的加载顺序：static 代码块是类加载的时候执行的
    /*static {
        System.out.format("[LazyInnerClassSingleton] static code block loaded!\n");
    }*/

    private LazyInnerClassSingleton() {}

    // 这里看起来像饿汉式单例，实际上是懒汉式单例
    // LazyInnerClassSingletonInstanceHolder 内部类里面的初始化逻辑需要等到外部类方法调用 getInstance 时才会执行
    // 巧妙的利用了 JLS Java 语言规范和 JVM 对类的加载后初始化的特性，利用了内部类的特性，即：一个类只有在被使用时才会初始化，而类初始化过程是非并行的
    // JVM 底层的执行逻辑逻辑，完美的避免了线程安全问题
    public static LazyInnerClassSingleton getInstance() {
        return LazyInnerClassSingletonInstanceHolder.lazyInnerClassSingletonInstance;
    }

    private static class LazyInnerClassSingletonInstanceHolder {
        // 测试内部类的加载顺序：static 代码块是类加载的时候执行的
        /*static {
            System.out.format("[LazyInnerClassSingleton LazyInnerClassSingletonInstanceHolder] inner class static code block loaded!\n");
        }*/

        private static final LazyInnerClassSingleton lazyInnerClassSingletonInstance = new LazyInnerClassSingleton();
    }

    // 测试内部类的加载顺序：如果执行下面的方法，按 JLS 和 JVM 特性，上面内部类的 static 代码块不会执行
    /*public static void doPrint() {
        System.out.format("[LazyInnerClassSingleton doPrint] inner class should not be loaded!\n");
    }*/
}
