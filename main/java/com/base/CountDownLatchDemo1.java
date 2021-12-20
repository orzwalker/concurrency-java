package com.base;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 让多个线程并行执行，然后统一处理每个线程的结果
 *
 * @author walker
 * @since 2021/12/19 23:05
 */
public class CountDownLatchDemo1 {

    long getDoctorCnt() {
        try {
            Thread.sleep(1000);
            return 100;
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    long getPatientCnt() {
        try {
            Thread.sleep(2000);
            return 200;
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    long sum(long cnt1, long cnt2) {
        return cnt1 + cnt2;
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        CountDownLatchDemo1 ins = new CountDownLatchDemo1();
        AtomicLong doctorCnt = new AtomicLong();
        AtomicLong patientCnt = new AtomicLong();

        // 并行计算的线程太多时，需要花费大量资源创建Thread————考虑使用线程池
        Thread doctorTh = new Thread(() -> doctorCnt.set(ins.getDoctorCnt()));
        Thread patientTh = new Thread(() -> patientCnt.set(ins.getPatientCnt()));


        doctorTh.start();
        patientTh.start();


        try {
            doctorTh.join();
            patientTh.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        doctorCnt.getAndIncrement();

        long res = ins.sum(doctorCnt.get(), patientCnt.get());
        System.out.println(res + " consume:" + (System.currentTimeMillis() - startTime));

    }
}
