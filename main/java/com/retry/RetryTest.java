package com.retry;

/**
 * @author walker
 * @since 2022/11/28 23:21
 */
public class RetryTest {
    public static void main(String[] args) {
        abc:
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.println(j);
                if (j == 3) {
                    // continue abc;
                    break abc;
                }
            }
        }
        System.out.println("ok");
    }
}
