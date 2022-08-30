package com.base;

/**
 * @author walker
 * @since 2021/12/7 09:53
 */
public class TestDemo1 {

    /**
     * 内存中的数据，多线程修改时，先将内存数据读到各自CPU（多核）缓存中，然后进行累加计算，最后再写到内存那种
     * 什么时候写到内存中是不能确定的，除非手动调用了底层相关方法
     * 加锁
     */
    private static long count = 0;

    volatile boolean flag = false;
    int a = 0;

    synchronized private void add() {
        int id = 0;
        while (id++ < 10000) {
            count += 1;
        }
    }

    public static void main(String[] args) {
        TestDemo1 ins = new TestDemo1();

        try {
            Thread th1 = new Thread(ins::add);
            Thread th2 = new Thread(ins::add);

            th1.start();
            th2.start();

            th1.join();
            th2.join();

            System.out.println(count);

        } catch (InterruptedException e) {
            System.out.println(e);
        }


    }
}
