package com.xettuyen.ui.dialog;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class CalculationProgressDialog extends JDialog {

    private final JProgressBar progressBar;
    private final JLabel lblStatus;
    private Object result;

    public CalculationProgressDialog(JFrame parent) {
        super(parent, "Đang tính điểm", true);
        setSize(360, 130);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        lblStatus = new JLabel("Đang tính điểm xét tuyển...");
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 13));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(0, 20));

        panel.add(lblStatus, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);

        setContentPane(panel);
    }

    public <T> T startTask(Callable<T> task) {
        progressBar.setValue(0);
        SwingWorker<T, Void> worker = new SwingWorker<>() {
            @Override
            protected T doInBackground() throws Exception {
                return task.call();
            }

            @Override
            protected void done() {
                try {
                    result = get();
                } catch (Exception ex) {
                    result = null;
                }
                dispose();
            }
        };
        worker.execute();
        setVisible(true);
        return (T) result;
    }

    public List<String> startCalculation(Callable<List<String>> task) {
        List<String> data = startTask(task);
        return data == null ? new ArrayList<>() : data;
    }

    public void updateProgress(int percent, String status) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(percent);
            lblStatus.setText(status);
        });
    }
}
