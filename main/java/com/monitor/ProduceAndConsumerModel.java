package com.monitor;

/**
 * 模拟服务假死
 *
 * @author walker
 * @since 2022/11/11 16:41
 */
public class ProduceAndConsumerModel {

    //a表示共享变量
    private int a = 0;
    //lock就是一把锁
    final private Object lock = new Object();
    // isProduced表示是否已经生产的标志
    private volatile boolean isProduced = false;

    public void produce() throws InterruptedException {
        synchronized (lock) {
            // 如果已经生产了，那就等消费了再生产
            while (isProduced) {
                lock.wait();
            }
            // 没有生产，那就生产一个，并通知消费者去消费
            a++;
            System.out.println("生产者" + Thread.currentThread().getName() + "生产一个产品：" + (a));
            isProduced = true;
//            lock.notifyAll();
            /**
             * 使用notify，因为单锁，所以可能唤醒的还是produce线程，这样的话一直处于阻塞状态
             * 使用notifyAll可解决该问题
             * 下consume同理
             */
            lock.notify();
        }

    }

    public void consume() throws InterruptedException {
        synchronized (lock) {
            // 如果有产品，那就消费，并通知生产者可以继续生产了。
            while (!isProduced) {
                lock.wait();
            }
            a--;
            System.out.println("消费者" + Thread.currentThread().getName() + "消费一个产品：" + (a));
            isProduced = false;
//            lock.notifyAll();
            lock.notify();
        }
    }

    public static void main(String[] args) {
        ProduceAndConsumerModel model = new ProduceAndConsumerModel();

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    while (true) {
                        model.produce();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, " produce-" + i).start();
        }

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    while (true) {
                        model.consume();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, " consume-" + i).start();
        }


    }

}