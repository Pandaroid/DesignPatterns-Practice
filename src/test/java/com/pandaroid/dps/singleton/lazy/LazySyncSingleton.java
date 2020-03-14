package com.pandaroid.dps.singleton.lazy;

public class LazySyncSingleton implements ILazySingleton {

    private volatile static LazySyncSingleton lazySimpleSingletonIns = null;

    private LazySyncSingleton() {}

    public static LazySyncSingleton getInstance() {
        if(null == lazySimpleSingletonIns) {
            synchronized (LazySyncSingleton.class) {
                if(null == lazySimpleSingletonIns) {
                    lazySimpleSingletonIns = new LazySyncSingleton();
                }
            }
        }
        return lazySimpleSingletonIns;
    }

}
