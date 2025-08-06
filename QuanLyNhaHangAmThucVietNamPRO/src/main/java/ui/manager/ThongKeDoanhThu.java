package ui.manager;

import dao.DoanhThuDAO;
import dao.impl.DoanhThuDAOImpl;
import entity.DoanhThu;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import util.ExcelExporter;
import util.XJdbc;
import uui.Auth;

public class ThongKeDoanhThu extends JDialog {

    private JTable tblDoanhThu;
    private JLabel lblTongThu, lblTongChi, lblLoiNhuan;
    private JComboBox<String> cboThang, cboNam, cboNhanVien;
    private JButton btnLoc, btnXuatExcel, btnThemThuCong;
    private JButton btnCapNhat;
    private DoanhThuDAO doanhThuDAO = new DoanhThuDAOImpl();
        private JButton btnTaiDuLieu;


    public ThongKeDoanhThu(Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Thống kê doanh thu");
        setSize(800, 500);
        setLocationRelativeTo(null);

        initComponents();
        fillComboBox();
        loadTableData(null, null, null);
         maunenBang(tblDoanhThu);
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
    private void initComponents() {
        setLayout(new BorderLayout());

        // North
        JPanel pnlTop = new JPanel(new FlowLayout());
        cboThang = new JComboBox<>();
        cboNam = new JComboBox<>();
        cboNhanVien = new JComboBox<>();
        btnLoc = new JButton("Lọc");
        btnXuatExcel = new JButton("Xuất Excel");
        btnThemThuCong = new JButton("Thêm thủ công");

        pnlTop.add(new JLabel("Nhân Viên:"));
        pnlTop.add(cboNhanVien);      
        pnlTop.add(new JLabel("Tháng:"));
        pnlTop.add(cboThang);
        pnlTop.add(new JLabel("Năm:"));
        pnlTop.add(cboNam);
        pnlTop.add(btnLoc);
        pnlTop.add(btnXuatExcel);
        pnlTop.add(btnThemThuCong);
        add(pnlTop, BorderLayout.NORTH);

        // Table
        tblDoanhThu = new JTable();
        tblDoanhThu.setModel(new DefaultTableModel(
                new Object[]{"Ngày","Nhân Viên","Tổng Thu", "Tổng Chi", "Lợi Nhuận"}, 0
        ));
        add(new JScrollPane(tblDoanhThu), BorderLayout.CENTER);

        // South
        JPanel pnlBottom = new JPanel(new GridLayout(1, 3));
        lblTongThu = new JLabel("Tổng thu: 0", SwingConstants.CENTER);
        lblTongChi = new JLabel("Tổng chi: 0", SwingConstants.CENTER);
        lblLoiNhuan = new JLabel("Lợi nhuận: 0", SwingConstants.CENTER);
        pnlBottom.setPreferredSize(new Dimension(0, 60));
        pnlBottom.add(lblTongThu);
        pnlBottom.add(lblTongChi);
        pnlBottom.add(lblLoiNhuan);
        add(pnlBottom, BorderLayout.SOUTH);
        
        Font fontLon = new Font("Arial", Font.BOLD, 14); // Hoặc Font.PLAIN, Font.ITALIC

        lblTongThu.setFont(fontLon);
        lblTongChi.setFont(fontLon);
        lblLoiNhuan.setFont(fontLon);

        // Events
        btnTaiDuLieu = new JButton("Tải dữ liệu");
        btnLoc.addActionListener(e -> locDoanhThu());
        btnXuatExcel.addActionListener(e -> xuatExcel());
        btnThemThuCong.addActionListener(e -> themDoanhThuThuCong());
        btnCapNhat = new JButton("Cập nhật tự động");
        pnlTop.add(btnCapNhat);
        btnCapNhat.addActionListener(e -> {
            doanhThuDAO.capNhatDoanhThuTuHoaDonVaChiTieu();
            JOptionPane.showMessageDialog(this, "Đã cập nhật doanh thu & chi tiêu từ dữ liệu.");
            locDoanhThu();
        });

    }

    private void fillComboBox() {
        cboThang.addItem("Tất cả");
        for (int i = 1; i <= 12; i++) {
            cboThang.addItem("Tháng " + i);
        }

        cboNam.addItem("Tất cả");
        for (int y = 2023; y <= 2030; y++) {
            cboNam.addItem(String.valueOf(y));
        }
        cboNhanVien.removeAllItems();
        cboNhanVien.addItem("Tất cả");
        String sql = "SELECT TENDANGNHAP FROM TaiKhoan";
        try (
            Connection con = XJdbc.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                cboNhanVien.addItem(rs.getString("TENDANGNHAP"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTableData(Integer thang, Integer nam,String TENDANGNHAP) {
        int maNV = Auth.nhanVienDangNhap.getMaNV();

        List<DoanhThu> list = doanhThuDAO.findByMonthYearAndNhanVien(thang, nam, TENDANGNHAP);
        DefaultTableModel model = (DefaultTableModel) tblDoanhThu.getModel();
        model.setRowCount(0);

        double tongThu = 0, tongChi = 0;
        DecimalFormat df = new DecimalFormat("#,###");

        for (DoanhThu dt : list) {
            tongThu += dt.getTongThu();
            tongChi += dt.getTongChi();
            model.addRow(new Object[]{
                dt.getNgay(),
                dt.getTENDANGNHAP(), // Thêm tên nhân viên

                df.format(dt.getTongThu()) + " VND",
                df.format(dt.getTongChi()) + " VND",
                df.format(dt.getTongThu() - dt.getTongChi()) + " VND"
            });
        }

        lblTongThu.setText("Tổng thu: " + df.format(tongThu) + " VND");
        lblTongChi.setText("Tổng chi: " + df.format(tongChi) + " VND");
        lblLoiNhuan.setText("Lợi nhuận: " + df.format(tongThu - tongChi) + " VND");
    }

    private void locDoanhThu() {
        Integer thang = cboThang.getSelectedIndex() == 0 ? null : cboThang.getSelectedIndex();
        Integer nam = cboNam.getSelectedIndex() == 0 ? null : Integer.parseInt((String) cboNam.getSelectedItem());
        String TENDANGNHAP = cboNhanVien.getSelectedIndex() == 0 ? null : (String) cboNhanVien.getSelectedItem();
        loadTableData(thang, nam, TENDANGNHAP);
    }

    private void themDoanhThuThuCong() {
        String ngay = JOptionPane.showInputDialog(this, "Nhập ngày (yyyy-MM-dd):");
        String thu = JOptionPane.showInputDialog(this, "Tổng thu:");
        String chi = JOptionPane.showInputDialog(this, "Tổng chi:");
        String ghiChu = JOptionPane.showInputDialog(this, "Ghi chú:");

        try {
            double tongThu = Double.parseDouble(thu);
            double tongChi = Double.parseDouble(chi);
            doanhThuDAO.insertThuCong(ngay, tongThu, tongChi, ghiChu);
            JOptionPane.showMessageDialog(this, "Thêm doanh thu thành công!");
            locDoanhThu();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm doanh thu: " + e.getMessage());
        }
    }

    private void xuatExcel() {
        try {
            ExcelExporter.exportTable(tblDoanhThu, "DoanhThu.xlsx");
            JOptionPane.showMessageDialog(this, "Xuất Excel thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xuất Excel: " + e.getMessage());
        }
    }
    
    public void loadDoanhThuTheoNhanVien() {
        DefaultTableModel model = (DefaultTableModel) tblDoanhThu.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        String sql = "SELECT hd.MaHD, hd.NgayLap, hd.TongTien, tk.TENDANGNHAP " +
                     "FROM HoaDon hd " +
                     "JOIN TaiKhoan tk ON hd.MaNV= tk.MaNV " +
                     "ORDER BY hd.NgayLap DESC";

        try (
            Connection con = XJdbc.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                int maHD = rs.getInt("MaHD");
                Timestamp ngayLap = rs.getTimestamp("NgayLap");
                double tongTien = rs.getDouble("TongTien");
                String TENDANGNHAP = rs.getString("TENDANGNHAP");

                model.addRow(new Object[]{maHD, ngayLap, tongTien, TENDANGNHAP});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }
//    private void loadNhanVienToComboBox() {
//        cboNhanVien.removeAllItems();
//        cboNhanVien.addItem("Tất cả");
//        String sql = "SELECT TENDANGNHAP FROM NhanVien";
//        try (
//            Connection con = XJdbc.getConnection();
//            PreparedStatement ps = con.prepareStatement(sql);
//            ResultSet rs = ps.executeQuery();
//        ) {
//            while (rs.next()) {
//                cboNhanVien.addItem(rs.getString("TENDANGNHAP"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    

}
