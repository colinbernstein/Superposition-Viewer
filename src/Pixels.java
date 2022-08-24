import javax.sound.sampled.*;
import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Pixels {
    private static boolean appRunning = true;
    private static Variable currentVar = Variable.AMPLITUDE;
    private static double[][] grid;
    private static double stepTime = 50;
    private static final int N = 300;
    private static long last;
    private static List<WaveEquation> waves;
    private static long time = 0;
    private static double scale = 2.0 / N;
    
    
    public static void main(String[] args) {
        int currWaveIdx = -1;
        waves = new ArrayList<>();
        grid = new double[N][N];
        StdDraw.show(0);
        StdDraw.setCanvasSize(900, 900);
        StdDraw.setXscale(0, N - 1);
        StdDraw.setYscale(1, N);
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledSquare(N / 2.0, N / 2.0, N / 2.0);
        
        advance();
        
        while (appRunning) {
            if (StdDraw.hasNextKeyTyped()) {
                int key = StdDraw.nextKeyTyped();
                switch (key) {
                    case 'c': grid = new double[N][N];
                        break;
                    case 'a': waves.add(new WaveEquation(new double[]{1.0, Math.random() * 20.0,
                            Math.random() * 20.0, Math.random(), Math.random() * 0.5, Math.random() * 2 * Math.PI, 0.0}));
                        currWaveIdx++;
                        break;
                    case 'd':
                        if (waves.size() > 0)
                            waves.remove(currWaveIdx);
                        if (currWaveIdx >= waves.size())
                            currWaveIdx--;
                        break;
                    case 'o':
                        if (currWaveIdx > 0)
                            currWaveIdx--;
                        break;
                    case 'p':
                        if (currWaveIdx < waves.size() - 1)
                            currWaveIdx++;
                        break;
                    case 'n': currentVar = currentVar.next();
                        break;
                    case 'w': if (currWaveIdx >= 0) {
                        WaveEquation wave = waves.get(currWaveIdx);
                        int ord = currentVar.ordinal();
                        if (wave.getVal(ord) + 0.2 <= currentVar.getMax())
                            wave.mutate(ord, wave.getVal(ord) + 0.2);
                    }
                        break;
                    case 's': if (currWaveIdx >= 0) {
                        WaveEquation wave = waves.get(currWaveIdx);
                        int ord = currentVar.ordinal();
                        if (wave.getVal(ord) - 0.2 >= currentVar.getMin())
                            wave.mutate(ord, wave.getVal(ord) - 0.2);
                    }
                        break;
                    case 'q':
                        appRunning = false;
                        System.exit(0);
                        return;
                    default: System.out.println((char) key);
                    
                }
                
            }
            
            if (System.currentTimeMillis() - last > stepTime) {
                last = System.currentTimeMillis();
                advance();
            }
            //StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.filledSquare(N / 2.0, N / 2.0, N / 2.0);
            for (int row = 0; row < N; row++) {
                for (int col = 0; col < N; col++) {
                    StdDraw.setPenColor(ColorScheme.transitionOfHueRange(grid[row][col], 0, 360));
                    StdDraw.filledSquare(col - 0.5, N - row + 0.5, 0.45);
                }
            }
            
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.textLeft(25.0, 293.0, "Wave Number: " + (currWaveIdx + 1));
            
            StdDraw.setPenColor(Color.RED);
            StdDraw.square(25.0 + currentVar.ordinal() * 10.5, 285.0, 4.0);
            
            StdDraw.setPenColor(Color.WHITE);
            StringBuilder stringBuilder = new StringBuilder();
            for (Variable v : Variable.values()) {
                stringBuilder.append(v.getLabel());
                stringBuilder.append("   ");
            }
            StdDraw.textLeft(25.0, 285.0, stringBuilder.toString());
            if (waves.size() > 0)
                StdDraw.textLeft(25.0, 280.0, Double.toString(waves.get(currWaveIdx).getVal(currentVar.ordinal())));
            
            StdDraw.show(20);
            
        }
    }
    
    private static void advance() {
        time++;
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                grid[row][col] = 0.0;
                if (!waves.isEmpty()) {
                    for (WaveEquation wave : waves)
                        grid[row][col] += wave.eval(new double[]{(-N / 2.0 + row) * scale, (-N / 2.0 + col) * scale}, time);
                    grid[row][col] = (grid[row][col] / waves.size() + (1.0 / 2));
                }
            }
        }
    }
}