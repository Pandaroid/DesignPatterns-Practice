package com.pandaroid.dps.singleton.threads;

import com.pandaroid.dps.singleton.lazy.ILazySingleton;
import com.pandaroid.dps.singleton.lazy.LazyInnerClassSingleton;
import com.pandaroid.dps.singleton.lazy.LazySimpleSingleton;
import com.pandaroid.dps.singleton.lazy.LazyDCLSingleton;

public class EtTest implements Runnable {

    @Override
    public void run() {
        ILazySingleton lazySimpleSingletonIns = LazySimpleSingleton.getInstance();
        System.out.println("[EtTest run] lazySimpleSingletonIns: " + lazySimpleSingletonIns.toString() + "; current Thread name: " + Thread.currentThread().getName() + "; current Thread ID: " + Thread.currentThread().getId());

        ILazySingleton lazyDCLSingletonIns = LazyDCLSingleton.getInstance();
        System.out.println("[EtTest run] lazyDCLSingletonIns: " + lazyDCLSingletonIns.toString() + "; current Thread name: " + Thread.currentThread().getName() + "; current Thread ID: " + Thread.currentThread().getId());

        ILazySingleton lazyInnerClassSingletonIns = LazyInnerClassSingleton.getInstance();
        System.out.println("[EtTest run] lazyInnerClassSingletonIns: " + lazyInnerClassSingletonIns.toString() + "; current Thread name: " + Thread.currentThread().getName() + "; current Thread ID: " + Thread.currentThread().getId());
        // 下面是测试静态类加载：上面的代码会看到 "[LazyInnerClassSingleton LazyInnerClassSingletonInstanceHolder] inner class static code block loaded!"，说明调用的时候初始化了内部类
        // 但是注释掉上面的代码，只执行下面的代码，看不到上面打印的日志，只能看到 doPrint() 打印的"[LazyInnerClassSingleton doPrint] inner class should not be loaded!"，说明没有调用的时候没有初始化内部类
        // LazyInnerClassSingleton.doPrint();
    }
}
