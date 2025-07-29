/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package ui.manager;

import dao.ChiTieuDao;
import entity.ChiTieu;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import dao.impl.ChiTieuDaoImpl;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 *
 * @author ACER
 */
public class QuanLyChiTieu extends javax.swing.JDialog {

    /**
     * Creates new form QuanLyChiTieu
     */
    public QuanLyChiTieu(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        fillTableChiTieu();
        setResizable(false);          // <-- không cho kéo giãn
        setLocationRelativeTo(null);
        setTitle("Quản lý Chi Tiêu");

        
        //============= định dạng bảng hiển thị ==================//
// Header bảng - xám đậm
JTableHeader header = tblChiTieu.getTableHeader();
header.setBackground(new Color(80, 80, 80)); // xám đậm
header.setForeground(Color.WHITE);
header.setFont(new Font("Segoe UI", Font.BOLD, 13));
header.setOpaque(false);

// Cài đặt bảng - màu nền xám nhạt
tblChiTieu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
tblChiTieu.setRowHeight(25);
tblChiTieu.setGridColor(new Color(200, 200, 200)); // đường lưới màu xám nhẹ
tblChiTieu.setSelectionBackground(new Color(220, 220, 220)); // dòng chọn: xám nhạt
tblChiTieu.setSelectionForeground(Color.BLACK); // chữ đen khi chọn dòng

// Bo viền bảng
tblChiTieu.setShowHorizontalLines(true);
tblChiTieu.setShowVerticalLines(true);

// Màu nền bảng
tblChiTieu.setBackground(new Color(235, 235, 235)); // Xám dịu hơn

tblChiTieu.setForeground(Color.DARK_GRAY); // chữ bình thường màu xám đậm

    }

    public void addChiTieu() {
        try {
            String soTienStr = txtSoTien.getText().trim();
            String moTa = txtGhiChu.getText().trim();

            // 🔴 Kiểm tra trống
            if (soTienStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠ Vui lòng nhập số tiền!");
                txtSoTien.requestFocus();
                return;
            }

            float soTien;
            try {
                // Dùng BigDecimal để parse và kiểm tra
                BigDecimal soTienBD = new BigDecimal(soTienStr);
                if (soTienBD.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this, "⚠ Số tiền phải lớn hơn 0!");
                    txtSoTien.requestFocus();
                    return;
                }
                // Gán lại về float
                soTien = soTienBD.floatValue();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "⚠ Số tiền phải là số!");
                txtSoTien.requestFocus();
                return;
            }

