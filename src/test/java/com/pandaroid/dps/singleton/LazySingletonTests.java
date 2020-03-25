package com.pandaroid.dps.singleton;

import com.pandaroid.dps.singleton.lazy.ILazySingleton;
import com.pandaroid.dps.singleton.lazy.LazyInnerClassSingleton;
import com.pandaroid.dps.singleton.threads.EtTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class LazySingletonTests {
    private static final Logger logger = LoggerFactory.getLogger(LazySingletonTests.class);

    @Test
    void testDpLazySingleton() throws InterruptedException {
        System.out.println("[LazySingletonTests testDpLazySingleton]");
        logger.debug("[LazySingletonTests testDpLazySingleton]");
        logger.info("[LazySingletonTests testDpLazySingleton]");
        logger.warn("[LazySingletonTests testDpLazySingleton]");
        Thread t1 = new Thread(new EtTest());
        Thread t2 = new Thread(new EtTest());
        t1.start();
        t2.start();
        Thread.sleep(2000);
        System.out.println("[LazySingletonTests testDpLazySingleton] exec end. Integer.MAX_VALUE(2147483647) + 1 : " + (Integer.MAX_VALUE + 1));
        System.out.println("[LazySingletonTests testDpLazySingleton] exec end. Integer.MIN_VALUE(-2147483648): " + (Integer.MIN_VALUE));
        // Integer.MAX_VALUE(2147483647) + 1 : -2147483648
    }

    @Test
    void testReflectSingleton() {
        Class<?> clazz = LazyInnerClassSingleton.class;
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            ILazySingleton lazyInnerClassSingletonReflect = (LazyInnerClassSingleton) constructor.newInstance();
            System.out.println("[LazySingletonTests testReflectSingleton] lazyInnerClassSingletonReflect.toString(): " + lazyInnerClassSingletonReflect.toString());
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        ILazySingleton lazyInnerClassSingleton = LazyInnerClassSingleton.getInstance();
        System.out.println("[LazySingletonTests testReflectSingleton] lazyInnerClassSingleton.toString(): " + lazyInnerClassSingleton.toString());
        // [LazySingletonTests testReflectSingleton] lazyInnerClassSingletonReflect.toString(): com.pandaroid.dps.singleton.lazy.LazyInnerClassSingleton@2698dc7
        // [LazySingletonTests testReflectSingleton] lazyInnerClassSingleton.toString(): com.pandaroid.dps.singleton.lazy.LazyInnerClassSingleton@43d7741f
        // 可以看到单例被破坏了
    }
}
