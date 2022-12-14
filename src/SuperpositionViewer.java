import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class SuperpositionViewer {
    private static final double PIXEL_SIZE = 0.6;
    private static final int SLIDER_STEPS = 1000;
    
    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 1000;
    private static final int GRAPHICS_WIDTH = 1000;
    private static final int GRAPHICS_HEIGHT = 875;
    
    private static final int PERIOD_MILLIS = 1;
    
    private static final Color GREY = new Color(40, 40, 40);
    
    private JFrame frame;
    private GraphicsFrame graphicsFrame;
    private ColorScheme colorScheme;
    private WaveEquationPanel waveEquationPanel;
    private WaveType currWaveType;
    private List<WaveEquation> waves;
    private int currWaveIdx;
    private double time;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SuperpositionViewer::createAndShowGUI);
    }
    
    private static void createAndShowGUI() {
        SuperpositionViewer viewer = new SuperpositionViewer();
        ActionListener scheduledAdvance = evt -> viewer.advance();
        new Timer(PERIOD_MILLIS, scheduledAdvance).start();
    }
    
    private SuperpositionViewer() {
        super();
        initViewer();
        colorScheme = new ClassicColorScheme(0, 360, 0.85, 0.45);
        waves = new ArrayList<>();
        currWaveIdx = -1;
        currWaveType = WaveType.PLANE;
        time = 0.0;
    }
    
    private void initViewer() {
        frame = new JFrame();
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        //frame.setResizable(false);
        graphicsFrame = new GraphicsFrame(GRAPHICS_WIDTH, GRAPHICS_HEIGHT);
        frame.setLayout(new BorderLayout());
        
        ImageIcon icon = new ImageIcon(graphicsFrame.getOnscreenImage());
        JLabel draw = new JLabel(icon);
        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                synchronized (graphicsFrame.mouseLock) {
                    if (!waves.isEmpty()) {
                        WaveEquation wave = waves.get(currWaveIdx);
                        if (wave instanceof RadialWaveEquation) {
                            double x = (double) e.getX() / graphicsFrame.width;
                            double y = (double) e.getY() / graphicsFrame.width;
                            ((RadialWaveEquation) wave).setCenter(x, y);
                        }
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
        };
        draw.addMouseListener(mouseListener);
        
        JPanel graphicsPanel = new JPanel();
        graphicsPanel.setSize(graphicsFrame.width, graphicsFrame.height);
        graphicsPanel.setLocation(0, FRAME_HEIGHT - GRAPHICS_HEIGHT);
        graphicsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        graphicsPanel.setBackground(Color.GRAY);
        frame.add(graphicsPanel);
        graphicsPanel.add(draw, BorderLayout.CENTER);
        graphicsPanel.setVisible(true);
        
        waveEquationPanel = new WaveEquationPanel();
        frame.add(waveEquationPanel);
        
        //frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);            // closes all windows
        frame.setTitle("Superposition Viewer");
        //ADD COLOR SCHEME SELECTOR
        //frame.setJMenuBar(new JMenuBar());
        //frame.pack();
        frame.requestFocusInWindow();
        frame.setVisible(true);
    }
    
    private class WaveEquationPanel extends JPanel {
        JPanel buttonCollection, sliderCollection;
        JButton waveTypeButton, addButton, nextButton, prevButton, deleteButton;
        JLabel currentIdxLabel;
        SliderPanel[] sliderPanels;
        
        private class SliderPanel extends JPanel {
            JLabel label;
            JSlider slider;
            Variable var;
            ChangeListener sliderListener;
            
            private SliderPanel(Variable var) {
                super();
                this.var = var;
                
                label = new JLabel(var.getLabel());
                label.setAlignmentY(JLabel.TOP);
                //label.setBackground(Color.GREEN);
                
                slider = new JSlider(0, SLIDER_STEPS);
                slider.setPreferredSize(new Dimension(150, 40));
                slider.setLabelTable(var.getTickLabelMap());
                //randomizeSliderPosition();
                slider.setPaintLabels(true);
                
                sliderListener = e -> {
                    if (!waves.isEmpty()) {
                        try {
                            waves.get(currWaveIdx).setCoefficient(var, sliderRangeToVarRange(var,
                                    waveEquationPanel.sliderPanels[var.ordinal()].slider.getValue()));
                        } catch (Exception ex) {
                            System.out.println("IllegalArgumentException" + "  " + var.getLabel() + " = " +
                                    waveEquationPanel.sliderPanels[var.ordinal()].slider.getValue());
                        }
                    }
                };
                slider.addChangeListener(sliderListener);
                
                add(label);
                add(slider);
                //setBackground(Color.LIGHT_GRAY);
            }
            
            private void randomizeSliderPosition() {
                if (var == Variable.AMPLITUDE)
                    slider.setValue((int) ((Math.random() * (SLIDER_STEPS * 5.0 / 6.0)) + (SLIDER_STEPS / 6.0)));
                else
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
            setSize(graphicsFrame.width, 125);
            setBackground(Color.GRAY);
            
            JMenuBar menuBar = new JMenuBar();
            JMenu colorSchemeMenu = new JMenu("Color Scheme");
            
            JMenuItem classic_scheme = new JMenuItem("Classic");
            JMenuItem heat_scheme = new JMenuItem("Heat");
            JMenuItem black_white_scheme = new JMenuItem("B&W");
            JMenuItem black_yellow_scheme = new JMenuItem("B&Y");
    
            ActionListener colorSchemeListener = e -> {
                switch (e.getActionCommand()) {
                    case "Classic":
                        colorScheme = new ClassicColorScheme(0, 360, 0.85, 0.45);
                        break;
                    case "Heat":
                        colorScheme = new ClassicColorScheme(0, 60, 0.8, 0.4);
                        break;
                    case "B&W":
                        colorScheme = new ClassicColorScheme(0, 360, 0.0, 0.5);
                        break;
                    case "B&Y":
                        colorScheme = new BlackToColorColorScheme(60, 1.0, 0.5);
                        break;
                    default:
                        System.out.println("Undefined color scheme selected: " + e.getActionCommand());
                        break;
                }
            };
            
            classic_scheme.addActionListener(colorSchemeListener);
            heat_scheme.addActionListener(colorSchemeListener);
            black_white_scheme.addActionListener(colorSchemeListener);
            black_yellow_scheme.addActionListener(colorSchemeListener);
            
            colorSchemeMenu.add(classic_scheme);
            colorSchemeMenu.add(heat_scheme);
            colorSchemeMenu.add(black_white_scheme);
            colorSchemeMenu.add(black_yellow_scheme);
            
            menuBar.add(colorSchemeMenu);
            frame.setJMenuBar(menuBar);
            
            
            currentIdxLabel = new JLabel("Wave Number: 0");
            waveTypeButton = new JButton("Plane");
            addButton = new JButton("Add");
            nextButton = new JButton("Next");
            prevButton = new JButton("Prev");
            deleteButton = new JButton("Delete");
            
            waveTypeButton.addActionListener(e -> {
                synchronized (graphicsFrame.mouseLock) {
                    SwingUtilities.invokeLater(() -> {
                        currWaveType = currWaveType.nextLooping();
                        if (!waves.isEmpty()) {
                            WaveEquation waveEquation = waves.get(currWaveIdx);
                            if (waveEquation instanceof PlaneWaveEquation)
                                waveEquation = ((PlaneWaveEquation) waveEquation).toRadialWave();
                            else if (waveEquation instanceof RadialWaveEquation)
                                waveEquation = ((RadialWaveEquation) waveEquation).toPlaneWave();
                            waves.set(currWaveIdx, waveEquation);
                        }
                        waveTypeButton.setText(currWaveType.getLabel());
                    });
                }
            });
            addButton.addActionListener(e -> {
                synchronized (graphicsFrame.mouseLock) {
                    SwingUtilities.invokeLater(() -> {
                        currWaveIdx++;
                        switch (currWaveType) {
                            case PLANE: waves.add(new PlaneWaveEquation()); break;
                            case RADIAL: waves.add(new RadialWaveEquation()); break;
                        }
                        for (SliderPanel sliderPanel : sliderPanels)
                            sliderPanel.randomizeSliderPosition();
                        currentIdxLabel.setText("Wave Number: " + (currWaveIdx + 1));
                        
                        for (Variable var : Variable.values()) {
                            try {
                                waves.get(currWaveIdx).setCoefficient(var, sliderRangeToVarRange(var,
                                        waveEquationPanel.sliderPanels[var.ordinal()].slider.getValue()));
                            } catch (Exception ex) {
                                System.out.println("IllegalArgumentException" + "  " + var.getLabel() + " = " +
                                        waveEquationPanel.sliderPanels[var.ordinal()].slider.getValue());
                            }
                        }
                    });
                }
            });
            nextButton.addActionListener(e -> {
                synchronized (graphicsFrame.mouseLock) {
                    SwingUtilities.invokeLater(() -> {
                        if (currWaveIdx < waves.size() - 1) {
                            currWaveIdx++;
                            for (SliderPanel sliderPanel : sliderPanels)
                                sliderPanel.setSliderPosition();
                            currentIdxLabel.setText("Wave Number: " + (currWaveIdx + 1));
                        }
                    });
                }
            });
            prevButton.addActionListener(e -> {
                synchronized (graphicsFrame.mouseLock) {
                    SwingUtilities.invokeLater(() -> {
                        if (currWaveIdx > 0) {
                            currWaveIdx--;
                            for (SliderPanel sliderPanel : sliderPanels)
                                sliderPanel.setSliderPosition();
                            currentIdxLabel.setText("Wave Number: " + (currWaveIdx + 1));
                        }
                    });
                }
            });
            deleteButton.addActionListener(e -> {
                synchronized (graphicsFrame.mouseLock) {
                    SwingUtilities.invokeLater(() -> {
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
                }
            });
            
            buttonCollection = new JPanel();
            sliderCollection = new JPanel(new GridLayout(1, 6, 10, 0));
            sliderCollection.setPreferredSize(new Dimension(FRAME_WIDTH - 50, 70));
            sliderCollection.setBackground(Color.GRAY);
            
            buttonCollection.add(currentIdxLabel);
            buttonCollection.add(waveTypeButton);
            buttonCollection.add(addButton);
            buttonCollection.add(prevButton);
            buttonCollection.add(nextButton);
            buttonCollection.add(deleteButton);
            
            sliderPanels = new SliderPanel[Variable.values().length];
            for (byte i = 0; i < sliderPanels.length; i++) {
                sliderPanels[i] = new SliderPanel(Variable.values()[i]);
                sliderCollection.add(sliderPanels[i]);
            }
            
            add(buttonCollection);
            add(sliderCollection);
            
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
    
    private void advance() {
        time += (PERIOD_MILLIS * 0.001);
        for (int row = 0; row < graphicsFrame.heightPixelCount; row++) {
            for (int col = 0; col < graphicsFrame.widthPixelCount; col++) {
                double sample = 0.0;
                Color color = Color.WHITE;
                if (!waves.isEmpty()) {
                    for (WaveEquation wave : waves)
                        sample += wave.evaluateEquation((double) col / graphicsFrame.widthPixelCount,
                                (double) row / graphicsFrame.widthPixelCount, time);
                    sample = sample / waves.size() / 2.0 + 0.5;
                    if (sample <= 1.0 && sample >= 0.0)
                        color = colorScheme.percentageToColor(sample);
                    else       //DEBUG FOR OUT OF RANGE VALUES
                        throw new IndexOutOfBoundsException("Color sample has value: " + color);
                } else  //Empty, grey out screen
                    color = GREY;
                graphicsFrame.filledSquare(col, graphicsFrame.heightPixelCount - row, PIXEL_SIZE, color);
            }
        }
        graphicsFrame.show(frame);
    }
}