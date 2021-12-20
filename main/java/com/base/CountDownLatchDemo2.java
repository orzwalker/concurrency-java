package com.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 使用线程池+CountDownLatch实现并行计算
 *
 * @author walker
 * @since 2021/12/19 23:14
 */
public class CountDownLatchDemo2 {

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        // 定长线程池
        Executor executor = Executors.newFixedThreadPool(2);

        // 计数器
        CountDownLatch countDownLatch = new CountDownLatch(2);

        CountDownLatchDemo2 ins = new CountDownLatchDemo2();
        AtomicLong doctorCnt = new AtomicLong();
        AtomicLong patientCnt = new AtomicLong();

        executor.execute(() -> {
            doctorCnt.set(ins.getDoctorCnt());
            countDownLatch.countDown();
        });
        executor.execute(() -> {
            patientCnt.set(ins.getPatientCnt());
            countDownLatch.countDown();
        });

        AtomicBoolean atomicBoolean = new AtomicBoolean();
        try {
            atomicBoolean.set(countDownLatch.await(3, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long res = ins.sum(doctorCnt.get(), patientCnt.get());
        System.out.println("success:" + atomicBoolean.get()
                + "  res:" + res
                + " consume:" + (System.currentTimeMillis() - startTime));
    }


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
}
