package com.pandaroid.dps.singleton.regist;

public class ThreadLocalSingleton {
    private ThreadLocalSingleton() {}

    /*private static final ThreadLocal<ThreadLocalSingleton> threadLocalSingletonInstance = new ThreadLocal<ThreadLocalSingleton>() {
        @Override
        protected ThreadLocalSingleton initialValue() {
            return new ThreadLocalSingleton();
        }
    };*/
    private static final ThreadLocal<ThreadLocalSingleton> threadLocalSingletonInstance = new ThreadLocal<ThreadLocalSingleton>().withInitial(() -> {
        return new ThreadLocalSingleton();
    });

    public static ThreadLocalSingleton getInstance() {
        return threadLocalSingletonInstance.get();
    }
}
