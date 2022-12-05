package com._heap;

import java.util.Arrays;

/**
 * 二叉堆
 *
 * @author walker
 * @since 2022/12/6 01:22
 */
public class BinaryHeap {

    public static void main(String[] args) {
        int[] queue = new int[6];
        Arrays.fill(queue, 0);

        insert(0, 11, queue);
        print(queue);
        insert(1, 8, queue);
        print(queue);
        insert(2, 5, queue);
        print(queue);
        insert(3, 33, queue);
        print(queue);
        insert(4, 111, queue);
        print(queue);
        insert(5, 3, queue);
        print(queue);

        /*
         * 11,0,0,0,0,0,
         * 8,11,0,0,0,0,
         * 5,11,8,0,0,0,
         * 5,11,8,33,0,0,
         * 5,11,8,33,111,0,
         * 3,11,5,33,111,8,
         */
    }

    static void insert(int k, int value, int[] arr) {
        int parent;
        while (k > 0) {
            parent = (k - 1) >>> 1;
            if (value > arr[parent]) {
                break;
            }
            arr[k] = arr[parent];
            k = parent;
        }
        arr[k] = value;
    }

    static void print(int[] arr) {
        for (int j : arr) {
            System.out.print(j + ",");
        }
        System.out.println();
    }
}
