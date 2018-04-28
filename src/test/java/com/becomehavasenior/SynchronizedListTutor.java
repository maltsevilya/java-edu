package com.becomehavasenior;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;

/**
 * 1) Попробуйте запустить программу. Почему программа (периодически) падает
 *		 с ArrayIndexOutOfBoundException? Что надо сделать, чтобы этого не происходило?
 * 2) Теперь попробуйте уменьшить количество циклов в run() до 10 и
 * 		добавить вывод на печать print() после добавления нового элемента.
 * 		Почему происходит ConcurrentModificationException?
 * 		Что сделать, чтобы этого не происходило?
 *
 */
public class SynchronizedListTutor {
    static String [] langs =
            {"SQL", "PHP", "XML", "Java", "Scala",
                    "Python", "JavaScript", "ActionScript", "Clojure", "Groovy",
                    "Ruby", "C++"};

//    List<String> randomLangs = new ArrayList<>();
    Collection<String> randomLangs = new ArrayBlockingQueue<String>(100*10);

    public String getRandomLangs() {
        int index = (int)(Math.random()*langs.length);
        return langs[index];
    }

    class TestThread implements Runnable {
        String threadName;

        public TestThread(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public void run() {
//            for (int i = 0; i < 100000; i++) {
            for (int i = 0; i < 100; i++) {
//                synchronized (randomLangs) {
                    randomLangs.add(getRandomLangs());
                    print(randomLangs);
//                }
            }
//            List<String> res = new ArrayList<>(100000);
//            for (int i=0;i<100000;i++) {
//                res.add(getRandomLangs());
//            }
//            synchronized (randomLangs) {
//                randomLangs.addAll(res);
//            }
        }
    }

    public void print(Collection<?> c) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iterator = c.iterator();
        while(iterator.hasNext()) {
            builder.append(iterator.next());
            builder.append(" ");
        }
        System.out.println(builder.toString());
    }

    @Test
    public void testThread() {
        long start = System.nanoTime();
        List<Thread> threads = new ArrayList<Thread>();
        for (int i=0;i<100;i++) {
            threads.add(new Thread(new TestThread("t"+i), "t"+i));
        }
        System.out.println("Starting threads");
        for (int i=0;i<100;i++) {
            threads.get(i).start();
        }
        System.out.println("Waiting for threads");
        try {
            for (int i=0;i<100;i++) {
                threads.get(i).join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();
        BigDecimal bd = new BigDecimal((end - start)/1.0e9).round(new MathContext(4, RoundingMode.HALF_EVEN));
        System.out.println("Time: " + bd + " sec");
        randomLangs.stream().limit(1000).forEach(System.out::print);
//        print(new ArrayList<>(randomLangs).subList(0, 10000));
    }
}
