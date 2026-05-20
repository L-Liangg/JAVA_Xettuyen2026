package com.xettuyen.ui.dialog;

import com.xettuyen.service.imports.ImportResult;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;

public class ImportProgressDialog extends JDialog {

    private final JProgressBar progressBar;
    private final JLabel lblStatus;
    private final JButton btnClose;
    private final JButton btnCancel;
    private volatile boolean cancelled = false;
    private ImportResult result;

    public ImportProgressDialog(JFrame parent) {
        super(parent, "Import Excel", true);
        setSize(420, 160);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblStatus = new JLabel("Đang chuẩn bị...");
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 13));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(0, 22));

        btnClose = new JButton("Đóng");
        btnClose.setEnabled(false);
        btnClose.addActionListener(e -> dispose());

        btnCancel = new JButton("Hủy");
        btnCancel.addActionListener(e -> {
            cancelled = true;
            btnCancel.setEnabled(false);
            lblStatus.setText("Đang hủy...");
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnCancel);
        bottomPanel.add(btnClose);

        panel.add(lblStatus, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(panel);
    }

    public ImportResult startImport(Callable<ImportResult> importTask) {
        SwingWorker<ImportResult, Void> worker = new SwingWorker<>() {
            @Override
            protected ImportResult doInBackground() throws Exception {
                return importTask.call();
            }

            @Override
            protected void done() {
                try {
                    result = get();
                } catch (Exception e) {
                    result = new ImportResult();
                    result.addError(0, e.getMessage());
                }
                progressBar.setValue(100);
                if (result.hasErrors()) {
                    lblStatus.setText("<html>Lỗi: <b>" + result.getErrors().size() + "</b></html>");
                } else {
                    lblStatus.setText("Import thành công!");
                }
                btnCancel.setEnabled(false);
                btnClose.setEnabled(true);
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                setTitle("Import hoàn thành");
            }
        };
        worker.execute();
        setVisible(true);
        return result;
    }

    public void updateProgress(int percent, String status) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(percent);
            lblStatus.setText(status);
        });
    }

    public boolean isCancelled() { return cancelled; }
}