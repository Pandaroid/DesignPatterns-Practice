package com.pandaroid.dps.singleton;

import com.pandaroid.dps.singleton.lazy.ILazySingleton;
import com.pandaroid.dps.singleton.lazy.LazyInnerClassSingleton;
import com.pandaroid.dps.singleton.serializable.SerializableSingleton;
import com.pandaroid.dps.singleton.threads.EtTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SingletonTests {
    private static final Logger logger = LoggerFactory.getLogger(SingletonTests.class);

    @Test
    void testDpLazySingleton() throws InterruptedException {
        System.out.println("[SingletonTests testDpLazySingleton]");
        logger.debug("[SingletonTests testDpLazySingleton]");
        logger.info("[SingletonTests testDpLazySingleton]");
        logger.warn("[SingletonTests testDpLazySingleton]");
        Thread t1 = new Thread(new EtTest());
        Thread t2 = new Thread(new EtTest());
        t1.start();
        t2.start();
        Thread.sleep(2000);
        System.out.println("[SingletonTests testDpLazySingleton] exec end. Integer.MAX_VALUE(2147483647) + 1 : " + (Integer.MAX_VALUE + 1));
        System.out.println("[SingletonTests testDpLazySingleton] exec end. Integer.MIN_VALUE(-2147483648): " + (Integer.MIN_VALUE));
        // Integer.MAX_VALUE(2147483647) + 1 : -2147483648
    }

    @Test
    void testReflectSingleton() {
        Class<?> clazz = LazyInnerClassSingleton.class;
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            ILazySingleton lazyInnerClassSingletonReflect = (LazyInnerClassSingleton) constructor.newInstance();
            System.out.println("[SingletonTests testReflectSingleton] lazyInnerClassSingletonReflect.toString(): " + lazyInnerClassSingletonReflect.toString());
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        ILazySingleton lazyInnerClassSingleton = LazyInnerClassSingleton.getInstance();
        System.out.println("[SingletonTests testReflectSingleton] lazyInnerClassSingleton.toString(): " + lazyInnerClassSingleton.toString());
        // [SingletonTests testReflectSingleton] lazyInnerClassSingletonReflect.toString(): com.pandaroid.dps.singleton.lazy.LazyInnerClassSingleton@2698dc7
        // [SingletonTests testReflectSingleton] lazyInnerClassSingleton.toString(): com.pandaroid.dps.singleton.lazy.LazyInnerClassSingleton@43d7741f
        // 可以看到单例被破坏了
    }

    @Test
    void testSerializableSingleton() {
        SerializableSingleton serializableSingleton2Read = null;
        SerializableSingleton serializableSingleton2Write = SerializableSingleton.getInstance();

        String fileName = "SerializableSingleton.obj";
        FileOutputStream fos = null;
        try {
            // 序列化写出
            fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(serializableSingleton2Write);
            oos.flush();
            oos.close();

            // 序列化读入
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            serializableSingleton2Read = (SerializableSingleton) ois.readObject();
            ois.close();
            
            // 输出结果
            System.out.println("[SingletonTests testSerializableSingleton] serializableSingleton2Write.toString(): " + serializableSingleton2Write.toString());
            System.out.println("[SingletonTests testSerializableSingleton] serializableSingleton2Read.toString(): " + serializableSingleton2Read.toString());
            System.out.println("[SingletonTests testSerializableSingleton] serializableSingleton2Write == serializableSingleton2Read : " + (serializableSingleton2Write == serializableSingleton2Read));
            // [SingletonTests testSerializableSingleton] serializableSingleton2Write.toString(): com.pandaroid.dps.singleton.serializable.SerializableSingleton@69379752
            // [SingletonTests testSerializableSingleton] serializableSingleton2Read.toString(): com.pandaroid.dps.singleton.serializable.SerializableSingleton@133e16fd
            // [SingletonTests testSerializableSingleton] serializableSingleton2Write == serializableSingleton2Read : false
            // 可以看到，通过序列化，我们破坏了官方比较推荐的单例写法。但是，这种破坏也是有解决方案的：readResolve
            // private Object readResolve() {
            //     return getInstance();
            // }
            // 再次执行，可以看到序列化无法破坏我们的单例了
            // [SingletonTests testSerializableSingleton] serializableSingleton2Write.toString(): com.pandaroid.dps.singleton.serializable.SerializableSingleton@69379752
            // [SingletonTests testSerializableSingleton] serializableSingleton2Read.toString(): com.pandaroid.dps.singleton.serializable.SerializableSingleton@69379752
            // [SingletonTests testSerializableSingleton] serializableSingleton2Write == serializableSingleton2Read : true
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
