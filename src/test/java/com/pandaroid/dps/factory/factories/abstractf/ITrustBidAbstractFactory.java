package com.pandaroid.dps.factory.factories.abstractf;

import com.pandaroid.dps.factory.factories.abstractf.iproducts.ICurrentTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.iproducts.IExperienceTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.iproducts.IRelayTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.iproducts.ITransferTrustBid;
import com.pandaroid.dps.factory.products.ITrustBid;

/**
 * 最顶层接口，要求所有子工厂都实现
 * 可以看作一个品牌的抽象，即一个种类的信托标的的抽象
 * 规定每一个品牌能创建哪些产品
 * 比如基础设施类信托标的工厂，可以创建活期标、可转让标、体验标、定期标
 * 抽象工厂不符合开闭原则，但扩展性非常强
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
    // 接力标：如果增加，子类实现类都需要增加，不符合开闭原则，但是扩展性强
    // 如果本来增加一个产品就是在不同产品等级结构下（不同品牌工厂），具有不同的实现，那么这样做使用抽象工厂方法模式，其实并没有引入太多的复杂度
    // IRelayTrustBid createRelayTrustBid();
}
