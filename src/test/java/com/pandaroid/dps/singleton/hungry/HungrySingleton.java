package com.pandaroid.dps.singleton.hungry;

public class HungrySingleton {
    // 单例实例
    private static final HungrySingleton hungrySingletonInstance = new HungrySingleton();
    // 构造方法私有
    private HungrySingleton() {}
    // 全局访问点
    public static HungrySingleton getInstance() {
        return hungrySingletonInstance;
    }
}
