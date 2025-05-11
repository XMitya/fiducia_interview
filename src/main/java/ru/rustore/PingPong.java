package ru.rustore;

import java.util.concurrent.atomic.AtomicInteger;

public class PingPong {

    /*
     *   There will be two players (threads), who will print the texts "ping" and "pong".
     *   The thread "ping" will play first. The game is finished after N iterations.
     */
    public static void main(String[] args) throws InterruptedException {
        final int N = 3;
        final int maxIter = N * 2;
        final AtomicInteger iterations = new AtomicInteger(maxIter);
        final Object lock = new Object();
        final Player player1 = new Player("ping", false, iterations, lock);
        final Player player2 = new Player("pong", true, iterations, lock);

        player1.start();
        player2.start();

        player1.join();
        player2.join();

        System.out.println("Done");

    }

    private static class Player extends Thread {
        private final String message;
        private final boolean odd;
        private final AtomicInteger iterations;
        private final Object monitor;

        private Player(String message,
                       boolean odd,
                       AtomicInteger iterations,
                       Object monitor) {
            this.message = message;
            this.odd = odd;
            this.iterations = iterations;
            this.monitor = monitor;
        }

        @Override
        public void run() {
            while (!isInterrupted() && iterations.get() > 0) {
                synchronized (monitor) {
                    final int i = iterations.get();
                    if (odd == isOdd(i)) {
                        System.out.println(message);
                        iterations.decrementAndGet();

                        monitor.notify();
                    } else {
                        await();
                    }
                }
            }
        }

        private boolean isOdd(int value) {
            return (value & 1) == 1;
        }

        private void await() {
            try {
                monitor.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
