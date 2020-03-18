package com.pandaroid.dps.factory.simple;

import com.pandaroid.dps.factory.simple.products.InfrastructureTrustBid;
import com.pandaroid.dps.factory.simple.products.RealEstateTrustBid;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

    @Test
    void testOtherFactories() {
        // Calendar 工厂，可以进入学习源码
        Calendar c = Calendar.getInstance();
        logger.info("Calendar instance getTime: {}", c.getTime());
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("Calendar instance getTime sdf: {}", sdf.format(c.getTime()));
        // LoggerFactory 工厂
        Logger lg = LoggerFactory.getLogger(SimpleFactoryTest.class);
        logger.info("LoggerFactory instance lg: {}", lg.toString());
    }

    // Start: 插曲，一道算法题
    // 给定一个包含 0, 1, 2, ..., n 中 n 个数的序列，找出 0 .. n 中没有出现在序列中的那个数。
    // 示例 1 ：
    // 输入 [3, 0, 1]
    // 输出 2
    // 示例 2 ：
    // 输入 [9, 6, 4, 2, 3, 5, 7, 0, 1]
    // 输出 8
    // 高斯求和
    int missingNumberByGauss(int[] nums) {
        int len = nums.length;
        // int sum = len * (len + 1) / 2;
        int sum = (len * (len + 1)) >> 1;
        for(int num : nums) {
            sum -= num;
        }
        return sum;
    }
    // 异或相消：两个相同的数，使用异或可以相消除
    int missingNumberByExclusiveOr(int[] nums) {
        int res = 0;
        for(int i = 0; i < nums.length; i++) {
            res ^= nums[i] ^ i;
        }
        return res ^ nums.length;
    }
    @Test
    void testMissingNumber() {
        //
        /*logger.info("0 ^ 3 ^ 0 : {}", 0 ^ 3 ^ 0);
        logger.info("3 ^ 1 ^ 1 : {}", 3 ^ 1 ^ 1);
        logger.info("3 ^ 0 ^ 2 : {}", 3 ^ 0 ^ 2);
        logger.info("1 ^ 3 : {}", 1 ^ 3);*/
        /*logger.info("3 * 4 / 2: {}", 3 * 4 / 2);
        logger.info("3 * 4 >> 1: {}", 3 * 4 >> 1);*/
        // int[] nums = new int[]{3, 0, 1};
        int[] nums = new int[]{9, 6, 4, 2, 3, 5, 7, 0, 1};
        logger.info("[testExclusiveOr missingNumber] nums: {}", nums);
        logger.info("[testExclusiveOr missingNumber] missingNumberByExclusiveOr: {}", missingNumberByExclusiveOr(nums));
        logger.info("[testExclusiveOr missingNumber] missingNumberByGauss: {}", missingNumberByGauss(nums));
    }
    // End  : 插曲，一道算法题

}
