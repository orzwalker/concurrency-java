package com.base;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SafeDateFormat {
    static ThreadLocal<DateFormat> threadLocal = ThreadLocal.withInitial(
            () -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    static DateFormat get() {
        return threadLocal.get();
    }

    public static void main(String[] args) {
        new Thread(() -> System.out.println(SafeDateFormat.get())).start();
        new Thread(() -> System.out.println(SafeDateFormat.get())).start();
        new Thread(() -> System.out.println(SafeDateFormat.get())).start();
    }

}
