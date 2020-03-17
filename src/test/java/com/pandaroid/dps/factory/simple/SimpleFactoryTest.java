package com.pandaroid.dps.factory.simple;

import com.pandaroid.dps.singleton.DesignPatternsTests;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleFactoryTest {
    private static final Logger logger = LoggerFactory.getLogger(SimpleFactoryTest.class);

    @Test
    void testSimpleFactory() {
        /*ITrustBid trustBid = new InfrastructureTrustBid();
        trustBid.pubTrustBid();*/
        // 使用简单工厂模式：createTrustBidByName
        TrustBidFactory trustBidFactory = TrustBidFactory.getInstance();
        ITrustBid infrustructureTrustBid = trustBidFactory.createTrustBidByName("Infrastructure");
        if(null == infrustructureTrustBid) {
            logger.error("infrustructureTrustBid is null");
            return ;
        }
        infrustructureTrustBid.pubTrustBid();
        // 使用简单工厂模式：createTrustBidByClassName
        ITrustBid infrustructureTrustBidByClassName = trustBidFactory.createTrustBidByClassName("com.pandaroid.dps.factory.simple.InfrastructureTrustBid");
        if(null == infrustructureTrustBidByClassName) {
            logger.error("infrustructureTrustBidByClassName is null");
            return ;
        }
        infrustructureTrustBidByClassName.pubTrustBid();
    }
}
