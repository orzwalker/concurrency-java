package com._park;

import java.util.concurrent.locks.LockSupport;

/**
 * park、unpark方法成对使用时，前后顺序没有严格限制
 */
public class ParkTest {
    public static void main(String[] args) {

        Thread thread = new Thread(() -> {
            System.out.println("start");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("park");
            LockSupport.park();
        }, "t1");

        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("unpark");
        LockSupport.unpark(thread);
    }

    /*
    start
    unpark
    park
     */

    /*
    start
    park
    unpark
     */
}
