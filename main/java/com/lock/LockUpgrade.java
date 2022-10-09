package com.lock;

import org.openjdk.jol.info.ClassLayout;

import java.util.concurrent.TimeUnit;

/**
 * 锁升级
 * <p>
 * <p>
 * 无状态(001)
 * |
 * 偏向锁(101)
 * |
 * 轻量级锁(00)
 * |
 * 重量级锁(10)
 *
 * @author walker
 * @since 2022/9/27 20:48
 */
public class LockUpgrade {
    public static void main(String[] args) throws InterruptedException {
        User tmpUser = new User();
        System.out.println("无状态(001), threadId:" + Thread.currentThread().getId() + " " + ClassLayout.parseInstance(tmpUser).toPrintable());


        // jvm默认延时4秒开启偏向锁
        TimeUnit.SECONDS.sleep(5);
        User user = new User();
        System.out.println("启用偏向锁(101)" + ClassLayout.parseInstance(user).toPrintable());
        // 单线程执行，只需要判断Object Header写入的线程是否是当前线程即可
        for (int i = 0; i < 2; i++) {
            synchronized (user) {
                System.out.println("偏向锁(101)(带线程id)" + ClassLayout.parseInstance(user).toPrintable());
            }
        }
        System.out.println("释放偏向锁(101)(带线程id)" + ClassLayout.parseInstance(user).toPrintable());

        // 当前开启了偏向锁，再次多线程加锁时升级为轻量级锁，使用CAS
        new Thread(() -> {
            synchronized (user) {
                System.out.println("轻量级锁(00)" + ClassLayout.parseInstance(user).toPrintable());

                // 先别释放轻量级锁，再有其他线程加锁时，升级为重量级锁
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("轻量级锁(00)-->重量级锁(10)" + ClassLayout.parseInstance(user).toPrintable());
            }
        }).start();

        // 轻量级锁还没有释放（休眠3秒），再次加锁时升级为重量级锁
        TimeUnit.SECONDS.sleep(1);
        new Thread(() -> {
            synchronized (user) {
                System.out.println("重量级锁(10)" + ClassLayout.parseInstance(user).toPrintable());
            }
        }).start();

    }
}