import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.*;
import java.awt.*;

public class CellularAutomation {
    private static byte[][] grid;
    private static final int N = 1024;
    private static int level = 0;
    private static boolean holding;
    private static Rule30 rule = new Rule30();
    private static Code912 code = new Code912();
    private static FlowYN flow = new FlowYN();
    private static AudioFormat format;
    
    private static void advance() {
        grid[level + 1] = flow.nextGeneration(level++);
        //audio(grid[level]);
    }
    
    private static class Rule30 {
        byte[] nextGeneration(int level) {
            byte[] next = new byte[N];
            for (int i = level + 1; i < N - level - 1; i++) {
                byte data = 0x0;
                for (int dx = -1; dx <= 1; dx++) {
                    if (grid[level][i - dx] == 1)
                        data |= 0x1;
                    if (dx != 1) data <<= 1;
                }
                switch (data) {
                    case 0x001: next[i] = 1;
                    case 0x002: next[i] = 1;
                    case 0x003: next[i] = 1;
                    case 0x004: next[i] = 1;
                }
            }
            return next;
        }
    }
    
    private static class Code912 {
        byte[] nextGeneration(int level) {
            byte[] next = new byte[N];
            for (int i = level + 1; i < N - level - 1; i++) {
                byte total = 0;
                for (byte dx = -1; dx <= 1; dx++)
                    total += grid[level][i + dx];
                switch (total) {
                    case 1:
                    case 6: next[i] = 0x1; break;
                    case 2:
                    case 4: next[i] = 0x2; break;
                }
            }
            return next;
        }
    }
    
    private static class FlowYN {
        byte[] nextGeneration(int level) {
            byte[] next = new byte[N];
            for (int i = 1; i < N - 1; i++) {
                byte total = 0;
                for (byte dx = -1; dx <= 1; dx++)
                    total += grid[level][i + dx];
                if (total < 4) next[i] = 0x1;
                else if (total >= 5 && total < 9) next[i] = 0x9;
                else if (total >= 9 && total < 15) next[i] = 0x5;
                else if (total >= 15 && total < 20) next[i] = 0x2;
            }
            return next;
        }
    }
    
    private static class FlowYN2 {
        byte[] nextGeneration(int level) {
            byte[] next = new byte[N];
            for (int i = 1; i < N - 1; i++) {
                byte total = 0;
                for (byte dx = -1; dx <= 1; dx++)
                    total += grid[level][i + dx];
                if (total < 3) next[i] = 0x2;
                else if (total < 6) next[i] = 0x1;
                else if (total < 9) next[i] = 0x3;
                else if (total < 12) next[i] = 0x6;
                else if (total < 15) next[i] = 0x7;
                else if (total < 18) next[i] = 0x8;
                else if (total < 21) next[i] = 0x9;
                else if (total < 24) next[i] = 0x4;
                else if (total < 27) next[i] = 0x0;
            }
            return next;
        }
    }
    
    private static class Flow2 {
        byte[] nextGeneration(int level) {
            byte[] next = new byte[N];
            for (int i = 1; i < N - 1; i++) {
                byte total = 15;
                for (byte dx = -1; dx <= 1; dx++)
                    total -= grid[level][i + dx];
                if (total < 4) next[i] = 0x1;
                else if (total >= 5 && total < 9) next[i] = 0x9;
                else if (total >= 9 && total < 15) next[i] = 0x5;
                else if (total >= 15 && total < 20) next[i] = 0x2;
            }
            return next;
        }
    }
    
    private static void setInitialCond() {
        for (int c = 0; c < N / 2; c++)
            grid[0][c] = (byte) (Math.floor(Math.random() * 10));
        for (int c = N / 2; c < N; c++)
            grid[0][c] = grid[0][N - c];
    }
    
    public static void main(String[] args) {
        grid = new byte[N][N];
        StdDraw.show(0);
        StdDraw.setCanvasSize(1000, 1000);
        StdDraw.setXscale(0, N - 1);
        StdDraw.setYscale(1, N);
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledSquare(N / 2.0, N / 2.0, N / 2.0);
        setInitialCond();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledSquare(N / 2.0, N / 2.0, N / 2.0);
        for (int row = 0; row < N; row++)
            for (int col = 0; col < N; col++)
                StdDraw.filledSquare(col - 0.5, N - row + 0.5, 0.45);
        while (true) {
            if (!holding && StdDraw.mousePressed()) {
                holding = true;
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();
                int i = (int) (N - Math.floor(y));
                int j = (int) (1 + Math.floor(x));
                if (i >= 0 && i < N && j >= 0 && j < N) {
                    if (grid[i][j] < 2)
                        grid[i][j]++;
                    else
                        grid[i][j] = 0;
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
                    grid = new byte[N][N];
            }
            if (level < N - 1) {advance();}
            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.filledSquare(N / 2.0, N / 2.0, N / 2.0);
            for (int row = 0; row < N; row++) {
                for (int col = 0; col < N; col++) {
                    StdDraw.setPenColor(colors(grid[row][col]));
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
        grid = new byte[N][N];
    }
    /*
    private static void audio(byte[] in) {
        int channel = 0; // 0 is a piano, 9 is percussion, other channels are for other instruments
        
        int volume = 80; // between 0 et 127
        int duration = 1; // in milliseconds
        
        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            MidiChannel[] channels = synth.getChannels();
            
            // --------------------------------------
            // Play a few notes.
            // The two arguments to the noteOn() method are:
            // "MIDI note number" (pitch of the note),
            // and "velocity" (i.e., volume, or intensity).
            // Each of these arguments is between 0 and 127.
            for (int i = 0; i < in.length; i++) {
                channels[channel].noteOn(in[i] * 10, volume); // C note
                Thread.sleep(duration);
                channels[channel].noteOff(in[i]);
            }
            channels[channel].allNotesOff();
            synth.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void play(SourceDataLine line, Note note, int ms) {
        ms = Math.min(ms, Note.SECONDS * 1000);
        int length = Note.SAMPLE_RATE * ms / 1000;
        int count = line.write(note.data(), 0, length);
    }*/
    
    private static Color colors(int value) {
        switch (value % 10) {
            case 0:
                return StdDraw.GRAY;
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
