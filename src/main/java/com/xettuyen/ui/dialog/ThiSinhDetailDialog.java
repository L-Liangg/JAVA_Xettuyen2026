package com.xettuyen.ui.dialog;

import com.xettuyen.entity.DiemThi;
import com.xettuyen.entity.DiemThiDgnlVsat;
import com.xettuyen.entity.ThiSinh;
import com.xettuyen.service.impl.DiemThiDgnlVsatService;
import com.xettuyen.service.impl.DiemThiService;
import com.xettuyen.service.impl.ThiSinhService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ThiSinhDetailDialog extends JDialog {

    private final ThiSinh thiSinh;
    private final DiemThiService diemThiService = new DiemThiService();
    private final DiemThiDgnlVsatService diemThiDgnlVsatService = new DiemThiDgnlVsatService();

    public ThiSinhDetailDialog(JFrame parent, ThiSinh thiSinh, ThiSinhService service) {
        super(parent, "Chi tiết thí sinh", true);
        this.thiSinh = thiSinh;
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setResizable(true);
        initUI();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Thông tin thí sinh", createThiSinhTab());
        tabbedPane.addTab("Điểm thi", createDiemThiTab());
        add(tabbedPane, BorderLayout.CENTER);

        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createThiSinhTab() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("CCCD:")); panel.add(new JLabel(str(thiSinh.getCccd())));
        panel.add(new JLabel("Số báo danh:")); panel.add(new JLabel(str(thiSinh.getSobaodanh())));
        panel.add(new JLabel("Họ:")); panel.add(new JLabel(str(thiSinh.getHo())));
        panel.add(new JLabel("Tên:")); panel.add(new JLabel(str(thiSinh.getTen())));
        panel.add(new JLabel("Ngày sinh:")); panel.add(new JLabel(str(thiSinh.getNgay_sinh())));
        panel.add(new JLabel("Giới tính:")); panel.add(new JLabel(str(thiSinh.getGioi_tinh())));
        panel.add(new JLabel("Điện thoại:")); panel.add(new JLabel(str(thiSinh.getDien_thoai())));
        panel.add(new JLabel("Email:")); panel.add(new JLabel(str(thiSinh.getEmail())));
        panel.add(new JLabel("Nơi sinh:")); panel.add(new JLabel(str(thiSinh.getNoi_sinh())));
        panel.add(new JLabel("Đối tượng:")); panel.add(new JLabel(str(thiSinh.getDoi_tuong())));
        panel.add(new JLabel("Khu vực:")); panel.add(new JLabel(str(thiSinh.getKhu_vuc())));
        panel.add(new JLabel("Cập nhật:")); panel.add(new JLabel(str(thiSinh.getUpdated_at())));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.NORTH);
        return wrapper;
    }

    private JPanel createDiemThiTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== ĐIỂM THPT =====
        JLabel lblThpt = new JLabel("Điểm thi THPT:");
        lblThpt.setFont(new Font("Arial", Font.BOLD, 13));
        lblThpt.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblThpt);
        panel.add(Box.createVerticalStrut(5));

        DiemThi diemThi = diemThiService.findByCccd(thiSinh.getCccd());
        if (diemThi != null) {
            String[] thptHeaders = {"Môn", "Điểm"};
            Object[][] thptData = {
                    {"Toán", diemThi.getTO()},
                    {"Văn", diemThi.getVA()},
                    {"Lí", diemThi.getLI()},
                    {"Hóa", diemThi.getHO()},
                    {"Sinh", diemThi.getSI()},
                    {"Sử", diemThi.getSU()},
                    {"Địa", diemThi.getDI()},
                    {"Tin", diemThi.getTI()},
                    {"KTPL", diemThi.getKTPL()},
                    {"N1 thi", diemThi.getN1_THI()},
                    {"N1 CC", diemThi.getN1_CC()},
                    {"CNCN", diemThi.getCNCN()},
                    {"CNNN", diemThi.getCNNN()},
                    {"NL1", diemThi.getNL1()},
                    {"NK1", diemThi.getNK1()},
                    {"NK2", diemThi.getNK2()},
            };
            JTable thptTable = createReadOnlyTable(thptHeaders, thptData);
            JScrollPane thptScroll = new JScrollPane(thptTable);
            thptScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
            thptScroll.setPreferredSize(new Dimension(600, 200));
            thptScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
            panel.add(thptScroll);
        } else {
            JLabel lblNoThpt = new JLabel("Không có dữ liệu điểm thi THPT.");
            lblNoThpt.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(lblNoThpt);
        }

        panel.add(Box.createVerticalStrut(15));

        // ===== ĐIỂM ĐGNL/VSAT =====
        JLabel lblDgnl = new JLabel("Điểm thi ĐGNL/VSAT:");
        lblDgnl.setFont(new Font("Arial", Font.BOLD, 13));
        lblDgnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblDgnl);
        panel.add(Box.createVerticalStrut(5));

        List<DiemThiDgnlVsat> dgnlList = diemThiDgnlVsatService.getAllByCccd(thiSinh.getCccd());
        if (dgnlList != null && !dgnlList.isEmpty()) {
            String[] dgnlHeaders = {"Môn", "Điểm", "Thang điểm", "Đợt thi", "Năm"};
            DefaultTableModel dgnlModel = new DefaultTableModel(dgnlHeaders, 0) {
                @Override
                public boolean isCellEditable(int row, int col) { return false; }
            };
            for (DiemThiDgnlVsat dt : dgnlList) {
                dgnlModel.addRow(new Object[]{
                        dt.getTen_mon(), dt.getDiem(), dt.getThang_diem(),
                        dt.getDot_thi(), dt.getNam()
                });
            }
            JTable dgnlTable = new JTable(dgnlModel);
            dgnlTable.setRowHeight(25);
            dgnlTable.getTableHeader().setReorderingAllowed(false);
            JScrollPane dgnlScroll = new JScrollPane(dgnlTable);
            dgnlScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
            dgnlScroll.setPreferredSize(new Dimension(600, 150));
            dgnlScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
            panel.add(dgnlScroll);
        } else {
            JLabel lblNoDgnl = new JLabel("Không có dữ liệu điểm thi ĐGNL/VSAT.");
            lblNoDgnl.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(lblNoDgnl);
        }

        return panel;
    }

    private JTable createReadOnlyTable(String[] headers, Object[][] data) {
        DefaultTableModel model = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        for (Object[] row : data) {
            if (row[1] != null) model.addRow(row);
        }
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);
        return table;
    }

    private String str(Object val) {
        return val == null ? "" : val.toString();
    }
}