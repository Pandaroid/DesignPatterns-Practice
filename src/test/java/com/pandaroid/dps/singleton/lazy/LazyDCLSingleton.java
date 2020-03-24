package com.pandaroid.dps.singleton.lazy;

public class LazyDCLSingleton implements ILazySingleton {
    // 这里增加 volatile ，解决 DCL 失效（指令重排序 + 多线程）问题
    private volatile static LazyDCLSingleton lazyDCLSingletonIns = null;

    private LazyDCLSingleton() {}

    public static LazyDCLSingleton getInstance() {
        if(null == lazyDCLSingletonIns) {
            synchronized (LazyDCLSingleton.class) {
                if(null == lazyDCLSingletonIns) {
                    lazyDCLSingletonIns = new LazyDCLSingleton();
                    // DCL 失效问题，DCL 单例模式因为指令重排失效（有序性）
                    // 根据 JLS 规范（Java Language Specification），DCL 下 lazyDCLSingletonIns 可能得到一个不为 null 但构造不完全的对象
                    // 原因：这段代码执行的时候，会转换为 JVM 指令执行，而编译器为了提高效率而进行了指令重排，只要认为单线程下没问题，就可以进行乱序写入，以保证不让 CPU 指令流水线中断
                    // 原子性、有序性、可见性，其中，有序性会被破坏
                    // 1、分配内存给这个对象
                    // 2、初始化对象
                    // 3、将初始化好的对象和内存地址建立关联，赋值
                    // 4、用户初次访问
                    // 解决方案：可以通过内部类单例或 volatile 保证有序性。官方比较推荐内部类单例方案
                }
            }
        }
        return lazyDCLSingletonIns;
    }

}
