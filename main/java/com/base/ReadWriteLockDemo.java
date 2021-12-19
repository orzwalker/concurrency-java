package com.base;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 使用读写锁实现简易缓存
 * 支持锁降级，不支持锁升级
 *
 * @author walker
 * @since 2021/12/19 18:22
 */
public class ReadWriteLockDemo<K, V> {

    ReadWriteLock rwl;
    Lock readLock;
    Lock writeLock;

    // hashmap非线程安全
    // 使用map作为缓存，并保证读写map的操作线程安全
    Map<K, V> cache = new HashMap<>();

    ReadWriteLockDemo() {
        rwl = new ReentrantReadWriteLock();
        readLock = rwl.readLock();
        writeLock = rwl.writeLock();
    }

    /**
     * 写操作
     */
    String set(K key, V value) {
        writeLock.lock();
        try {
            System.out.println("set " + key + " " + value);
            cache.put(key, value);
        } finally {
            writeLock.unlock();
        }
        return "success";
    }

    /**
     * 读操作
     */
    V get(K key) {
        readLock.lock();
        try {
            V v = cache.get(key);
            System.out.println(v);
            return v;
        } finally {
            readLock.unlock();
        }
    }

    public static void main(String[] args) {
        ReadWriteLockDemo<String, String> ins = new ReadWriteLockDemo<>();
        new Thread(() -> ins.set("key1", "value1")).start();
        new Thread(() -> ins.set("key2", "value2")).start();
        new Thread(() -> ins.set("key3", "value3")).start();
        new Thread(() -> ins.set("key4", "value4")).start();
        new Thread(() -> ins.set("key5", "value5")).start();

        new Thread(() -> ins.get("key1")).start();
        new Thread(() -> ins.get("key1")).start();
        new Thread(() -> ins.get("key1")).start();
    }

}
