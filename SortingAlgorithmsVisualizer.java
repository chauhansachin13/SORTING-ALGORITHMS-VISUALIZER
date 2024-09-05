import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SortingAlgorithmsVisualizer extends JFrame {
    private int[] array;
    private JPanel drawPanel;
    private JSlider speedSlider;
    private JTextField sizeField;
    private JComboBox<String> algorithmComboBox;
    private JButton startButton, pauseButton, stopButton;
    private AtomicBoolean isPaused, isStopped;
    private Thread sortingThread;

    private void generateArray() {
        int size = Integer.parseInt(sizeField.getText());
        array = new int[size];
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            array[i] = rand.nextInt(500) + 1;
        }
        drawPanel.repaint();
    }

    public SortingAlgorithmsVisualizer() {
        setTitle("Sorting Algorithms Visualizer");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        drawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawArray(g);
            }
        };

        drawPanel.setPreferredSize(new Dimension(1000, 500));
        add(drawPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        add(controlPanel, BorderLayout.SOUTH);

        JLabel sizeLabel = new JLabel("Array Size:");
        controlPanel.add(sizeLabel);

        sizeField = new JTextField("50", 5);
        controlPanel.add(sizeField);

        JButton generateButton = new JButton("Generate Array");
        controlPanel.add(generateButton);

        JLabel speedLabel = new JLabel("Speed:");
        controlPanel.add(speedLabel);

        speedSlider = new JSlider(1, 100, 50);
        speedSlider.setMajorTickSpacing(25);
        speedSlider.setMinorTickSpacing(5);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);

        Dictionary<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(1, new JLabel("Very Slow"));
        labelTable.put(25, new JLabel("Slow"));
        labelTable.put(50, new JLabel("Medium"));
        labelTable.put(75, new JLabel("Fast"));
        labelTable.put(100, new JLabel("Very Fast"));
        speedSlider.setLabelTable(labelTable);

        Dimension sliderSize = new Dimension(450, 50);
        speedSlider.setPreferredSize(sliderSize);
        controlPanel.add(speedSlider);

        JLabel algorithmLabel = new JLabel("Algorithm:");
        controlPanel.add(algorithmLabel);

        String[] algorithms = { "Bubble Sort", "Selection Sort", "Insertion Sort", "Merge Sort", "Quick Sort",
                "Heap Sort" };
        algorithmComboBox = new JComboBox<>(algorithms);
        controlPanel.add(algorithmComboBox);

        startButton = new JButton("Start Sorting");
        controlPanel.add(startButton);

        pauseButton = new JButton("Pause");
        pauseButton.setEnabled(false);
        controlPanel.add(pauseButton);

        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        controlPanel.add(stopButton);

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateArray();
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSorting();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseSorting();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopSorting();
            }
        });

        isPaused = new AtomicBoolean(false);
        isStopped = new AtomicBoolean(false);

        // Generate array of default size (50) when the application starts
        generateArray();
    }

    private void startSorting() {
        if (sortingThread != null && sortingThread.isAlive()) {
            return;
        }

        isPaused.set(false);
        isStopped.set(false);
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);

        sortingThread = new Thread(() -> {
            try {
                String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
                switch (selectedAlgorithm) {
                    case "Bubble Sort":
                        bubbleSort();
                        break;
                    case "Selection Sort":
                        selectionSort();
                        break;
                    case "Insertion Sort":
                        insertionSort();
                        break;
                    case "Merge Sort":
                        mergeSort();
                        break;
                    case "Quick Sort":
                        quickSort(0, array.length - 1);
                        break;
                    case "Heap Sort":
                        heapSort();
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    startButton.setEnabled(true);
                    pauseButton.setEnabled(false);
                    stopButton.setEnabled(false);
                    // Ensure the complexity dialog appears after sorting is complete
                    showComplexityDialog();
                });
            }
        });

        sortingThread.start();
    }

    private void pauseSorting() {
        isPaused.set(!isPaused.get());
        pauseButton.setText(isPaused.get() ? "Resume" : "Pause");
    }

    private void stopSorting() {
        isStopped.set(true);
        if (sortingThread != null) {
            sortingThread.interrupt();
        }
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    private void bubbleSort() throws InterruptedException {
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (isStopped.get())
                    return;
                while (isPaused.get())
                    Thread.sleep(10);
                if (array[j] > array[j + 1]) {
                    // Swap
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    drawPanel.repaint();
                    Thread.sleep(calculateSleepTime());
                }
            }
        }
    }

    private void selectionSort() throws InterruptedException {
        for (int i = 0; i < array.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < array.length; j++) {
                if (isStopped.get())
                    return;
                while (isPaused.get())
                    Thread.sleep(10);
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
            }
            // Swap
            int temp = array[minIndex];
            array[minIndex] = array[i];
            array[i] = temp;
            drawPanel.repaint();
            Thread.sleep(calculateSleepTime());
        }
    }

    private void insertionSort() throws InterruptedException {
        for (int i = 1; i < array.length; i++) {
            int key = array[i];
            int j = i - 1;
            while (j >= 0 && array[j] > key) {
                if (isStopped.get())
                    return;
                while (isPaused.get())
                    Thread.sleep(10);
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
            drawPanel.repaint();
            Thread.sleep(calculateSleepTime());
        }
    }

    private void mergeSort() throws InterruptedException {
        mergeSort(0, array.length - 1);
    }

    private void mergeSort(int left, int right) throws InterruptedException {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(left, mid);
            mergeSort(mid + 1, right);
            merge(left, mid, right);
            drawPanel.repaint();
            Thread.sleep(calculateSleepTime());
        }
    }

    private void merge(int left, int mid, int right) throws InterruptedException {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        int[] L = new int[n1];
        int[] R = new int[n2];
        System.arraycopy(array, left, L, 0, n1);
        System.arraycopy(array, mid + 1, R, 0, n2);

        int i = 0, j = 0;
        int k = left;
        while (i < n1 && j < n2) {
            if (isStopped.get())
                return;
            while (isPaused.get())
                Thread.sleep(10);
            if (L[i] <= R[j]) {
                array[k++] = L[i++];
            } else {
                array[k++] = R[j++];
            }
        }
        while (i < n1) {
            array[k++] = L[i++];
        }
        while (j < n2) {
            array[k++] = R[j++];
        }
    }

    private void quickSort(int low, int high) throws InterruptedException {
        if (low < high) {
            int pi = partition(low, high);
            quickSort(low, pi - 1);
            quickSort(pi + 1, high);
            drawPanel.repaint();
            Thread.sleep(100 - speedSlider.getValue());
        }
        // Check if sorting is complete and the sorting thread is not interrupted
        if (low == 0 && high == array.length - 1 && !isStopped.get()) {
            SwingUtilities.invokeLater(() -> showComplexityDialog());
        }
    }

    private int partition(int low, int high) throws InterruptedException {
        int pivot = array[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (isStopped.get())
                return high;
            while (isPaused.get())
                Thread.sleep(10);
            if (array[j] <= pivot) {
                i++;
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        return i + 1;
    }

    private void heapSort() throws InterruptedException {
        int n = array.length;
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(n, i);
        }
        for (int i = n - 1; i >= 0; i--) {
            if (isStopped.get())
                return;
            while (isPaused.get())
                Thread.sleep(10);
            int temp = array[0];
            array[0] = array[i];
            array[i] = temp;
            heapify(i, 0);
            drawPanel.repaint();
            Thread.sleep(calculateSleepTime());
        }
    }

    private void heapify(int n, int i) throws InterruptedException {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && array[left] > array[largest]) {
            largest = left;
        }
        if (right < n && array[right] > array[largest]) {
            largest = right;
        }
        if (largest != i) {
            if (isStopped.get())
                return;
            while (isPaused.get())
                Thread.sleep(10);
            int swap = array[i];
            array[i] = array[largest];
            array[largest] = swap;
            heapify(n, largest);
        }
    }

    private void drawArray(Graphics g) {
        if (array == null)
            return;

        double width = drawPanel.getWidth();
        int height = drawPanel.getHeight();
        double barWidth = (double) (width / array.length);

        for (int i = 0; i < array.length; i++) {
            int barHeight = (int) (((double) array[i] / 500) * height);

            // Generate a unique color for each bar
            Color barColor = generateUniqueColor(i);

            g.setColor(barColor);
            g.fillRect((int) (i * barWidth), height - barHeight, (int) barWidth, barHeight);

            // Draw the integer value above the bar
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(array[i]), (int) (i * barWidth + barWidth / 4), height - barHeight - 5);
        }
    }

    private Color generateUniqueColor(int index) {
        int red = (index * 3) % 256;
        int green = (index * 53) % 256;
        int blue = (index * 211) % 256;
        return new Color(red, green, blue);
    }

    // private Color generateUniqueColor(int index) {
    // float hue = (index * 0.618033988749895f) % 1.0f; // Golden ratio conjugate to
    // avoid repetition
    // float saturation = 0.5f + ((index * 0.1f) % 0.5f); // Varying saturation
    // slightly
    // float brightness = 0.7f + ((index * 0.1f) % 0.3f); // Varying brightness
    // slightly

    // return Color.getHSBColor(hue, saturation, brightness);
    // }

    private int calculateSleepTime() {
        // Use the slider value to determine sleep time
        return 100 - speedSlider.getValue();
    }

    private void showComplexityDialog() {
        String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
        String title = "Time Complexity of " + selectedAlgorithm;
        String complexityDetails = "";

        switch (selectedAlgorithm) {
            case "Bubble Sort":
                complexityDetails = "<b>Average Case:</b> O(n^2)<br><b>Best Case:</b> O(n)<br><b>Worst Case:</b> O(n^2)";
                break;
            case "Selection Sort":
                complexityDetails = "<b>Average Case:</b> O(n^2)<br><b>Best Case:</b> O(n^2)<br><b>Worst Case:</b> O(n^2)";
                break;
            case "Insertion Sort":
                complexityDetails = "<b>Average Case:</b> O(n^2)<br><b>Best Case:</b> O(n)<br><b>Worst Case:</b> O(n^2)";
                break;
            case "Merge Sort":
                complexityDetails = "<b>Average Case:</b> O(n log n)<br><b>Best Case:</b> O(n log n)<br><b>Worst Case:</b> O(n log n)";
                break;
            case "Quick Sort":
                complexityDetails = "<b>Average Case:</b> O(n log n)<br><b>Best Case:</b> O(n log n)<br><b>Worst Case:</b> O(n^2)";
                break;
            case "Heap Sort":
                complexityDetails = "<b>Average Case:</b> O(n log n)<br><b>Best Case:</b> O(n log n)<br><b>Worst Case:</b> O(n log n)";
                break;
        }

        JOptionPane.showMessageDialog(this,
                "<html><b><font color='blue'>" + title + "</font></b><br><br>" + complexityDetails + "</html>",
                "Algorithm Complexity",
                JOptionPane.INFORMATION_MESSAGE);

        // Restore the fullscreen mode after the dialog box is closed
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        // gd.setFullScreenWindow(this);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SortingAlgorithmsVisualizer visualizer = new SortingAlgorithmsVisualizer();
            visualizer.setVisible(true);

            // Set the window to maximized (instead of fullscreen)
            visualizer.setExtendedState(JFrame.MAXIMIZED_BOTH);
        });
    }

}
