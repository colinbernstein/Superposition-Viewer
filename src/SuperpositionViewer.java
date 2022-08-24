import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SuperpositionViewer {
    private static final int PERIOD_MILLIS = 1;
    private Variable currentVar;
    private List<WaveEquation> waves;
    private final double scale;
    private double time;
    private int N, currWaveIdx;
    
    public static void main(String[] args) {
        try {
            //JFrame_sliderSine frame = new JFrame_sliderSine();
            SuperpositionViewer viewer = new SuperpositionViewer(300);
            
            Runnable advanceRunner = () -> {
                viewer.advance();
                viewer.drawFrame();
                StdDraw.show(PERIOD_MILLIS);
            };
            
            ScheduledExecutorService advanceExecutor = Executors.newScheduledThreadPool(8);
            advanceExecutor.scheduleAtFixedRate(advanceRunner, 0, PERIOD_MILLIS, TimeUnit.MILLISECONDS);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private SuperpositionViewer(int N) {
        currentVar = Variable.AMPLITUDE;
        waves = new ArrayList<>();
        scale = 2.0 / N;
        currWaveIdx = -1;
        time = 0.0;
        this.N = N;
        
        StdDraw.show(0);
        StdDraw.setCanvasSize(900, 900);
        StdDraw.setXscale(0, N - 1);
        StdDraw.setYscale(1, N);
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledSquare(N / 2.0, N / 2.0, N / 2.0);
    }
    
    private void drawFrame() {
        if (StdDraw.hasNextKeyTyped()) {
            int key = StdDraw.nextKeyTyped();
            switch (key) {
                case 'a': waves.add(new WaveEquation(new double[]{1.0, Math.random() * 20.0,
                        Math.random() * 20.0, Math.random(), Math.random() * 100.0, Math.random() * 2 * Math.PI, 0.0}));
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
                    System.exit(0);
                    return;
                default: System.out.println((char) key);
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
    }
    
    private void advance() {
        time += (PERIOD_MILLIS * 0.001);
        blackout();
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                double sample = 0.0;
                if (!waves.isEmpty()) {
                    for (WaveEquation wave : waves)
                        sample += wave.eval(new double[]{(-N / 2.0 + row) * scale, (-N / 2.0 + col) * scale}, time);
                    sample = sample / waves.size() + (1.0 / 2);
                    StdDraw.setPenColor(ColorScheme.transitionOfHueRange(sample, 0, 360, 0.85, 0.45));
                    StdDraw.filledSquare(col - 0.5, N - row + 0.5, 0.45);
                } else {
                    StdDraw.setPenColor(ColorScheme.transitionOfHueRange(0, 0, 360, 0.0, 0.1));
                    StdDraw.filledSquare(col - 0.5, N - row + 0.5, 0.45);
                }
            }
        }
    }
    
    private void blackout() {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(0.0, 0.0, N, N);
    }
}