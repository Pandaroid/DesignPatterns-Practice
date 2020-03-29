package com.pandaroid.dps.singleton.regist;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContainerSingleton {
    private ContainerSingleton() {}

    private static Map<String, Object> ioc = new ConcurrentHashMap<>();

    public static Object getBean(String className) {
        if(!ioc.containsKey(className)) {
            synchronized (ioc) {
                if(!ioc.containsKey(className)) {
                    try {
                        Class clazz = Class.forName(className);
                        Object obj = clazz.newInstance();
                        ioc.put(className, obj);
                    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return ioc.get(className);
    }
}
