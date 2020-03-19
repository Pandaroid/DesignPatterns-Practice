package com.pandaroid.dps.factory.method;

/**
 * 可以将一些重复的逻辑抽象出来，放到抽象类里面
 * */
public abstract class ATrustBidFactory {
    // 如果所有的信托标的产品工厂，都有相同的创建产品前检查逻辑
    // 比如：检查当前时段是否能发布创建的产品（分时间段发布不同收益、额度的信托标的）、创建的产品对接的信托剩余额度是否足够等
    // 其他的逻辑，比如必须要记录日志成功（一般关键操作前置后置都要有日志）以方便意外情况排查和快速恢复
    public void checkBeforeCreate() {
        System.out.printf("[ATrustBidFactory] checkBeforeCreate");
    }
}
