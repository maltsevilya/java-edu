package com.becomehavasenior;

import org.junit.Test;

/**
 * Как сделать так, чтобы потоки вызывались по очереди?
 * <p>
 * Часто необходимо упорядочить потоки, т.к. результат одного потока
 * понадобится другому, и нужно дождаться, когда первый поток сделает свою работу.
 * <p>
 * Задача: добавьте еще один поток, который будет выводить в лог сообщения о
 * значениях счетчика, кратных 10, например 10, 20, 30...
 * При этом такие сообщения должны выводиться после того, как все потоки преодолели
 * кратность 10, но до того, как какой-либо поток двинулся дальше.
 */
public class WaitTutor {
    private final Object monitor = new Object();
    private int t1Counter = 0;
    private int t2Counter = 0;
    private int maxCounter = 100;

    class PrintThread implements Runnable {
        @Override
        public void run() {
            while (hasJob()) {
                synchronized (monitor) {
                    if (t1Counter == t2Counter && t1Counter % 10 == 0) {
                        System.out.printf("Print thread: Counter is %d, equals = true%n", t1Counter);
                        monitor.notifyAll();
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        monitor.notifyAll();
                    }
                }
            }
        }

        private boolean hasJob() {
            return t1Counter < maxCounter - 1 && t2Counter < maxCounter - 1;
        }
    }

    class TestThread implements Runnable {
        String threadName;
        int n;

        TestThread(String threadName, int n) {
            this.threadName = threadName;
            this.n = n;
        }

        @Override
        public void run() {
            for (int i = 0; i < maxCounter; i++) {
                System.out.println(threadName + ":" + i);
                synchronized (monitor) {
                    try {
                        if (t1Counter == t2Counter) {
                            //System.out.println(threadName + " stop at " + t1Counter);
                            monitor.wait();
                        }
                        if (n == 1) t1Counter = i;
                        if (n == 2) t2Counter = i;
                        monitor.notify();
                        Thread.yield();
                        if (n == 1) {
                            if (i > t2Counter) {
                                System.out.println("t1 is ahead with i=" + i + ", wait for t2Counter=" + t2Counter);
                                monitor.wait();
                            }
                        }
                        if (n == 2) {
                            if (i > t1Counter) {
                                System.out.println("t2 is ahead with i=" + i + ", wait for t1Counter=" + t1Counter);
                                monitor.wait();
                            }
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                Thread.yield();
            }
        }
    }

    @Test
    public void testThread() {
        Thread t1 = new Thread(new TestThread("t1", 1), "t1");
        Thread t2 = new Thread(new TestThread("t2", 2), "t2");
        Thread printThread = new Thread(new PrintThread(), "print");
        System.out.println("Starting threads");
        t1.start();
        t2.start();
        printThread.start();

        System.out.println("Waiting for threads");
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
