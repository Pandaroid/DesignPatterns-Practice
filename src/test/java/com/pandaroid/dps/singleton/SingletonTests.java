package com.pandaroid.dps.singleton;

import com.pandaroid.dps.singleton.lazy.ILazySingleton;
import com.pandaroid.dps.singleton.lazy.LazyInnerClassSingleton;
import com.pandaroid.dps.singleton.regist.ContainerSingleton;
import com.pandaroid.dps.singleton.regist.EnumSingleton;
import com.pandaroid.dps.singleton.regist.ThreadLocalSingleton;
import com.pandaroid.dps.singleton.regist.concurrency.ConcurrentExecutor;
import com.pandaroid.dps.singleton.regist.concurrency.RunHandler;
import com.pandaroid.dps.singleton.serializable.SerializableSingleton;
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

    @Test
    void testSerializableEnumSingleton() {
        EnumSingleton enumSingleton2Read = null;
        EnumSingleton enumSingleton2Write = EnumSingleton.getInstance();

        System.out.println("[SingletonTests testSerializableEnumSingleton] enumSingleton2Write.name(): " + enumSingleton2Write.name());
        System.out.println("[SingletonTests testSerializableEnumSingleton] enumSingleton2Write.ordinal(): " + enumSingleton2Write.ordinal());
        // [SingletonTests testSerializableEnumSingleton] enumSingleton2Write.name(): ENUM_SINGLETON_INSTANCE
        // [SingletonTests testSerializableEnumSingleton] enumSingleton2Write.ordinal(): 0

        // 用于后面 getData 测试
        enumSingleton2Write.setData(new Object());

        String fileName = "EnumSingleton.obj";
        FileOutputStream fos = null;
        try {
            // 序列化写出
            fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(enumSingleton2Write);
            oos.flush();
            oos.close();

            // 序列化读入
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            enumSingleton2Read = (EnumSingleton) ois.readObject();
            ois.close();

            // 输出结果
            System.out.println("[SingletonTests testSerializableEnumSingleton] enumSingleton2Write: " + enumSingleton2Write);
            System.out.println("[SingletonTests testSerializableEnumSingleton] enumSingleton2Read: " + enumSingleton2Read);
            System.out.println("[SingletonTests testSerializableEnumSingleton] enumSingleton2Write == serializableSingleton2Read : " + (enumSingleton2Write == enumSingleton2Read));
            // [SingletonTests testSerializableEnumSingleton] enumSingleton2Write.toString(): ENUM_SINGLETON_INSTANCE
            // [SingletonTests testSerializableEnumSingleton] enumSingleton2Read.toString(): ENUM_SINGLETON_INSTANCE
            // [SingletonTests testSerializableEnumSingleton] enumSingleton2Write == serializableSingleton2Read : true
            // 可以看到，通过序列化，并没有破坏《Effective Java》推荐的 Enum 枚举式单例写法
            // 这里测试了 implements Serializable 和不带 implements Serializable 两种情况，结果都如上，一致
            System.out.println("[SingletonTests testSerializableEnumSingleton] enumSingleton2Write.getData(): " + enumSingleton2Write.getData());
            System.out.println("[SingletonTests testSerializableEnumSingleton] enumSingleton2Read.getData(): " + enumSingleton2Read.getData());
            System.out.println("[SingletonTests testSerializableEnumSingleton] enumSingleton2Write.getData() == serializableSingleton2Read.getData() : " + (enumSingleton2Write.getData() == enumSingleton2Read.getData()));
            // [SingletonTests testSerializableEnumSingleton] enumSingleton2Write.getData(): java.lang.Object@1ad282e0
            // [SingletonTests testSerializableEnumSingleton] enumSingleton2Read.getData(): java.lang.Object@1ad282e0
            // [SingletonTests testSerializableEnumSingleton] enumSingleton2Write.getData() == serializableSingleton2Read.getData() : true
            // 可以看到 data 也是同一个，确实保证了单例，没有被序列化破坏
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testReflectEnumSingleton() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> clazz = EnumSingleton.class;

        // Start: 没有无参的 Constructor
        /*Constructor constructorNoArgs = clazz.getDeclaredConstructor();
        constructorNoArgs.setAccessible(true);
        constructorNoArgs.newInstance();*/
        // java.lang.NoSuchMethodException: com.pandaroid.dps.singleton.regist.EnumSingleton.<init>()
        // End  : 没有无参的 Constructor

        // Start: 从反编译的源码中，我们看到有一个 Constructor
        // private EnumSingleton(String s, int i)
        // {
        //     super(s, i);
        // }
        Constructor[] constructors = clazz.getDeclaredConstructors();
        for (Constructor constructor : constructors) {
            System.out.println("[SingletonTests testReflectEnumSingleton] constructor: " + constructor);
            // [SingletonTests testReflectEnumSingleton] constructor: private com.pandaroid.dps.singleton.regist.EnumSingleton(java.lang.String,int)
            // 只有这一个 Constructor ，我们尝试用它来 newInstance
        }
        Constructor constructorStringInt = clazz.getDeclaredConstructor(String.class, int.class);
        constructorStringInt.setAccessible(true);
        EnumSingleton enumSingleton = (EnumSingleton) constructorStringInt.newInstance("ENUM_SINGLETON_INSTANCE", 0);
        System.out.println("[SingletonTests testReflectEnumSingleton] enumSingleton.getData(): " + enumSingleton.getData());
        // java.lang.IllegalArgumentException: Cannot reflectively create enum objects
        // 上面异常表示：不能用反射来创建枚举类型。为什么？答案就在 newInstance 方法的源码中
        // if ((clazz.getModifiers() & Modifier.ENUM) != 0)
        //     throw new IllegalArgumentException("Cannot reflectively create enum objects");
        // 对枚举又是特殊对待，如果修饰符是 Modifier.ENUM 枚举类型，则直接抛出异常
        // 因为在 JDK 中枚举的特殊性，无论在反编译后的 jad 源码中，还是反序列化、反射的源码中，我们都可以看到枚举非常特殊
        // 正是因为这些特殊和优待保护，让枚举式单例十分优雅，成为《Effective Java》推荐的一种单例写法
        // End  : 从反编译的源码中，我们看到有一个 Constructor
    }

    @Test
    void testContainerSingleton() {
        Object bean = ContainerSingleton.getBean("com.pandaroid.dps.singleton.EtTest");
        System.out.println("[SingletonTests testContainerSingleton] bean: " + bean);
    }

    @Test
    void testContainerSingletonConcurrency() {
        ConcurrentExecutor.execute(() -> {
            Object bean = ContainerSingleton.getBean("com.pandaroid.dps.singleton.EtTest");
            System.out.println("[SingletonTests testContainerSingletonConcurrency] bean: " + bean);
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }, 1000, 6);
        // 非线程安全，执行结果：
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@24d606ed
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@412414b2
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@a2514f8
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@1dd14e16
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@1dd14e16
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@1dd14e16
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@1dd14e16
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@1dd14e16
        // [ConcurrentExecutor execute] executeTimeMillis: 4
        // 加上 synchronized 以后，没有明显增加执行时间，但执行结果都是保持单例的一致的了
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
        // [SingletonTests testContainerSingletonConcurrency] bean: com.pandaroid.dps.singleton.EtTest@173c9b06
        // [ConcurrentExecutor execute] executeTimeMillis: 3
        // 线程安全，executeCount 1000 ，耗时：
        // [ConcurrentExecutor execute] executeTimeMillis: 71
        // 线程安全，executeCount 1000 ，DCL ，耗时：
        // [ConcurrentExecutor execute] executeTimeMillis: 47
        // 明显 DCL 还是有作用的，逼死强迫症啊
    }

    @Test
    void testThreadLocalSingleton() {
        ThreadLocalSingleton threadLocalSingleton = ThreadLocalSingleton.getInstance();
        System.out.println("[SingletonTests testThreadLocalSingleton] threadLocalSingleton: " + threadLocalSingleton);
        // 多次执行，结果一致：
        // [SingletonTests testThreadLocalSingleton] threadLocalSingleton: com.pandaroid.dps.singleton.regist.ThreadLocalSingleton@2698dc7
    }
}
