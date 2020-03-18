package com.pandaroid.dps.factory;

/**
 * 信托产品接口，我们当时公司内部都说"标的"
 */
public interface ITrustBid {
    // 信托产品需要提供发布接口，供我们手动发标或系统按计划自动发标
    public void pubTrustBid();
}
