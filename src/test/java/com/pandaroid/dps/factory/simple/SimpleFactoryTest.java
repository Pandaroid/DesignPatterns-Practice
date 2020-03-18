package com.pandaroid.dps.factory.simple;

import com.pandaroid.dps.factory.simple.products.InfrastructureTrustBid;
import com.pandaroid.dps.factory.simple.products.RealEstateTrustBid;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* 简单工厂解决问题：封装复杂的创建逻辑，对用户透明，简化调用获取产品的逻辑
* */
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
        //
        ITrustBid reakEstateTrustBid = trustBidFactory.createTrustBidByName("ReakEstateTrustBid");
        if(null == reakEstateTrustBid) {
            logger.error("reakEstateTrustBid is null");
            return ;
        }
        reakEstateTrustBid.pubTrustBid();
        // 使用简单工厂模式：createTrustBidByClassName
        ITrustBid infrustructureTrustBidByClassName = trustBidFactory.createTrustBidByClassName("com.pandaroid.dps.factory.simple.products.InfrastructureTrustBid");
        if(null == infrustructureTrustBidByClassName) {
            logger.error("infrustructureTrustBidByClassName is null");
            return ;
        }
        infrustructureTrustBidByClassName.pubTrustBid();
        //
        ITrustBid realEstateTrustBidByClassName = trustBidFactory.createTrustBidByClassName("com.pandaroid.dps.factory.simple.products.RealEstateTrustBid");
        if(null == realEstateTrustBidByClassName) {
            logger.error("realEstateTrustBidByClassName is null");
            return ;
        }
        realEstateTrustBidByClassName.pubTrustBid();
        // 使用简单工厂模式：createTrustBidByClass
        ITrustBid infrustructureTrustBidByClass = trustBidFactory.createTrustBidByClass(InfrastructureTrustBid.class);
        if(null == infrustructureTrustBidByClass) {
            logger.error("infrustructureTrustBidByClass is null");
            return ;
        }
        infrustructureTrustBidByClass.pubTrustBid();
        //
        ITrustBid realEstateTrustBidByClass = trustBidFactory.createTrustBidByClass(RealEstateTrustBid.class);
        if(null == realEstateTrustBidByClass) {
            logger.error("realEstateTrustBidByClass is null");
            return ;
        }
        realEstateTrustBidByClass.pubTrustBid();
    }

    
}
