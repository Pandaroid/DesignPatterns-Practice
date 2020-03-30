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
        System.out.println("[ThreadLocalSingleton withInitial] Thread.currentThread(): " + Thread.currentThread());
        return new ThreadLocalSingleton();
    });

    public static ThreadLocalSingleton getInstance() {
        return threadLocalSingletonInstance.get();
    }

    public static void testLazy() {
        System.out.println("[ThreadLocalSingleton testLazy] Thread.currentThread(): " + Thread.currentThread());
        System.out.println("[ThreadLocalSingleton testLazy] threadLocalSingletonInstance: " + threadLocalSingletonInstance);
        // [SingletonTests testThreadLocalSingleton] Thread.currentThread(): Thread[main,5,main]
        // [ThreadLocalSingleton testLazy] Thread.currentThread(): Thread[main,5,main]
        // [ThreadLocalSingleton testLazy] threadLocalSingletonInstance: java.lang.ThreadLocal$SuppliedThreadLocal@17baae6e
        // [SingletonTests testThreadLocalSingleton ConcurrentExecutor.execute] Thread.currentThread(): Thread[pool-1-thread-1,5,main]
        // [ThreadLocalSingleton withInitial] Thread.currentThread(): Thread[pool-1-thread-1,5,main]
        // 对于 main 线程来说，因为没有调用 ThreadLocalSingleton.getInstance() ，所以没有执行 [ThreadLocalSingleton withInitial] ，对比下面执行了 getInstance
        // [SingletonTests testThreadLocalSingleton] Thread.currentThread(): Thread[main,5,main]
        // [ThreadLocalSingleton testLazy] Thread.currentThread(): Thread[main,5,main]
        // [ThreadLocalSingleton testLazy] threadLocalSingletonInstance: java.lang.ThreadLocal$SuppliedThreadLocal@17baae6e
        // [ThreadLocalSingleton withInitial] Thread.currentThread(): Thread[main,5,main]
        // [SingletonTests testThreadLocalSingleton] ThreadLocalSingleton.getInstance(): com.pandaroid.dps.singleton.regist.ThreadLocalSingleton@69379752
        // [SingletonTests testThreadLocalSingleton] ThreadLocalSingleton.getInstance(): com.pandaroid.dps.singleton.regist.ThreadLocalSingleton@69379752
        // [SingletonTests testThreadLocalSingleton] ThreadLocalSingleton.getInstance(): com.pandaroid.dps.singleton.regist.ThreadLocalSingleton@69379752
        // [SingletonTests testThreadLocalSingleton ConcurrentExecutor.execute] Thread.currentThread(): Thread[pool-1-thread-1,5,main]
        // [ThreadLocalSingleton withInitial] Thread.currentThread(): Thread[pool-1-thread-1,5,main]
        // 所以，可以确定 ThreadLocalSingleton 是懒汉式单例
    }
}
