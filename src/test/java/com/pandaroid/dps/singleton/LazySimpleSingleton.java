package com.pandaroid.dps.singleton;

public class LazySimpleSingleton implements ILazySingleton {

    private static LazySimpleSingleton lazySimpleSingletonIns = null;

    private LazySimpleSingleton() {}

    public static LazySimpleSingleton getInstance() {
        if(null == lazySimpleSingletonIns) {
            lazySimpleSingletonIns = new LazySimpleSingleton();
        }
        return lazySimpleSingletonIns;
    }

}
