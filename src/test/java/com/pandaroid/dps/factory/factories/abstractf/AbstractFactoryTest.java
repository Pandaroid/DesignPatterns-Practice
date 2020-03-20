package com.pandaroid.dps.factory.factories.abstractf;

import com.pandaroid.dps.factory.factories.abstractf.infrastructure.InfrastructureTrustAbstractFactory;
import com.pandaroid.dps.factory.products.ITrustBid;
import org.junit.jupiter.api.Test;

public class AbstractFactoryTest {
    @Test
    void testAbstractFactory() {
        // 基础设施类信托标的产品工厂
        ITrustBidAbstractFactory infrastructureTrustBidAbstractFactory = new InfrastructureTrustAbstractFactory();
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
}
