package com.pandaroid.dps.singleton;

import com.pandaroid.dps.singleton.threads.EtTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LazySingletonTests {
    private static final Logger logger = LoggerFactory.getLogger(LazySingletonTests.class);

    @Test
    void testDpLazySingleton() throws InterruptedException {
        System.out.println("[LazySingletonTests testDpLazySingleton]");
        logger.debug("[LazySingletonTests testDpLazySingleton]");
        logger.info("[LazySingletonTests testDpLazySingleton]");
        logger.warn("[LazySingletonTests testDpLazySingleton]");
        Thread t1 = new Thread(new EtTest());
        Thread t2 = new Thread(new EtTest());
        t1.start();
        t2.start();
        Thread.sleep(2000);
        System.out.println("[LazySingletonTests testDpLazySingleton] exec end");
    }
}
