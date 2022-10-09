package com._countdownlatch;

import java.util.concurrent.CountDownLatch;

/**
 * @author walker
 * @since 2022/10/8 09:53
 */
public class CountDownLatchTest {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(7);
        Service service = new Service(countDownLatch);
        Runnable runnable = service::execute;
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(runnable);
            thread.start();
        }

        System.out.println("main thread wait....");
        // countDownLatch.await(2000, TimeUnit.MILLISECONDS);
        countDownLatch.await();
        System.out.println("main thread done...");
    }

    static class Service {

        private final CountDownLatch countDownLatch;

        public Service(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        public void execute() {
            try {
                System.out.println(Thread.currentThread().getName() + " execute task start...");
                Thread.sleep(2000);
                System.out.println(Thread.currentThread().getName() + " execute task done...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }
    }
}
