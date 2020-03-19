package com.pandaroid.dps.factory.factories.method;

import com.pandaroid.dps.factory.products.ITrustBid;

/**
 * 工厂方法模式
 * 本身只是一个接口规范，不用做任何事情，标准化作用
 */
public interface ITrustBidMethodFactory {
    ITrustBid createTrustBid();
}
