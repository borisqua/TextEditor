package com.example.flipper_swing_concurrency_demo_app;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;

//This is an equivalent of flipping a coin
public class Flipper extends JFrame implements ActionListener {

    private final GridBagConstraints constraints;
    private final JTextField headsText, totalText, devText;
    private final Border border = BorderFactory.createLoweredBevelBorder();
    private final JButton startButton, stopButton;
    private FlipTask flipTask;

    private JTextField makeText() {
        JTextField t = new JTextField(14);
        t.setEditable(false);
        t.setHorizontalAlignment(JTextField.RIGHT);
        t.setBorder(border);
        return t;
    }

    private void addLabeledField(String label, JTextField field, int row) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.LINE_END;
        getContentPane().add(new JLabel(label), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.LINE_START;
        getContentPane().add(field, constraints);
    }

    private void addInfoBox() {
        JTextArea info = new JTextArea("This demo flips a virtual coin on a background thread and updates the UI with the number of heads, total flips, and deviation from 50%.");
        info.setColumns(28);
        info.setRows(3);
        info.setEditable(false);
        info.setFocusable(false);
        info.setOpaque(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.LINE_START;
        getContentPane().add(info, constraints);
        constraints.gridwidth = 1;
    }

    private JButton makeButton(String caption) {
        JButton b = new JButton(caption);
        b.setActionCommand(caption);
        b.addActionListener(this);
        getContentPane().add(b, constraints);
        return b;
    }

    public Flipper() {
        super("Flipper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Make text boxes
        getContentPane().setLayout(new GridBagLayout());
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 10, 4, 10);
        addInfoBox();
        headsText = makeText();
        totalText = makeText();
        devText = makeText();
        addLabeledField("Heads:", headsText, 1);
        addLabeledField("Total:", totalText, 2);
        addLabeledField("Deviation:", devText, 3);

        //Make buttons
        constraints.gridy = 4;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        startButton = makeButton("Start");
        stopButton = makeButton("Stop");
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        getContentPane().add(buttonPanel, constraints);
        constraints.gridwidth = 1;
        stopButton.setEnabled(false);

        //Display the window.
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
        setVisible(true);
    }

    //This represents the result (number of heads on total tries)
    private record FlipPair(long heads, long total) {
    }

    private class FlipTask extends SwingWorker<Void, FlipPair> {

        //constantly tries random.nextBoolean() - it is throwing the coin;
        @Override
        protected Void doInBackground() {
            long heads = 0;
            long total = 0;
            Random random = new Random();
            while (!isCancelled()) {
                total++;
                if (random.nextBoolean()) {
                    heads++;
                }
                publish(new FlipPair(heads, total));
            }
            return null;
        }

        @Override
        protected void process(List<FlipPair> pairs) {
            FlipPair pair = pairs.getLast();
            headsText.setText(String.format("%d", pair.heads));
            totalText.setText(String.format("%d", pair.total));
            devText.setText(String.format("%.10g",
                    ((double) pair.heads) / ((double) pair.total) - 0.5));
        }
    }

    public void actionPerformed(ActionEvent e) {
        if ("Start".equals(e.getActionCommand())) {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            (flipTask = new FlipTask()).execute();
        } else if ("Stop".equals(e.getActionCommand())) {
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            flipTask.cancel(true);
            flipTask = null;
        }

    }

    static void main() {
        SwingUtilities.invokeLater(Flipper::new);
    }
}
