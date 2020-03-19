package com.pandaroid.dps.factory.method;

import com.pandaroid.dps.factory.ITrustBid;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

public class FactoryMethodTest {
    @Test
    void testFactoryMethod() {
        // 这里可以使用单例 + 简单工厂来提供不同的工厂方法类实例，更优雅
        // 如果不同工厂的逻辑更加复杂，可以通过策略模式实现
        ITrustBidMethodFactory infrustructureTrustFactory = new InfrastructureTrustFactory();
        ITrustBid infrustructureTrustBid = infrustructureTrustFactory.createTrustBid();
        infrustructureTrustBid.pubTrustBid();
    }

    @Test
    void testOtherFactoryMethods() {
        // 进入看源码，会通过
        // ILoggerFactory iLoggerFactory = getILoggerFactory();
        // 再获取工厂方法类：log4j 、NOP 等。每个都按接口规范，实现了 getLogger 方法
        LoggerFactory.getLogger(FactoryMethodTest.class);
    }
}
