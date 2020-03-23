package com.pandaroid.dps.singleton;

import com.pandaroid.dps.singleton.threads.EtTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LazySingletonTests {
    private static final Logger logger = LoggerFactory.getLogger(LazySingletonTests.class);

    @Test
    void testPaySimpleFactory() {
        System.out.println("[DesignPatternsApplicationTests testPaySimpleFactory]");
    }

    @Test
    void testDpLazySimpleSingleton() throws InterruptedException {
        System.out.println("[DesignPatternsApplicationTests testDpLazySimpleSingleton]");
        logger.debug("[DesignPatternsApplicationTests testDpLazySimpleSingleton]");
        logger.info("[DesignPatternsApplicationTests testDpLazySimpleSingleton]");
        logger.warn("[DesignPatternsApplicationTests testDpLazySimpleSingleton]");
        Thread t1 = new Thread(new EtTest());
        Thread t2 = new Thread(new EtTest());
        t1.start();
        t2.start();
        Thread.sleep(2000);
        System.out.println("[DesignPatternsApplicationTests testDpLazySimpleSingleton] exec end");
    }
}
