package com.base;

/**
 * CAS实现模拟
 *
 * @author walker
 * @since 2021/12/20 18:23
 */
public class CASDemo1 {

    volatile int count;

    void addOne() {
        int newValue;
        do {
            newValue = count + 1;

            // 内存中实际值 和 期望的内存值 不等， 自旋重新读取内存中最新count，计算newValue 再尝试更新内存的值
        } while (count != compareAndSwap(count, newValue));
    }

    /**
     * 模拟原子操作
     */
    synchronized int compareAndSwap(int expect, int newValue) {
        int currValue = count;
        // 比较 内存中当前值 和 内存中期望值 是否相等
        if (expect == currValue) {
            // 相等，说明没有其他线程更新内存中count的值，当前线程更新count的值为新值newValue
            // 也就是说当前值是内存中最新的值
            System.out.println(System.currentTimeMillis() + " 线程" + Thread.currentThread().getName()
                    + ", 内存中count被更新,旧值是:" + count + ", 最新值是:" + newValue);
            count = newValue;
        }
        return currValue;
    }

    public static void main(String[] args) {
        CASDemo1 ins = new CASDemo1();
        new Thread(ins::addOne).start();
        new Thread(ins::addOne).start();
        new Thread(ins::addOne).start();
        new Thread(ins::addOne).start();
    }
}
