package com.pandaroid.dps.factory.simple;

import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TrustBidFactory {
    // Start: 工厂模式一般会结合单例模式使用
    private static TrustBidFactory trustBidFactory;
    // 饿汉式单例赋值：因为我们这里必然会使用到
    static {
        try {
            trustBidFactory = new TrustBidFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 私有构造，防止反射
    private TrustBidFactory() throws Exception {
        if(trustBidFactory != null) {
            throw new Exception("不能调用单例类的私有构造方法");
        }
    }
    // 向外暴露 getInstance 方法
    public static TrustBidFactory getInstance() {
        return trustBidFactory;
    }
    // 防止反序列化破坏单例：implements Serializable
    /*private Object readResolve() {
        return trustBidFactory;
    }*/
    // End  : 工厂模式一般会结合单例模式使用

    // Start: 创建 TrustBid 产品的简单工厂方法
    // 这种方法容易写错 name
    public ITrustBid createTrustBidByName(String name) {
        if("Infrastructure".equals(name)) {
            return new InfrastructureTrustBid("createTrustBidByName");
        }
        return null;
    }
    // 这种方法也容易写错 name ，但是可以体会一下工厂模式对复杂的创建对象逻辑的封装，虽然这里也不是特别复杂，就是用到了反射而已
    public ITrustBid createTrustBidByClassName(String className) {
        if(StringUtils.isEmpty(className)) {
            return null;
        }
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor(String.class);
            return (ITrustBid) constructor.newInstance("createTrustBidByClassName");
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
    // End  : 创建 TrustBid 产品的简单工厂方法
}
