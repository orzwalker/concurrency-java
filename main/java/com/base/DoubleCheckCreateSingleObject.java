package com.base;

/**
 * @author walker
 * @since 2021/12/10 15:06
 */
public class DoubleCheckCreateSingleObject {

    /**
     * 多线程并发调getInstance方法创建单例对象，进行「双重检测」
     * 编译优化时，先将对象地址赋值给ins变量，后初始化对象
     * 并发时，ins的结果可能为空
     * 所以需要volatile修饰变量，禁止「指令重排」
     */
    // static DoubleCheckCreateSingleObject ins;


    static volatile DoubleCheckCreateSingleObject ins; // 禁止指令重排

    static DoubleCheckCreateSingleObject getInstance() {
        if (null == ins) {
            synchronized (DoubleCheckCreateSingleObject.class) {
                if (null == ins) {
                    ins = new DoubleCheckCreateSingleObject();
                    return ins;
                }
            }
        }
        return ins;
    }


    public static void main(String[] args) {
        try {
            Thread th1 = new Thread(() -> {
                DoubleCheckCreateSingleObject instance = getInstance();
                System.out.println(instance.toString());
            });

            Thread th2 = new Thread(() -> {
                DoubleCheckCreateSingleObject instance = getInstance();
                System.out.println(instance.toString());
            });

            Thread th3 = new Thread(() -> {
                DoubleCheckCreateSingleObject instance = getInstance();
                System.out.println(instance.toString());
            });

            th1.start();
            th2.start();
            th3.start();

            th1.join();
            th2.join();
            th3.join();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
