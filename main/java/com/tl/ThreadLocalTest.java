package com.tl;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测试引入线程池后，标识符在父子线程之间复制的问题
 *
 * @author walker
 * @since 2022/6/10 08:54
 */
public class ThreadLocalTest {


    /**
     * Thread-0:0
     * Thread-1:1
     * Thread-2:2
     * Thread-3:3
     * Thread-4:4
     * parent:Thread-4:null, tomcat-thread:pool-1-thread-1
     * parent:Thread-3:null, tomcat-thread:pool-1-thread-2
     * parent:Thread-1:null, tomcat-thread:pool-1-thread-3
     * parent:Thread-2:null, tomcat-thread:pool-1-thread-3
     * parent:Thread-0:null, tomcat-thread:pool-1-thread-3
     */
//    private static final ThreadLocal<Integer> TL = new ThreadLocal<>();

    /**
     * Thread-1:1
     * Thread-4:4
     * Thread-3:3
     * Thread-2:2
     * Thread-0:0
     * parent:Thread-3:3, tomcat-thread:pool-1-thread-1
     * parent:Thread-1:1, tomcat-thread:pool-1-thread-3
     * parent:Thread-0:3, tomcat-thread:pool-1-thread-1
     * parent:Thread-2:1, tomcat-thread:pool-1-thread-3
     * parent:Thread-4:4, tomcat-thread:pool-1-thread-2
     */
//    private static final ThreadLocal<Integer> TL = new InheritableThreadLocal<>();

    /**
     * Thread-1:0
     * Thread-3:2
     * Thread-5:4
     * Thread-4:3
     * Thread-2:1
     * parent:Thread-2:1, tomcat-thread:pool-1-thread-3
     * parent:Thread-4:3, tomcat-thread:pool-1-thread-1
     * parent:Thread-1:0, tomcat-thread:pool-1-thread-3
     * parent:Thread-5:4, tomcat-thread:pool-1-thread-2
     * parent:Thread-3:2, tomcat-thread:pool-1-thread-1
     */
    private static final ThreadLocal<Integer> TL = new TransmittableThreadLocal<>();

    static ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(3);
    // 模拟Tomcat线程池--3个工作线程
    private static final ExecutorService threadPool = TtlExecutors.getTtlExecutorService(threadPoolExecutor);

    public static void main(String[] args) {
        // 提交5个任务给Tomcat线程池
        for (int i = 0; i < 5; i++) {
            new TomcatThread(i).start();
        }
    }


    /**
     * 父线程----提交任务让Tomcat执行的线程
     */
    static class TomcatThread extends Thread {
        private final int index;

        public TomcatThread(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            // 父线程将标识添加到线程上下文变量TL
            TL.set(index);
            String name = Thread.currentThread().getName();
            System.out.println(name + ":" + index);
            BusinessTask businessTask = new BusinessTask(name);
            // 提交到线程池，线程池执行业务任务
            assert threadPool != null;
            // 线程池工作线程拿到任务，开始执行
            threadPool.execute(Objects.requireNonNull(TtlRunnable.get(businessTask)));
        }
    }

    /**
     * tomcat通过线程池去执行业务任务
     */
    static class BusinessTask implements Runnable {
        private final String parentThreadName;

        public BusinessTask(String parentThreadName) {
            this.parentThreadName = parentThreadName;
        }

        @Override
        public void run() {
            System.out.println("parent:" + parentThreadName + ":" + TL.get() + ", tomcat-thread:" + Thread.currentThread().getName());
        }
    }

}
