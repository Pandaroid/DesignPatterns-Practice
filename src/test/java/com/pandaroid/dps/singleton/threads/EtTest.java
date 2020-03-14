package com.pandaroid.dps.singleton.threads;

import com.pandaroid.dps.singleton.lazy.ILazySingleton;
import com.pandaroid.dps.singleton.lazy.LazySimpleSingleton;
import com.pandaroid.dps.singleton.lazy.LazySyncSingleton;

public class EtTest implements Runnable {

    @Override
    public void run() {
        ILazySingleton lazySimpleSingletonIns = LazySimpleSingleton.getInstance();
        System.out.println("[EtTest run] lazySimpleSingletonIns: " + lazySimpleSingletonIns.toString() + "; current Thread name: " + Thread.currentThread().getName() + "; current Thread ID: " + Thread.currentThread().getId());

        ILazySingleton lazySyncSingletonIns = LazySyncSingleton.getInstance();
        System.out.println("[EtTest run] lazySyncSingletonIns: " + lazySyncSingletonIns.toString() + "; current Thread name: " + Thread.currentThread().getName() + "; current Thread ID: " + Thread.currentThread().getId());
    }
}
