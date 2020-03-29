package com.pandaroid.dps.singleton.regist.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ConcurrentExecutor {
    /**
     * 用于并发执行 runHandler 任务，控制执行总数 executeCount ，和可同时进入执行任务的并发数 concurrentCount
     * @param runHandler
     * @param executeCount
     * @param concurrentCount
     */
    public static void execute(final RunHandler runHandler, int executeCount, int concurrentCount) {
        // startTimeMillis
        long startTimeMillis = System.currentTimeMillis();
        // executorService
        ExecutorService executorService = Executors.newCachedThreadPool();
        // 用 Semaphore 控制 concurrentCount 设置的并发数量
        final Semaphore semaphore = new Semaphore(concurrentCount);
        // 用 CountDownLaunch 来实现 executeCount 执行完毕后的 executorService 释放
        final CountDownLatch countDownLatch = new CountDownLatch(executeCount);
        for(int i = 0; i < executeCount; i++) {
            executorService.execute(() -> {
                try {
                    // 控制并发不超过 concurrentCount ，超过的会在此阻塞等待直到其他线程 release 并 acquire 到许可以后再执行
                    semaphore.acquire();
                    runHandler.handler();
                    // 释放许可
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        // 在此通过 countDownLatch 阻塞主线程，直到 executeCount 的子线程执行完毕后（闭锁值为 0 时），阻塞释放，继续向下执行 executorService 的 shutDown
        // The {@link #shutdown} method will allow previously submitted tasks to execute before terminating
        // 所以我认为这里使用 countDownLatch 是没有必要的，但是 shutDown 在某些场景下有必要，因为：
        // An unused {@code ExecutorService} should be shut down to allow reclamation of its resources.
        // 经测试，不使用 countDownLatch ，执行 shutdown ，任务不能执行完全部 executeCount ，如果使用 shutdownNow ，会抛出 java.lang.InterruptedException: sleep interrupted
        // 因为我们用 Thread.sleep() 来辅助测试
        // 所以，这里使用 countDownLatch 是有必要的，但我还是怀疑这里是因为主线程提前结束了，所以子线程没执行完导致异常，于是我试试主线程 sleep 足够长时间
        // 使用 shutdownNow 还是会 java.lang.InterruptedException: sleep interrupted
        // 但是，使用 shutdown 最终所有 executeCount 个线程都执行完了，得证
        // 所以，这里使用 countDownLatch 是更高效的解决了这个问题，而不用 sleep 足够长时间，executeCount 个都执行完了，阻塞解除，安全的 shutdown
        // 否则，主线程结束了，子线程还没执行完，就会抛出异常
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // shutDown executorService
        executorService.shutdown();
        // executorService.shutdownNow();
        /*try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        // 执行时间打印
        System.out.println("[ConcurrentExecutor execute] executeTimeMillis: " + (System.currentTimeMillis() - startTimeMillis));
    }
}