            int choice = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn thêm chi tiêu với số tiền: " + soTien + " không?",
                    "Xác nhận thêm", JOptionPane.YES_NO_OPTION);

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }

            ChiTieu ct = new ChiTieu();
            java.util.Date currentDateUtil = new java.util.Date();  // Ngày hiện tại
            java.sql.Date currentDateSql = new java.sql.Date(currentDateUtil.getTime());  // Chuyển thành java.sql.Date

            ct.setNgay(currentDateSql);  // yyyy-MM-dd
            ct.setSoTien(soTien);
            ct.setMoTa(moTa);

            ChiTieuDao dao = new ChiTieuDaoImpl();
            dao.create(ct);

            JOptionPane.showMessageDialog(this, "  Thêm chi tiêu thành công!");
            fillTableChiTieu();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, " Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void fillTableChiTieu() {
        DefaultTableModel model = (DefaultTableModel) tblChiTieu.getModel();
        model.setRowCount(0); // Xóa bảng trước khi đổ mới

        ChiTieuDao dao = new ChiTieuDaoImpl();

        List<ChiTieu> list = dao.findAll(); // Gọi hàm lấy toàn bộ chi tiêu

        // ✅ Định dạng số tiền để tránh E notation
        DecimalFormat df = new DecimalFormat("#,##0.##");

        for (ChiTieu ct : list) {
            Object[] row = {
                ct.getMaChiTieu(),
                ct.getNgay(),
                df.format(ct.getSoTien()), // ✅ Format float để hiển thị đẹp
                ct.getMoTa()
            };
            model.addRow(row);
        }
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Áp dụng renderer cho cột "Mã CT" (cột 0)
        tblChiTieu.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        // Áp dụng renderer cho cột "Ngày" (cột 1)
        tblChiTieu.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        // Tạo renderer căn phải cho cột "Mô Tả" (cột 3)
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        // Áp dụng renderer cho cột "Mô Tả" (cột 3)
        tblChiTieu.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblChiTieu = new javax.swing.JTable();
        txtTongTien = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtFromDate = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtToDate = new javax.swing.JTextField();
        btnLocTheo = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtSoTien = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtGhiChu = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblChiTieu.setBackground(new java.awt.Color(153, 153, 153));
        tblChiTieu.setForeground(new java.awt.Color(0, 0, 0));
        tblChiTieu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã CT", "Ngày", "Số Tiền", "Mô Tả"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Long.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblChiTieu);
        if (tblChiTieu.getColumnModel().getColumnCount() > 0) {
            tblChiTieu.getColumnModel().getColumn(0).setMinWidth(50);
            tblChiTieu.getColumnModel().getColumn(0).setMaxWidth(50);
            tblChiTieu.getColumnModel().getColumn(1).setMinWidth(100);
            tblChiTieu.getColumnModel().getColumn(1).setMaxWidth(100);
            tblChiTieu.getColumnModel().getColumn(2).setMinWidth(150);
            tblChiTieu.getColumnModel().getColumn(2).setMaxWidth(150);
        }

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 750, 280));

        txtTongTien.setBackground(new java.awt.Color(102, 102, 102));
        txtTongTien.setFont(new java.awt.Font("Segoe UI", 1, 19)); // NOI18N
        txtTongTien.setForeground(new java.awt.Color(255, 255, 255));
        txtTongTien.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        getContentPane().add(txtTongTien, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 450, 310, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Tổng Chi Tiêu:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 420, -1, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Từ Năm/Tháng/Ngày: ");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 360, -1, -1));

        txtFromDate.setBackground(new java.awt.Color(102, 102, 102));
        txtFromDate.setForeground(new java.awt.Color(255, 255, 255));
        txtFromDate.setText("yyyy-mm-dd");
        txtFromDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFromDateActionPerformed(evt);
            }
        });
        getContentPane().add(txtFromDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 390, 144, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Đến Năm/Tháng/Ngày: ");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 360, -1, -1));

        txtToDate.setBackground(new java.awt.Color(102, 102, 102));
        txtToDate.setForeground(new java.awt.Color(255, 255, 255));
        txtToDate.setText("yyyy-mm-dd");
        txtToDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtToDateActionPerformed(evt);
            }
        });
        getContentPane().add(txtToDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 390, 150, -1));

        btnLocTheo.setBackground(new java.awt.Color(153, 153, 153));
        btnLocTheo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLocTheo.setForeground(new java.awt.Color(255, 255, 255));
        btnLocTheo.setText("LỌC");
        btnLocTheo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocTheoActionPerformed(evt);
            }
        });
        getContentPane().add(btnLocTheo, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 450, 88, 35));

        jButton1.setBackground(new java.awt.Color(102, 102, 102));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Làm Mới");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 360, 200, -1));

        jButton2.setBackground(new java.awt.Color(102, 102, 102));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Nhập Chi Tiêu");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 400, 200, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Mô Tả");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 110, 70, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Nhập Chi Tiêu Hằng Ngày");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 10, 190, -1));

        txtSoTien.setBackground(new java.awt.Color(153, 153, 153));
        txtSoTien.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtSoTien.setForeground(new java.awt.Color(0, 0, 0));
        txtSoTien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSoTienActionPerformed(evt);
            }
        });
        getContentPane().add(txtSoTien, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 60, 200, 30));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Số Tiền");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 40, 70, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Danh Sách Chi Tiêu");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 190, -1));

        txtGhiChu.setBackground(new java.awt.Color(153, 153, 153));
        txtGhiChu.setColumns(20);
        txtGhiChu.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtGhiChu.setForeground(new java.awt.Color(0, 0, 0));
        txtGhiChu.setRows(5);
        jScrollPane2.setViewportView(txtGhiChu);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 150, 200, 190));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/quanlychitieu.png"))); // NOI18N
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 510));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        addChiTieu();
        txtSoTien.setText("");
        txtGhiChu.setText("");
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        txtSoTien.setText("");
        txtGhiChu.setText("");// TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnLocTheoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocTheoActionPerformed
        // TODO add your handling code here:
        ChiTieuDao dao = new ChiTieuDaoImpl();

        try {
            String tuNgayStr = txtFromDate.getText().trim();
            String denNgayStr = txtToDate.getText().trim();

            if (tuNgayStr.isEmpty() || denNgayStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ ngày bắt đầu và kết thúc!");
                return;
            }

            LocalDate tuNgay = LocalDate.parse(tuNgayStr);
            LocalDate denNgay = LocalDate.parse(denNgayStr);

            float tong = dao.TongChiTrongKhoang(tuNgay, denNgay); // ✅ đúng hàm

            txtTongTien.setText(String.format("%,.0f VNĐ", tong));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Định dạng ngày không hợp lệ (yyyy-MM-dd)!");
        }
    }//GEN-LAST:event_btnLocTheoActionPerformed

    private void txtSoTienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSoTienActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSoTienActionPerformed

    private void txtFromDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFromDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFromDateActionPerformed

    private void txtToDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtToDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtToDateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLocTheo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblChiTieu;
    private javax.swing.JTextField txtFromDate;
    private javax.swing.JTextArea txtGhiChu;
    private javax.swing.JTextField txtSoTien;
    private javax.swing.JTextField txtToDate;
    private javax.swing.JTextField txtTongTien;
    // End of variables declaration//GEN-END:variables
}
