package com.pandaroid.dps.factory.factories.abstractf;

import com.pandaroid.dps.factory.factories.abstractf.iproducts.ICurrentTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.iproducts.IExperienceTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.iproducts.ITransferTrustBid;
import com.pandaroid.dps.factory.products.ITrustBid;

/**
 * 最顶层接口，要求所有子工厂都实现
 * 可以看作一个品牌的抽象，即一个种类的信托标的的抽象
 * 规定每一个品牌能创建哪些产品
 * 比如基础设施类信托标的工厂，可以创建活期标、可转让标、体验标、定期标
 * */
public interface ITrustBidAbstractFactory {
    // 原来的定期标
    ITrustBid createTrustBid();
    // 活期标
    ICurrentTrustBid createCurrentTrustBid();
    // 可转让标
    ITransferTrustBid createTransferTrustBid();
    // 体验标
    IExperienceTrustBid createExperienceTrustBid();
}
