package ru.otus.l131.thread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@SuppressWarnings("ALL")
public  class MergeSortThread {
    private static int SIZE_ARRAY ;
    private static int SIZE_THREADS = 4;
    private static List<Thread> pool_Threads = new ArrayList<>();
    private static Integer countThread = 0;
    static int[] array ;
    private static int SIZE_PART_ARRAY ;


    public synchronized static void sort(int[] arrays) {
        array = arrays;
        SIZE_ARRAY = arrays.length;
        if (SIZE_ARRAY == 0){
            return;
        }

        if (SIZE_ARRAY < 4){
            // Size very small;
            Arrays.sort(array);
            return;
        }

        SIZE_PART_ARRAY = SIZE_ARRAY % SIZE_THREADS != 0 ? SIZE_ARRAY / SIZE_THREADS + 1 : SIZE_ARRAY / SIZE_THREADS;

            for (int i = 0; i < SIZE_THREADS; i++) {
                pool_Threads.add(new Thread(() -> {
                    sortPartArray();
                }));
            }
            pool_Threads.forEach(x -> x.start());
            pool_Threads.forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });


        merge(0, SIZE_PART_ARRAY - 1, SIZE_PART_ARRAY * 2 - 1);
        merge(SIZE_PART_ARRAY * 2, SIZE_PART_ARRAY * 3 - 1, SIZE_ARRAY - 1);
        merge(0, SIZE_PART_ARRAY * 2 - 1, SIZE_ARRAY - 1);
    }

    private static void merge(int low, int mid, int high) {
        int[] left = new int[mid - low + 1];
        int[] right = new int[high - mid];

        int n1 = mid - low + 1, n2 = high - mid, i, j;

        for (i = 0; i < n1; i++)
            left[i] = array[i + low];

        for (i = 0; i < n2; i++)
            right[i] = array[i + mid + 1];

        int k = low;
        i = j = 0;


        while (i < n1 && j < n2) {
            if (left[i] <= right[j])
                array[k++] = left[i++];
            else
                array[k++] = right[j++];
        }


        while (i < n1) {
            array[k++] = left[i++];
        }


        while (j < n2) {
            array[k++] = right[j++];
        }
    }

    private static void sortPartArray() {
        int sizePart;
        int low;
        int high;

        synchronized (countThread) {
            sizePart = countThread++;
        }
            low = sizePart * (SIZE_PART_ARRAY);
            high = (sizePart + 1) * (SIZE_PART_ARRAY);

        if (high > SIZE_ARRAY) {
            high = SIZE_ARRAY;
        }
        int[] sortPartArrays = Arrays.copyOfRange(array, low, high);
        Arrays.sort(sortPartArrays);
        for (int i = low; i < high; i++) {
            array[i] = sortPartArrays[i - low];
        }

    }

}
