package com.pandaroid.dps.singleton;

import com.pandaroid.dps.singleton.threads.EtTest;
import org.junit.jupiter.api.Test;

public class DesignPatternsTests {
    @Test
    void testPaySimpleFactory() {
        System.out.println("[DesignPatternsApplicationTests testPaySimpleFactory]");
    }

    @Test
    void testDpLazySimpleSingleton() throws InterruptedException {
        System.out.println("[DesignPatternsApplicationTests testDpLazySimpleSingleton]");
        Thread t1 = new Thread(new EtTest());
        Thread t2 = new Thread(new EtTest());
        t1.start();
        t2.start();
        Thread.sleep(2000);
        System.out.println("[DesignPatternsApplicationTests testDpLazySimpleSingleton] exec end");
    }
}
