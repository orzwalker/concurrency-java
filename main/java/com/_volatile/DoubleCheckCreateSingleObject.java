package com._volatile;

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

    static DoubleCheckCreateSingleObject ins;


//    static volatile DoubleCheckCreateSingleObject ins; // 禁止指令重排

    static DoubleCheckCreateSingleObject getInstance() {
        // 2.并发时，当其他线程进来时，ins非空，直接return，但是对象还没有初始化
        if (null == ins) {
            synchronized (DoubleCheckCreateSingleObject.class) {
                if (null == ins) {
                    // 1.编译器优化时，先将对象的内存地址赋值给ins，然后再进行对象初始化
                    ins = new DoubleCheckCreateSingleObject();
                    return ins;
                }
            }
        }
        return ins;
    }


    public static void main(String[] args) {
        try {
            for (int i = 0; i < 100; i++) {
                new Thread(() -> {
                    System.out.println(Thread.currentThread().getName() + "==" + getInstance());
                }).start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
