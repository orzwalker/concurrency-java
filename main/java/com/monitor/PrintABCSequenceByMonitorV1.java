package com.monitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 3个线程交替打印A、B、C
 */
public class PrintABCSequenceByMonitorV1 {

    /**
     * 因为用了同一把锁，所以可见性能保证
     */
    int flag = 0;

    Lock lock = new ReentrantLock(true);

    /**
     * 等待打印A的条件等待队列
     */
    Condition wait_print_a_queue = lock.newCondition();
    /**
     * 等待打印B的条件等待队列
     */
    Condition wait_print_b_queue = lock.newCondition();
    /**
     * 等待打印C的条件等待队列
     */
    Condition wait_print_c_queue = lock.newCondition();


    /**
     * 输出A
     */
    public void printA() {
        lock.lock();
        try {
            // flag==0时才打印A，其他情况都阻塞进入等待队列
            while (flag != 0) {
                wait_print_a_queue.await();
            }
            System.out.println(Thread.currentThread().getName() + "_A");
            // flag=1，并唤起打印B的等待队列
            flag = 1;
            wait_print_b_queue.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public void printB() {
        lock.lock();
        try {
            while (flag != 1) {
                wait_print_b_queue.await();
            }
            System.out.println(Thread.currentThread().getName() + "_B");
            flag = 2;
            wait_print_c_queue.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printC() {
        lock.lock();
        try {
            while (flag != 2) {
                wait_print_c_queue.await();
            }
            System.out.println(Thread.currentThread().getName() + "_C");
            flag = 0;
            wait_print_a_queue.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


    public static void main(String[] args) {
        int size = 4;
        PrintABCSequenceByMonitorV1 ins = new PrintABCSequenceByMonitorV1();
        Thread a = new Thread(() -> {
            for (int i = 0; i < size; i++) {
                ins.printA();
            }
        });
        Thread b = new Thread(() -> {
            for (int i = 0; i < size; i++) {
                ins.printB();
            }
        });
        Thread c = new Thread(() -> {
            for (int i = 0; i < size; i++) {
                ins.printC();
            }
        });

        a.start();
        b.start();
        c.start();
    }
}
