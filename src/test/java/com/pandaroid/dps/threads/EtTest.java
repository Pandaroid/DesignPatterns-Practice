package com.pandaroid.dps.threads;

import com.pandaroid.dps.singleton.ILazySingleton;
import com.pandaroid.dps.singleton.LazySimpleSingleton;
import com.pandaroid.dps.singleton.LazySyncSingleton;

public class EtTest implements Runnable {

    @Override
    public void run() {
        ILazySingleton lazySimpleSingletonIns = LazySimpleSingleton.getInstance();
        System.out.println("[EtTest run] lazySimpleSingletonIns: " + lazySimpleSingletonIns.toString() + "; current Thread name: " + Thread.currentThread().getName() + "; current Thread ID: " + Thread.currentThread().getId());

        ILazySingleton lazySyncSingletonIns = LazySyncSingleton.getInstance();
        System.out.println("[EtTest run] lazySyncSingletonIns: " + lazySyncSingletonIns.toString() + "; current Thread name: " + Thread.currentThread().getName() + "; current Thread ID: " + Thread.currentThread().getId());
    }
}
