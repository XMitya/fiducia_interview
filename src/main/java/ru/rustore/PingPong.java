package ru.rustore;

import java.util.concurrent.atomic.AtomicInteger;

public class PingPong {

    /*
     *   There will be two players (threads), who will print the texts "ping" and "pong".
     *   The thread "ping" will play first. The game is finished after N iterations.
     */
    public static void main(String[] args) throws InterruptedException {
        final int N = 6;
        final int maxIter = N * 2;
        final AtomicInteger iterations = new AtomicInteger(1);
        final Object lock = new Object();
        final Player player1 = new Player("ping", true, iterations, 100, maxIter, lock);
        final Player player2 = new Player("pong", false, iterations, 100, maxIter, lock);

        player1.start();
        player2.start();

        player1.join();
        player2.join();

//        player1.interrupt();

        System.out.println("Done");

    }

    private static class Player extends Thread {
        private final String message;
        private final boolean odd;
        private final AtomicInteger iteration;
        private final long delay;
        private final int maxIteration;
        private final Object monitor;

        private Player(String message,
                       boolean odd,
                       AtomicInteger iteration,
                       long delay,
                       int maxIteration,
                       Object monitor) {
            this.message = message;
            this.odd = odd;
            this.iteration = iteration;
            this.delay = delay;
            this.maxIteration = maxIteration;
            this.monitor = monitor;
        }

        @Override
        public void run() {
            while (!isInterrupted() && iteration.get() <= maxIteration) {
                synchronized (monitor) {
                    final int i = iteration.get();
                    if (odd == isOdd(i)) {
                        System.out.println(message);
    //                    sleep_(delay);
                        iteration.incrementAndGet();
                        monitor.notify();
                    } else {
                        wait_();
                    }
                }
            }
        }

        private boolean isOdd(int value) {
            return value % 2 != 0;
        }

        private void wait_() {
            try {
                monitor.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private void sleep_(long delay) {
            try {
                sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
