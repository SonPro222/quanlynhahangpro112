/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package ui.manager;

import dao.HoaDonChiTietDAO;
import entity.LichSuGiaoDich;
import entity.HoaDonChiTiet;
import dao.LichSuGiaoDichDAO;
import dao.impl.HoaDonChiTietDAOImpl;
import dao.impl.LichSuGiaoDichDAOImpl; // Thêm import này nếu chưa có
import java.awt.Color;
import java.awt.Font;
import util.XJdbc;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import javax.swing.JOptionPane;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;

/**
 *
 * @author KaiserAri
 */
public class xLichSuGiaoDich extends javax.swing.JDialog {
  private LichSuGiaoDichDAO lichSuGiaoDichDAO = new LichSuGiaoDichDAOImpl();
    public xLichSuGiaoDich(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setTitle("Lịch sử giao dịch");
        setResizable(false);
        setLocationRelativeTo(null);
         maunenBang(tblChiTietHoaDon);
        maunenBang(tblLSHoaDon);
        fillTableHoaDon(); // <-- Gọi hàm fill bảng hóa đơn khi mở form
       
        tblLSHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblLSHoaDon.getSelectedRow();
                if (selectedRow != -1) {
                    int maHD = (int) tblLSHoaDon.getValueAt(selectedRow, 0); // Lấy MaHD từ cột 0
                    fillChiTietHoaDon(maHD); // Gọi hàm để fill bảng chi tiết
                }
            }
        });
        
    }

  

    public void maunenBang(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(80, 80, 80)); // xám đậm
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setOpaque(false);

// Cài đặt bảng - màu nền xám nhạt
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.setGridColor(new Color(200, 200, 200)); // đường lưới màu xám nhẹ
        table.setSelectionBackground(new Color(220, 220, 220)); // dòng chọn: xám nhạt
        table.setSelectionForeground(Color.BLACK); // chữ đen khi chọn dòng

// Bo viền bảng
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);

// Màu nền bảng
        table.setBackground(new Color(235, 235, 235)); // Xám dịu hơn

        table.setForeground(Color.DARK_GRAY); // chữ bình thường màu xám đậm
    }

    public void fillTableHoaDon() {
        DefaultTableModel model = (DefaultTableModel) tblLSHoaDon.getModel();
        model.setRowCount(0); // Clear existing data

        List<LichSuGiaoDich> list = lichSuGiaoDichDAO.findAll();
        for (LichSuGiaoDich ls : list) {
            Object[] row = {
                ls.getMaHD(),
                ls.getNgayLap(),
                ls.getTrangThai(),
                ls.getTenNhanVien(),
                ls.getTenBan(),
                ls.getTongTien()
            };
            model.addRow(row);
        }
    }
    private HoaDonChiTietDAO chiTietDAO = new HoaDonChiTietDAOImpl();

    private void fillChiTietHoaDon(int maHD) {
        DefaultTableModel model = (DefaultTableModel) tblChiTietHoaDon.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        List<HoaDonChiTiet> list = chiTietDAO.findByHoaDonId(maHD);
        for (HoaDonChiTiet ct : list) {
            Object[] row = {
                ct.getMaHD(),
                ct.getMaChiTiet(),
                ct.getMaNuocUong(),
                ct.getTenMon(),
                ct.getSoLuong(),
                ct.getDonGia()
            };
            model.addRow(row);
        }
    }

    public void filterHoaDonByDate(String tuNgay, String denNgay) {
        DefaultTableModel model = (DefaultTableModel) tblLSHoaDon.getModel();
        model.setRowCount(0); // Clear existing data

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fromDate = sdf.parse(tuNgay);
            Date toDate = sdf.parse(denNgay);

            List<LichSuGiaoDich> list = lichSuGiaoDichDAO.findByDateRange(fromDate, toDate);
            for (LichSuGiaoDich ls : list) {
                Object[] row = {
                    ls.getMaHD(),
                    ls.getNgayLap(),
                    ls.getTrangThai(),
                    ls.getTenNhanVien(),
                    ls.getTenBan(),
                    ls.getTongTien()
                };
                model.addRow(row);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi định dạng ngày: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lọc lịch sử giao dịch: " + e.getMessage());
        }
    }

    private Object getTenNhanVien() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTenNhanVien'");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        cbNamThangQuy = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLSHoaDon = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblChiTietHoaDon = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setMinimumSize(new java.awt.Dimension(1000, 555));
        jPanel1.setPreferredSize(new java.awt.Dimension(1080, 550));
        jPanel1.setVerifyInputWhenFocusTarget(false);
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Từ ngày");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 30, 62, -1));

        jTextField1.setBackground(new java.awt.Color(102, 102, 102));
        jTextField1.setForeground(new java.awt.Color(255, 255, 255));
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jPanel1.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 120, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Đến Ngày");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 30, 76, -1));

        jTextField2.setBackground(new java.awt.Color(102, 102, 102));
        jTextField2.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 30, 120, -1));

        jButton1.setBackground(new java.awt.Color(153, 153, 153));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Lọc");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 30, 150, -1));

        cbNamThangQuy.setBackground(new java.awt.Color(153, 153, 153));
        cbNamThangQuy.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cbNamThangQuy.setForeground(new java.awt.Color(0, 0, 0));
        cbNamThangQuy.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Năm nay", "Tuần này", "Tháng này", "Qúy này", "Hôm nay" }));
        cbNamThangQuy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbNamThangQuyActionPerformed(evt);
            }
        });
        jPanel1.add(cbNamThangQuy, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 30, 130, -1));

        tblLSHoaDon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã phiếu", "Ngày tạo", "Trạng thái", "Nhân viên", "Bàn", "Tổng tiền VND"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblLSHoaDon);
        if (tblLSHoaDon.getColumnModel().getColumnCount() > 0) {
            tblLSHoaDon.getColumnModel().getColumn(0).setMinWidth(60);
            tblLSHoaDon.getColumnModel().getColumn(0).setMaxWidth(60);
            tblLSHoaDon.getColumnModel().getColumn(4).setMinWidth(80);
            tblLSHoaDon.getColumnModel().getColumn(4).setMaxWidth(80);
        }

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 840, 200));

        tblChiTietHoaDon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "MaHD", "Mã Món", "Mã Nước", "Tên Món", "Số Lượng", "Đơn Giá VND"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblChiTietHoaDon);
        if (tblChiTietHoaDon.getColumnModel().getColumnCount() > 0) {
            tblChiTietHoaDon.getColumnModel().getColumn(0).setMinWidth(60);
            tblChiTietHoaDon.getColumnModel().getColumn(0).setMaxWidth(60);
            tblChiTietHoaDon.getColumnModel().getColumn(1).setMinWidth(70);
            tblChiTietHoaDon.getColumnModel().getColumn(1).setMaxWidth(70);
            tblChiTietHoaDon.getColumnModel().getColumn(2).setMinWidth(70);
            tblChiTietHoaDon.getColumnModel().getColumn(2).setMaxWidth(70);
            tblChiTietHoaDon.getColumnModel().getColumn(4).setMinWidth(90);
            tblChiTietHoaDon.getColumnModel().getColumn(4).setMaxWidth(90);
        }

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 330, 840, 180));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Lịch Sử Chi Tiết Hóa Đơn");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 300, 200, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Lịch Sử Hóa Đơn");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 60, 139, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lichsugiaodichpro (2).png"))); // NOI18N
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 0, 1020, 560));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 550));
        jPanel1.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
   String tuNgay = jTextField1.getText().trim(); // yyyy-MM-dd
