package com.becomehavasenior;

import org.junit.Test;

/**
 * Как остановить поток?
 * <p>
 * Для того, чтобы прервать поток, мы можем использовать флаг
 * shouldTerminate, который должен проверяться в цикле внутри run().
 * Если флаг становится true, мы просто выходим из цикла.
 * <p>
 * Однако, тут могут быть проблемы, если от нашего потока зависят другие потоки.
 * В настоящий момент поток t2 прерывается, и программа подвисает,
 * т.к. поток t1 ждет второй поток и не может дождаться.
 * Какие есть решения проблемы?
 */
public class WaitTerminateTutor {
    Thread t1, t2;
    Object monitor = new Object();
    int runningThreadNumber = 1;
    int countOfWorkingThreads = 0;

    class TestThread implements Runnable {
        String threadName;
        public boolean shouldTerminate;

        public TestThread(String threadName) {
            this.threadName = threadName;
            countOfWorkingThreads++;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                System.out.println(threadName + ":" + i);
                synchronized (monitor) {
                    try {
                        while (!threadName.equals("t" + runningThreadNumber)
                                && countOfWorkingThreads > 1) {
                            System.out.println("wait for thread " + "t" + runningThreadNumber);
                            monitor.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runningThreadNumber++;
                    if (runningThreadNumber > 2) runningThreadNumber = 1;
                    monitor.notifyAll();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (shouldTerminate) {
                        countOfWorkingThreads--;
                        return;
                    }
                }
            }
            countOfWorkingThreads--;
        }
    }

    @Test
    public void testThread() {
        TestThread testThread1 = new TestThread("t1");
        t1 = new Thread(testThread1, "t1");
        final TestThread testThread2 = new TestThread("t2");
        t2 = new Thread(testThread2, "t2");
        System.out.println("Starting threads...");
        t1.start();
        t2.start();

        Thread terminator = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                testThread2.shouldTerminate = true;
            }
        }, "terminator");
        terminator.start();

        System.out.println("Waiting threads to join...");
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
