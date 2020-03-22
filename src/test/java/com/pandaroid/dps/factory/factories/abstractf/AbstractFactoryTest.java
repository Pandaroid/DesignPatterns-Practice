package com.pandaroid.dps.factory.factories.abstractf;

import com.pandaroid.dps.factory.factories.abstractf.estate.RealEstateTrustBidAbstractFactory;
import com.pandaroid.dps.factory.factories.abstractf.infrastructure.InfrastructureTrustBidAbstractFactory;
import com.pandaroid.dps.factory.products.ITrustBid;
import org.junit.jupiter.api.Test;

public class AbstractFactoryTest {
    @Test
    void testInfrastructureTrustBidAbstractFactory() {
        // 基础设施类信托标的产品工厂
        ITrustBidAbstractFactory infrastructureTrustBidAbstractFactory = new InfrastructureTrustBidAbstractFactory();
        // 生产产品：一般定期标的
        ITrustBid infrastructureTrustBid = infrastructureTrustBidAbstractFactory.createTrustBid();
        infrastructureTrustBid.pubTrustBid();
        // 生产产品：活期标旳
        ITrustBid infrastructureCurrentTrustBid = infrastructureTrustBidAbstractFactory.createCurrentTrustBid();
        infrastructureCurrentTrustBid.pubTrustBid();
        // 生产产品：体验标
        ITrustBid infrastructureExperienceTrustBid = infrastructureTrustBidAbstractFactory.createExperienceTrustBid();
        infrastructureExperienceTrustBid.pubTrustBid();
        // 生产产品：可转让标
        ITrustBid infrastructureTransferTrustBid = infrastructureTrustBidAbstractFactory.createTransferTrustBid();
        infrastructureTransferTrustBid.pubTrustBid();
    }

    @Test
    void testRealEstateTrustBidAbstractFactory() {
        // 房地产类信托标的产品工厂
        ITrustBidAbstractFactory realEstateTrustBidAbstractFactory = new RealEstateTrustBidAbstractFactory();
        // 生产产品：一般定期标的
        ITrustBid infrastructureTrustBid = realEstateTrustBidAbstractFactory.createTrustBid();
        infrastructureTrustBid.pubTrustBid();
        // 生产产品：活期标旳
        ITrustBid infrastructureCurrentTrustBid = realEstateTrustBidAbstractFactory.createCurrentTrustBid();
        infrastructureCurrentTrustBid.pubTrustBid();
        // 生产产品：体验标
        ITrustBid infrastructureExperienceTrustBid = realEstateTrustBidAbstractFactory.createExperienceTrustBid();
        infrastructureExperienceTrustBid.pubTrustBid();
        // 生产产品：可转让标
        ITrustBid infrastructureTransferTrustBid = realEstateTrustBidAbstractFactory.createTransferTrustBid();
        infrastructureTransferTrustBid.pubTrustBid();
    }
}