String denNgay = jTextField2.getText().trim(); // yyyy-MM-dd

List<LichSuGiaoDich> danhSachLoc = new ArrayList<>();

try {
    // Kiểm tra nếu cả 2 trường ngày đều không rỗng
    if (!tuNgay.isEmpty() && !denNgay.isEmpty()) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        inputFormat.setLenient(false); // không cho phép sai định dạng

        // Parse thử để chắc chắn chuỗi hợp lệ
        Date fromDate = inputFormat.parse(tuNgay);
        Date toDate = inputFormat.parse(denNgay);

        // Gọi DAO nếu hợp lệ
        danhSachLoc = lichSuGiaoDichDAO.locHoaDonTheoNgay(tuNgay, denNgay);
    } else {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ cả TỪ NGÀY và ĐẾN NGÀY để lọc.");
        return;
    }
} catch (ParseException e) {
    JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ. Vui lòng nhập theo định dạng yyyy-MM-dd.");
    e.printStackTrace();
    return;
}

// Đổ kết quả vào bảng
DefaultTableModel model = (DefaultTableModel) tblLSHoaDon.getModel();
model.setRowCount(0);

SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
for (LichSuGiaoDich hd : danhSachLoc) {
    model.addRow(new Object[]{
        hd.getMaHD(),
        sdf.format(hd.getNgayLap()),
        hd.getTrangThai(),
        hd.getTenNhanVien(),
        hd.getTenBan(),
        hd.getTongTien()
    });
}

    }//GEN-LAST:event_jButton1ActionPerformed

    private void cbNamThangQuyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbNamThangQuyActionPerformed
        String selected = (String) cbNamThangQuy.getSelectedItem();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String tuNgay = today.format(formatter);
        String denNgay = today.format(formatter);
        switch (selected) {
            case "Hôm nay":
                tuNgay = today.format(formatter);
                denNgay = today.plusDays(1).format(formatter);
                break;

            case "Tuần này":
                LocalDate monday = today.with(DayOfWeek.MONDAY);
                LocalDate sunday = today.with(DayOfWeek.SUNDAY);
                tuNgay = monday.format(formatter);
                denNgay = sunday.format(formatter);
                break;
            case "Tháng này":
                LocalDate firstDayOfMonth = today.withDayOfMonth(1);
                LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());
                tuNgay = firstDayOfMonth.format(formatter);
                denNgay = lastDayOfMonth.format(formatter);
                break;
            case "Qúy này":
                int currentQuarter = (today.getMonthValue() - 1) / 3 + 1;
                int startMonth = (currentQuarter - 1) * 3 + 1;
                int endMonth = startMonth + 2;
                LocalDate firstDayOfQuarter = LocalDate.of(today.getYear(), startMonth, 1);
                LocalDate lastDayOfQuarter = LocalDate.of(today.getYear(), endMonth, YearMonth.of(today.getYear(), endMonth).lengthOfMonth());
                tuNgay = firstDayOfQuarter.format(formatter);
                denNgay = lastDayOfQuarter.format(formatter);
                break;
            case "Năm nay":
                tuNgay = LocalDate.of(today.getYear(), 1, 1).format(formatter);
                denNgay = LocalDate.of(today.getYear(), 12, 31).format(formatter);
                break;
        }

        // Gọi hàm lọc
        filterHoaDonByDate(tuNgay, denNgay);

        // Gán lại text cho 2 ô ngày (nếu có)
        jTextField1.setText(tuNgay);
        jTextField2.setText(denNgay);

    }//GEN-LAST:event_cbNamThangQuyActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cbNamThangQuy;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTable tblChiTietHoaDon;
    private javax.swing.JTable tblLSHoaDon;
    // End of variables declaration//GEN-END:variables
}
