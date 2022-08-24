import java.awt.*;
import java.util.*;

public class TheGameOfLife {
    private static boolean[][] grid;
    private static double speed = 1.0;
    private static final int N = 100;
    private static long last;
    private static boolean holding;
    private static Rule rule = new Conway();

    private static void advance() {
        grid = rule.nextGeneration();
    }

    private static class Conway implements Rule {
        @Override
        public boolean[][] nextGeneration() {
            boolean[][] next = new boolean[N][N];
            for (int i = 0; i < N; i++)
                for (int j = 0; j < N; j++) {
                    int n = neighbors(i, j);
                    if (grid[i][j] && n < 2)
                        next[i][j] = false;
                    else if (grid[i][j] && n > 3)
                        next[i][j] = false;
                    else if (!grid[i][j] && n == 3)
                        next[i][j] = true;
                    else
                        next[i][j] = grid[i][j];
                }
            return next;
        }
    }

    private static int[] closest(int a, int b) {
        double shortestDist = Double.MAX_VALUE;
        int x = 0, y = 0;
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                if (i != a && j != b && grid[i][j]) {
                    double dist = Math.sqrt((a - i) * (a - i) + (b - j) * (b - j));
                    if (dist <= shortestDist) {
                        shortestDist = dist;
                        x = i;
                        y = j;
                    }
                }
            }
        return new int[]{x, y};
    }

    private static int neighbors(int i, int j) {
        int ret = 0;
        int[] dx = {0, 0, 1, -1, 1, 1, -1, -1};
        int[] dy = {1, -1, 0, 0, 1, -1, 1, -1};
        for (int a = 0; a < dx.length; a++)
            if (inBounds(i + dx[a], j + dy[a]))
                if (grid[i + dx[a]][j + dy[a]])
                    ret++;
        return ret;
    }

    private static boolean inBounds(int i, int j) {
        return i >= 0 && j >= 0 && i < N && j < N;
    }

    public static void main(String[] args) {
        grid = new boolean[N][N];
        StdDraw.show(0);
        StdDraw.setCanvasSize(1000, 1000);
        StdDraw.setXscale(0, N - 1);
        StdDraw.setYscale(1, N);
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledSquare(N / 2.0, N / 2.0, N / 2.0);

        while (true) {
            if (StdDraw.mousePressed()) {
                holding = true;
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();
                int i = (int) (N - Math.floor(y));
                int j = (int) (1 + Math.floor(x));
                if (i >= 0 && i < N && j >= 0 && j < N) {
                    grid[i][j] = true;
                }
            }
            if (!StdDraw.mousePressed() && !StdDraw.hasNextKeyTyped())
                holding = false;
            if (StdDraw.hasNextKeyTyped()) {
                int key = StdDraw.nextKeyTyped();
                if (!holding && key == 'w') {
                    holding = true;
                    advance();
                } else if (key == 'c')
                    grid = new boolean[N][N];
                else if (key == '=') {
                    for (int i = 0; i < N; i++)
                        for (int j = 0; j < N; j++) {
                            if (!grid[i][j])
                                grid[i][j] = Math.floor((Math.random()) * (Math.sqrt(i * i + j * j))) == 0;
                        }
                } else if (key == '-') {
                    for (int i = 0; i < N; i++)
                        for (int j = 0; j < N; j++) {
                            if (grid[i][j])
                                grid[i][j] = Math.floor((Math.random()) * (i + j)) != 0;
                        }
                } else if (key == '1') {
                    for (int i = 0; i < N; i++)
                        for (int j = 0; j < N; j++) {
                            grid[i][j] = (i + j) % 2 == 0;
                        }
                } else if (key == '2') {
                    for (int i = 0; i < N; i++)
                        for (int j = 0; j < N; j++) {
                            grid[i][j] = Math.cos(Math.sqrt(i*j)) > 0.5;
                        }
                }
                // speed *= 2.0;
                // else if (key == 's')
                //   speed /= 2.0;
            }
            if (System.currentTimeMillis() - last > speed * 1000) {
                last = System.currentTimeMillis();
                //advance();
            }
            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.filledSquare(N / 2.0, N / 2.0, N / 2.0);
            for (int row = 0; row < N; row++) {
                for (int col = 0; col < N; col++) {
                    if (grid[row][col]) {
                        StdDraw.setPenColor(StdDraw.RED);
                    } else {
                        StdDraw.setPenColor(StdDraw.DARK_GRAY);
                    }
                    StdDraw.filledSquare(col - 0.5, N - row + 0.5, 0.45);
                }
            }
            StdDraw.show(20);
        }

    }

    private static void reset(Color color) {
        for (int i = 0; i < 3; i++) {
            StdDraw.clear();
            StdDraw.setPenColor(color);
            StdDraw.filledSquare(N / 2.0, N / 2.0, N / 2.0);
            for (int row = 0; row < N; row++) {
                for (int col = 0; col < N; col++) {
                    StdDraw.filledSquare(col - 0.5, N - row + 0.5, 0.45);
                }
            }
            StdDraw.show(200);
            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.filledSquare(N / 2.0, N / 2.0, N / 2.0);
            for (int row = 0; row < N; row++) {
                for (int col = 0; col < N; col++) {
                    StdDraw.filledSquare(col - 0.5, N - row + 0.5, 0.45);
                }
            }
            StdDraw.show(200);
        }
        grid = new boolean[N][N];
    }

    private static Color colors(int value) {
        switch (value % 10) {
            case 0:
                return StdDraw.RED;
            case 1:
                return StdDraw.ORANGE;
            case 2:
                return StdDraw.YELLOW;
            case 3:
                return StdDraw.GREEN;
            case 4:
                return StdDraw.BLUE;
            case 5:
                return StdDraw.BOOK_BLUE;
            case 6:
                return StdDraw.BOOK_LIGHT_BLUE;
            case 7:
                return StdDraw.CYAN;
            case 8:
                return StdDraw.PINK;
            case 9:
                return StdDraw.MAGENTA;
        }
        return StdDraw.WHITE;
    }
}