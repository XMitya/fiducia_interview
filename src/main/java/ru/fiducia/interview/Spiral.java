package ru.fiducia.interview;

public class Spiral {
    /*
     * Fill the array with numbers from 1 to N like below:
     * N = 6
     * 1 20 19 18 17 16
     * 2 21 32 31 30 15
     * 3 22 33 36 29 14
     * 4 23 34 35 28 13
     * 5 24 25 26 27 12
     * 6  7  8  9 10 11
     *
     * N = 3
     * 1 8 7
     * 2 9 6
     * 3 4 5
     *
     * N = 2
     * 1 4
     * 2 3
     */
    public static void main(String[] args) {
        final int N = 6;
        int [][] field = new int[N][N];
        // code
        Direction direction = Direction.start();
        int i = 0;
        int j = 0;
        int count = 1;
        field[i][j] = count++;
        while (count <= N * N) {
            final Point nextPoint = direction.nextPoint(i, j);
            if (nextPoint.isOutOfBounds(N) || field[nextPoint.i()][nextPoint.j()] != 0) {
                direction = direction.nextDirection();
            } else {
                i = nextPoint.i();
                j = nextPoint.j();
                field[i][j] = count++;
            }
        }

        print(field);
    }


    public static void print(int[][] field) {
        for (int[] ints : field) {
            for (int i = 0; i < field.length; ++i) {
                System.out.print(ints[i] + " ");
            }
            System.out.println();
        }
    }

    private enum Direction {
        DOWN, RIGHT, UP, LEFT;

        private static final Direction[] values = values();

        public Point nextPoint(int i, int j) {
            return switch (this) {
                case DOWN -> new Point(i + 1, j);
                case RIGHT -> new Point(i, j + 1);
                case UP -> new Point(i - 1, j);
                case LEFT -> new Point(i, j - 1);
            };
        }

        public Direction nextDirection() {
            final int ordinal = ordinal();
            if (ordinal == values.length - 1) {
                return values[0];
            }

            return values[ordinal + 1];
        }

        public static Direction start() {
            return DOWN;
        }
    }

    private record Point(int i, int j) {
        public boolean isOutOfBounds(int limit) {
            return i >= limit || j >= limit || i < 0 || j < 0;
        }
    }
}