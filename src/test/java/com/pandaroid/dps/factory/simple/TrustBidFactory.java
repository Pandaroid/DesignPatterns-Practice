package com.pandaroid.dps.factory.simple;

import com.pandaroid.dps.factory.simple.products.InfrastructureTrustBid;
import com.pandaroid.dps.factory.simple.products.RealEstateTrustBid;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 简单工厂适用于工厂类创建的产品对象较少
 * 客户端只需要提供工厂类创建产品对象的参数，对于其如何创建的过程逻辑不用关心
 */
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
    // 每增加一种产品，方法内部也需要增加，违反开闭原则
    // 另外两种方式不需要修改 create 产品方法
    // 三种方式都需要提供类的源码或 jar 包进行依赖，但前两种都不用依赖具体实现
    public ITrustBid createTrustBidByName(String name) {
        if("Infrastructure".equals(name)) {
            return new InfrastructureTrustBid("createTrustBidByName");
        } else if("ReakEstateTrustBid".equals(name)) {
            return new RealEstateTrustBid("createTrustBidByName");
        }
        return null;
    }
    // 这种方法也容易写错 name ，但是可以体会一下工厂模式对复杂的创建对象逻辑的封装，虽然这里也不是特别复杂，就是用到了反射而已
    // 实际上可以把 name 和 className 放 enum 里，实际调用时就不那么容易出问题了
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
    // 直接传 Clazz ，不容易出错，但是会造成调用类对具体实现 Class 的依赖，我们要依赖接口，而不是具体实现
    public ITrustBid createTrustBidByClass(Class<?> clazz) {
        if(null == clazz) {
            return null;
        }
        try {
            Constructor<?> constructor = clazz.getConstructor(String.class);
            return (ITrustBid) constructor.newInstance("createTrustBidByClass");
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
    // End  : 创建 TrustBid 产品的简单工厂方法
}
