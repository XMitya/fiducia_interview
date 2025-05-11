package ru.rustore;

class Spiral {
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
    /*
    0,0 | 0,1 | 0,2 | 1,2 | 2,2 | 2,1 | 2,0 | 1,0 | 1,1
     */
    public static void main(String[] args) {
        final int N = 4;
        int [][] field = new int[N][N];
        // code
        Direction direction = Direction.DOWN;
        int i = 0;
        int j = 0;
        int count = 1;
        field[0][0] = count++;
        while (count <= N * N) {
            final Point nextPoint = direction.nextPoint(i, j);
            if (nextPoint.i() >= N || nextPoint.j() >= N || nextPoint.i() < 0 || nextPoint.j() < 0) {
                direction = direction.nextDirection();
                continue;
            }
            if (field[nextPoint.i()][nextPoint.j()] != 0) {
                direction = direction.nextDirection();
                continue;
            }

            i = nextPoint.i();
            j = nextPoint.j();
            field[i][j] = count++;
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

        public Point nextPoint(int i, int j) {
            return switch (this) {
                case DOWN -> new Point(i + 1, j);
                case RIGHT -> new Point(i, j + 1);
                case UP -> new Point(i - 1, j);
                case LEFT -> new Point(i, j - 1);
            };
        }

        public Direction nextDirection() {
            final Direction[] values = values();
            for (int i = 0; i < values.length; i++) {
                if (this == values[i]) {
                    if (i == values.length - 1) {
                        return values[0];
                    }

                    return values[i + 1];
                }
            }

            throw new IllegalStateException();
        }
    }

    private record Point(int i, int j) {
    }
}