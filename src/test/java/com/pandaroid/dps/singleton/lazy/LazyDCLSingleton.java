package com.pandaroid.dps.singleton.lazy;

public class LazyDCLSingleton implements ILazySingleton {

    private volatile static LazyDCLSingleton lazySimpleSingletonIns = null;

    private LazyDCLSingleton() {}

    public static LazyDCLSingleton getInstance() {
        if(null == lazySimpleSingletonIns) {
            synchronized (LazyDCLSingleton.class) {
                if(null == lazySimpleSingletonIns) {
                    lazySimpleSingletonIns = new LazyDCLSingleton();
                }
            }
        }
        return lazySimpleSingletonIns;
    }

}
