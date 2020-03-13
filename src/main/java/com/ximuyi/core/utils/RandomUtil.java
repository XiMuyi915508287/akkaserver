package com.ximuyi.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class RandomUtil {

    public static int nextInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public static int randomBetween(int begin, int end) {
        if (begin >= end) {
            return begin;
        } else {
            return ThreadLocalRandom.current().nextInt(end - begin) + begin;
        }
    }

    public static <T> T nextItem(List<T> list, Function<T, Integer> function){
        int[] rates = new int[list.size()];
        int sum = 0;
        for (int i = 0; i < list.size(); i++) {
            int weight = function.apply(list.get(i));
            sum += weight;
            rates[i] = weight;
        }

        int randomRate = ThreadLocalRandom.current().nextInt(sum);
        int sumRate = 0;
        int index = -1;
        for (int j = 0; j < rates.length; j++) {
            sumRate += rates[j];
            if (randomRate < sumRate){
                index = j;
                break;
            }
        }
        return list.get(index);
    }

    public static <T> List<T> disorder(List<T> list){
        List<T> randomList = new ArrayList<>(list);
        if (randomList.size() < 2){
            return randomList;
        }
        int length = nextInt(list.size()) / 2;
        for (int i = 0; i < length; i++) {
            int j = randomBetween(length, randomList.size());
            T it = randomList.get(i);
            randomList.set(i, randomList.get(j));
            randomList.set(j, it);
        }
        return randomList;
    }
    
    
    public static void main(String[] args){
        for (int i = 0; i < 10; i++) {
            List<Integer> list = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                list.add(j);
            }
            System.out.println("排序前：" + list);
            System.out.println("排序后：" + disorder(list));
        }
    }
}
