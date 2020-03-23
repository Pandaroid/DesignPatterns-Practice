package com.pandaroid.dps.singleton.hungry;

public class HungryStaticSingleton {
    // 单例实例
    private static final HungryStaticSingleton hungrySingletonInstance;
    // static
    static {
        hungrySingletonInstance = new HungryStaticSingleton();
    }
    // 构造方法私有
    private HungryStaticSingleton() {}
    // 全局访问点
    public static HungryStaticSingleton getInstance() {
        return hungrySingletonInstance;
    }
}
