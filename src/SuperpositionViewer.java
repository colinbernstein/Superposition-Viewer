import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class SuperpositionViewer extends GraphicsInterface implements MouseListener {
    private JPanel displayPanel;
    private WaveEquationPanel waveEquationPanel;
    private static int currWaveIdx = -1;
    private static WaveType currWaveType = WaveType.PLANE;
    private static final double PIXEL_SIZE = 0.6;
    private static final int SLIDER_STEPS = 1000;
    
    
    private static final int PERIOD_MILLIS = 1;
    private List<WaveEquation> waves;
    private double time;
    private int X, Y;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SuperpositionViewer::createAndShowGUI);
    }
    
    private static void createAndShowGUI() {
        try {
            SuperpositionViewer viewer = new SuperpositionViewer(300, 300);
            Runnable advanceRunner = () -> {
                if (!viewer.waves.isEmpty())
                    for (Variable var : Variable.values()) {
                        try {
                            viewer.waves.get(currWaveIdx).setCoefficient(var, sliderRangeToVarRange(var, viewer.waveEquationPanel.sliderPanels[var.ordinal()].slider.getValue()));
                        } catch (Exception e) {
                            System.out.println("IllegalArgumentException" + "  " + var.getLabel() + " = " + viewer.waveEquationPanel.sliderPanels[var.ordinal()].slider.getValue());
                        }
                    }
                viewer.advance();
                //viewer.drawFrame();
                viewer.show(PERIOD_MILLIS);
            };
            
            ScheduledExecutorService advanceExecutor = Executors.newScheduledThreadPool(8);
            advanceExecutor.scheduleAtFixedRate(advanceRunner, 0, PERIOD_MILLIS, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private SuperpositionViewer(int X, int Y) {
        super();
        initViewer();
        waves = new ArrayList<>();
        time = 0.0;
        this.X = X;
        this.Y = Y;
        
        setXscale(0, X - 1);
        setYscale(1, Y);
        
        show(1);
    }
    
    
    private void advance() {
        time += (PERIOD_MILLIS * 0.001);
        for (int row = 0; row < Y; row++) {
            for (int col = 0; col < X; col++) {
                double sample = 0.0;
                if (!waves.isEmpty()) {
                    for (WaveEquation wave : waves)
                        sample += wave.evaluateEquation((double) col / X, (double) row / Y, time);
                    sample = sample / waves.size() / 2.0 + 0.5;
                    if (sample > 1.0 || sample < 0.0)       //DEBUG FOR OUT OF RANGE VALUES
                        setPenColor(Color.white);           //DEBUG FOR OUT OF RANGE VALUES
                    else
                        setPenColor(ColorScheme.transitionOfHueRange(sample, 0, 360, 0.85, 0.45));
                    filledSquare(col, Y - row, PIXEL_SIZE);
                } else {    //Empty, grey out screen
                    setPenColor(ColorScheme.transitionOfHueRange(0, 0, 360, 0.0, 0.1));
                    filledSquare(col, Y - row, PIXEL_SIZE);
                }
            }
        }
    }
    
    
    private void initViewer() {
        ImageIcon icon = new ImageIcon(onscreenImage);
        JLabel draw = new JLabel(icon);
        //draw.setLocation(0, -100);
        draw.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        draw.addMouseListener(this);
        //draw.addMouseMotionListener(std);
        
        waveEquationPanel = new WaveEquationPanel();
        frame.add(waveEquationPanel);
        
        displayPanel = new JPanel();
        displayPanel.setSize(width, height);
        displayPanel.setLocation(0, 0);
        //displayPanel.setBackground(Color.RED);
        frame.add(displayPanel);
        displayPanel.add(draw);
        displayPanel.setVisible(true);
        
        //panel.setVisible(true);
        //frame.addKeyListener(std);    // JLabel cannot get keyboard focus
        //frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);            // closes all windows
        frame.setTitle("Superposition Viewer");
        //frame.setJMenuBar(createMenuBar());
        frame.setSize(width, height);
        //frame.pack();
        frame.requestFocusInWindow();
        frame.setVisible(true);
    }
    
    private class WaveEquationPanel extends JPanel {
        JButton waveTypeButton, addButton, nextButton, prevButton, deleteButton;
        JLabel currentIdxLabel;
        SliderPanel[] sliderPanels;
        
        private class SliderPanel extends JPanel {
            JLabel label;
            JSlider slider;
            Variable var;
            
            private SliderPanel(Variable var) {
                super();
                this.var = var;
                label = new JLabel(var.getLabel());
                label.setBackground(Color.GREEN);
                
                slider = new JSlider(0, SLIDER_STEPS);
                slider.setLabelTable(var.getTickLabelMap());
                randomizeSliderPosition();
                slider.setPaintLabels(true);
                
                add(label);
                add(slider);
            }
            
            private void randomizeSliderPosition() {
                slider.setValue((int) (Math.random() * SLIDER_STEPS));
            }
            
            private void setSliderPosition() {
                slider.setValue(varRangeToSliderRange(var, waves.get(currWaveIdx).getVal(var)));
            }
            
            private void centerSliderPosition() {
                slider.setValue(SLIDER_STEPS / 2);
            }
        }
        
        private WaveEquationPanel() {
            super();
            setLayout(new FlowLayout());
            setSize(width, 125);
            setBackground(Color.GRAY);
            
            currentIdxLabel = new JLabel("Wave Number: 0");
            waveTypeButton = new JButton("Plane");
            addButton = new JButton("Add");
            nextButton = new JButton("Next");
            prevButton = new JButton("Prev");
            deleteButton = new JButton("Delete");
            
            waveTypeButton.addActionListener(e -> {
                currWaveType = currWaveType.nextLooping();
                waveTypeButton.setText(currWaveType.getLabel());
            });
            addButton.addActionListener(e -> {
                currWaveIdx++;
                switch (currWaveType) {
                    case PLANE: waves.add(new PlaneWaveEquation()); break;
                    case RADIAL: waves.add(new RadialWaveEquation()); break;
                }
                for (SliderPanel sliderPanel : sliderPanels)
                    sliderPanel.randomizeSliderPosition();
                currentIdxLabel.setText("Wave Number: " + (currWaveIdx + 1));
            });
            nextButton.addActionListener(e -> {
                if (currWaveIdx < waves.size() - 1) {
                    currWaveIdx++;
                    for (SliderPanel sliderPanel : sliderPanels)
                        sliderPanel.setSliderPosition();
                    currentIdxLabel.setText("Wave Number: " + (currWaveIdx + 1));
                }
            });
            prevButton.addActionListener(e -> {
                if (currWaveIdx > 0) {
                    currWaveIdx--;
                    for (SliderPanel sliderPanel : sliderPanels)
                        sliderPanel.setSliderPosition();
                    currentIdxLabel.setText("Wave Number: " + (currWaveIdx + 1));
                }
            });
            deleteButton.addActionListener(e -> {
                if (!waves.isEmpty()) {
                    waves.remove(currWaveIdx);
                    if (currWaveIdx == waves.size())
                        currWaveIdx--;
                    currentIdxLabel.setText("Wave Number: " + (currWaveIdx + 1));
                }
                if (waves.isEmpty())
                    for (SliderPanel sliderPanel : sliderPanels)
                        sliderPanel.centerSliderPosition();
            });
            add(currentIdxLabel);
            add(waveTypeButton);
            add(addButton);
            add(prevButton);
            add(nextButton);
            add(deleteButton);
            
            sliderPanels = new SliderPanel[Variable.values().length];
            for (byte i = 0; i < sliderPanels.length; i++) {
                sliderPanels[i] = new SliderPanel(Variable.values()[i]);
                add(sliderPanels[i]);
            }
            setVisible(true);
        }
        
    }
    
    /**
     * Converts a number in the slider range [0, SLIDER_STEPS] to a number in the range of a certain Variable
     * The result is the sum of a magnitude and phase shift
     *
     * @param var   Variable of the target range
     * @param value the slider value [0, SLIDER_STEPS] to be translated
     * @return the converted value in the target range
     */
    private static double sliderRangeToVarRange(Variable var, int value) {
        if (value < 0 || value > SLIDER_STEPS)
            throw new IllegalArgumentException();
        double targetRange = (var.getMax() - var.getMin());
        return (value * targetRange / SLIDER_STEPS) + var.getMin();
    }
    
    /**
     * Converts a number in a Variable range to a number in the slider range [0, SLIDER_STEPS]
     * The result is the sum of a magnitude and phase shift
     *
     * @param var   Variable corresponding to the current wave equation value
     * @param value the wave equation variable value to be translated back into to slider range [0, SLIDER_STEPS]
     * @return the converted value in the slider range
     */
    private static int varRangeToSliderRange(Variable var, double value) {
        if (value < var.getMin() || value > var.getMax())
            throw new IllegalArgumentException();
        return (int) ((value * SLIDER_STEPS / (var.getMax() - var.getMin())) + var.getMin());
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (!waves.isEmpty()) {
            WaveEquation wave = waves.get(currWaveIdx);
            if (wave instanceof RadialWaveEquation) {
                double x = (double) e.getX() / width;
                double y = (double) e.getY() / height;
                ((RadialWaveEquation) wave).setCenter(x, y);
            }
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
    }
}