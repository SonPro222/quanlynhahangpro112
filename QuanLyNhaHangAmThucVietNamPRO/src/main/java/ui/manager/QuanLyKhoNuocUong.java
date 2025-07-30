/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package ui.manager;

import dao.impl.KhoNuocUongDAOImpl;
import dao.KhoNuocUongDAO;
import entity.KhoNuocUong;
import dao.ChiTieuDao;
import dao.NuocUongDAO;
import dao.TonKhoDAO;
import dao.XuatKhoNuocUongDAO;
import dao.impl.ChiTieuDaoImpl;
import dao.impl.NuocUongDAOImpl;
import dao.impl.TonKhoDAOImpl;
import dao.impl.XuatKhoNuocUongDAOImpl;
import entity.ChiTieu;
import entity.NuocUong;
import entity.TonKho;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Date;
import java.time.DayOfWeek;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import util.XJdbc;

/**
 *
 * @author ACER
 */
public class QuanLyKhoNuocUong extends javax.swing.JDialog {

    private int selectedMaNuocUong = -1;

    public QuanLyKhoNuocUong(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setResizable(false);
        setLocationRelativeTo(null);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                fillNuocUong();

                return null;
            }
        }.execute();
        initDonViNhapKho();
        fillDanhSachNhapKhoHomNay();
        fillLichSuXuatKho();
        cboLoaiDonVi.addActionListener(e -> {
            String donViChon = cboLoaiDonVi.getSelectedItem().toString();
            int heSo = quyDoiDonVi.getOrDefault(donViChon, 1);

            if (heSo == 1) {
                lblLoaiDonVi.setText("Đang nhập giá cho đơn vị: lon");
            } else {
                lblLoaiDonVi.setText("Đang nhập giá cho đơn vị: thùng = " + heSo + " lon");
            }
        });
        styleTable(tblBangLoaiNuoc);
        styleTable(tblChiTietNhapKho);
        styleTable(tblDanhSachNhapKhoHomNay);
        tatDuongKeBang(tblBangLoaiNuoc);
        tatDuongKeBang(tblChiTietNhapKho);
        tatDuongKeBang(tblDanhSachNhapKhoHomNay);
        tatDuongKeBang(tblXuatKho);
        styleTable(tblXuatKho);
    }

    public void tatDuongKeBang(JTable table) {
        table.setShowHorizontalLines(false); // ẩn đường ngang
        table.setShowVerticalLines(false);   // ẩn đường dọc
    }

    public static void styleTable(JTable table) {
        // Header
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(80, 80, 80)); // xám đậm
        header.setForeground(Color.WHITE);

        // Body màu
        table.setBackground(new Color(200, 200, 200)); // nền bảng xám dịu
        table.setForeground(Color.DARK_GRAY);          // chữ bảng
        table.setSelectionBackground(new Color(220, 220, 220)); // dòng được chọn
        table.setSelectionForeground(Color.BLACK);      // chữ khi được chọn
    }
    ArrayList<Integer> dsMaDoUong = new ArrayList();

    public ImageIcon resizeImageIcon(URL imgURL, int width, int height) {
        try {
            ImageIcon original = new ImageIcon(imgURL);
            Image scaled = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            System.out.println("Lỗi resize ảnh: " + e.getMessage());
            return null;
        }
    }

    public void fillNuocUong() {
        DefaultTableModel model = (DefaultTableModel) tblBangLoaiNuoc.getModel();
        model.setRowCount(0);
        NuocUongDAO nuocDAO = new NuocUongDAOImpl();
        TonKhoDAO tonKhoDAO = new TonKhoDAOImpl();

        List<NuocUong> dsNuoc = nuocDAO.findAll();
        List<TonKho> dsTonKho = tonKhoDAO.findAll();

        Map<Integer, Integer> tonKhoMap = new HashMap<>();
        for (TonKho ton : dsTonKho) {
            tonKhoMap.put(ton.getMaNuocUong(), ton.getSoLuongTong());
        }

        for (NuocUong nuoc : dsNuoc) {
            ImageIcon icon = null;
            try {
                String hinhAnh = nuoc.getHinhAnh();
                if (hinhAnh != null && !hinhAnh.isEmpty()) {
                    URL imgURL = getClass().getResource("/images/" + hinhAnh);
                    if (imgURL != null) {
                        icon = resizeImageIcon(imgURL, 100, 100);
                    }
                }
            } catch (Exception e) {
                System.out.println("Lỗi load ảnh: " + e.getMessage());
            }

            int soLuong = tonKhoMap.getOrDefault(nuoc.getMaNuocUong(), -1);
            String soLuongHienThi;
            if (soLuong <= 0) {
                soLuongHienThi = "Hết Hàng";
            } else {
                String donVi = nuoc.getDonViTinh(); // ví dụ: lon, chai
                int quyCach = getQuyCachSoLuong(donVi); // ví dụ: 24
                soLuongHienThi = convertSoLuongToThungLe(soLuong, donVi, quyCach);
            }

            model.addRow(new Object[]{
                nuoc.getMaNuocUong(),
                icon,
                nuoc.getTenNuocUong(),
                soLuongHienThi
            });
        }

        // Ẩn cột mã nước
        TableColumn hiddenCol = tblBangLoaiNuoc.getColumnModel().getColumn(0);
        hiddenCol.setMinWidth(0);
        hiddenCol.setMaxWidth(0);
        hiddenCol.setPreferredWidth(0);

        // Căn chỉnh hình ảnh
        tblBangLoaiNuoc.getColumnModel().getColumn(1).setPreferredWidth(110);
        tblBangLoaiNuoc.setRowHeight(110);
        tblBangLoaiNuoc.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                setHorizontalAlignment(SwingConstants.CENTER);
                setVerticalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.PLAIN, 20));
                if (value instanceof ImageIcon) {
                    setIcon((ImageIcon) value);
                    setText("");
                } else {
                    setIcon(null);
                    setText("Không có ảnh");
                }
                return this;
            }
        });

        // Căn giữa chữ
        for (int i = 2; i <= 3; i++) {
            DefaultTableCellRenderer centerText = new DefaultTableCellRenderer();
            centerText.setHorizontalAlignment(SwingConstants.CENTER);
            centerText.setVerticalAlignment(SwingConstants.CENTER);
            centerText.setFont(new Font("Arial", Font.PLAIN, 20));
            tblBangLoaiNuoc.getColumnModel().getColumn(i).setCellRenderer(centerText);
        }

        // Sự kiện click hàng
        tblBangLoaiNuoc.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblBangLoaiNuoc.getSelectedRow();
                if (row >= 0) {
                    try {
                        selectedMaNuocUong = Integer.parseInt(tblBangLoaiNuoc.getValueAt(row, 0).toString());
                        Object tenNuocUong = tblBangLoaiNuoc.getValueAt(row, 2);
                        lblLoaiNuocUong.setText("Đang nhập kho cho nước uống: " + tenNuocUong);
                        fillChiTietNhapKho(selectedMaNuocUong);
                    } catch (Exception ex) {
                        selectedMaNuocUong = -1;
                        System.out.println("Lỗi lấy mã nước uống: " + ex.getMessage());
                    }
                }
            }
        });
    }

    private String convertSoLuongToThungLe(int tongSoLuong, String donViTinh, int quyCach) {
        if (quyCach <= 0) {
            return tongSoLuong + " " + donViTinh;
        }

        int soThung = tongSoLuong / quyCach;
        int soLe = tongSoLuong % quyCach;

        StringBuilder result = new StringBuilder();
        if (soThung > 0) {
            result.append(soThung).append(" thùng");
        }
        if (soLe > 0) {
            if (soThung > 0) {
                result.append(" + ");
            }
            result.append(soLe).append(" ").append(donViTinh);
        }
        return result.toString();
    }

    private int getQuyCachSoLuong(String donViTinh) {
        switch (donViTinh.toLowerCase()) {
            case "lon":
                return 24;
            case "chai":
                return 24;
            case "hộp":
                return 12;
            default:
                return 0;
        }
    }

    private String getQuyCachMoTa(String donViTinh) {
        switch (donViTinh.toLowerCase()) {
            case "chai":
                return " 24 chai";
            case "lon":
                return " 24 lon";
            case "hộp":
                return "12 hộp";
            default:
                return "Quy cách chưa xác định";
        }
    }
// Khai báo global
    private Map<String, Integer> quyDoiDonVi;

    public void initDonViNhapKho() {
        quyDoiDonVi = new HashMap<>();
        quyDoiDonVi.put("1 lon/chai", 1);
        quyDoiDonVi.put("1 thùng = 12 lon/chai", 12);
        quyDoiDonVi.put("1 thùng = 24 lon/chai", 24);
        quyDoiDonVi.put("1 thùng = 30 lon/chai", 30);

        cboLoaiDonVi.removeAllItems(); // hoặc giữ nếu anh muốn thêm tùy chọn mới
        for (String key : quyDoiDonVi.keySet()) {
            cboLoaiDonVi.addItem(key);
        }
        cboLoaiDonVi.setSelectedItem("1 lon/chai"); // mặc định chọn
        lblLoaiDonVi.setText("Đang nhập giá cho đơn vị: lon"); // gán mặc định cho label
    }

    public void nhapKhoNuocUong() {
        // Kiểm tra mã nước uống đã chọn
        if (selectedMaNuocUong <= 0) {
            JOptionPane.showMessageDialog(null, "❌ Vui lòng chọn nước uống trước khi nhập kho.");
            return;
        }

        // Lấy đơn vị và hệ số quy đổi
        String donViChon = (String) cboLoaiDonVi.getSelectedItem();
        int heSoQuyDoi = quyDoiDonVi.getOrDefault(donViChon, 1);

        // Kiểm tra số lượng nhập
        int soLuongNhap;
        try {
            soLuongNhap = Integer.parseInt(txtSoLuongNhap.getText().trim());
            if (soLuongNhap <= 0) {
                JOptionPane.showMessageDialog(null, "❌ Số lượng nhập phải lớn hơn 0.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "❌ Số lượng nhập không hợp lệ.");
            return;
        }

        // Kiểm tra giá nhập
        double giaNhap;
        try {
            giaNhap = Double.parseDouble(txtGiaNhapVao.getText().trim());
            if (giaNhap <= 0) {
                JOptionPane.showMessageDialog(null, "❌ Giá nhập phải lớn hơn 0.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "❌ Giá nhập không hợp lệ.");
            return;
        }

        // Tính toán số lon thực tế và đơn giá mỗi lon
        int tongLonNhap = soLuongNhap * heSoQuyDoi;
        double giaMoiLon = giaNhap / tongLonNhap;

        // Hiển thị đơn vị quy đổi
        lblLoaiDonVi.setText(
                heSoQuyDoi == 1
                        ? "Đang nhập giá cho đơn vị lon"
                        : "Đang nhập giá cho đơn vị thùng = " + heSoQuyDoi + " lon"
        );

        // Chuẩn bị DAO
        KhoNuocUongDAO dao = new KhoNuocUongDAOImpl();
        NuocUongDAO nuocUongDAO = new NuocUongDAOImpl();

        // Lấy thông tin nước uống
        NuocUong thongTinNuoc = nuocUongDAO.findById(selectedMaNuocUong);
        if (thongTinNuoc == null) {
            JOptionPane.showMessageDialog(null, "❌ Không tìm thấy thông tin nước uống với mã: " + selectedMaNuocUong);
            return;
        }

        // ➤ ➤ ➤ KHÔNG kiểm tra kho hiện tại. Luôn thêm mới (INSERT)
        KhoNuocUong kho = new KhoNuocUong();
        kho.setMaNuocUong(selectedMaNuocUong);
        kho.setSoLuong(tongLonNhap);
        kho.setGiaNhap(giaMoiLon);
        kho.setNgayNhap(new java.sql.Date(System.currentTimeMillis()));
        kho.setDonViTinh("lon");
        kho.setNuocUong(thongTinNuoc);
        dao.insert(kho);  // ➤➤ INSERT chứ KHÔNG update

        // Ghi chi tiêu
        String ghiChuSoLuong = convertSoLuongToThungLe(
                tongLonNhap,
                kho.getDonViTinh(),
                getQuyCachSoLuong(kho.getDonViTinh())
        );
//    String ghiChuChiTieu = "Nhập đồ uống: " + thongTinNuoc.getTenNuocUong() + " + " + ghiChuSoLuong;
        String ghiChuChiTieu = "Nhập kho đồ uống: "
                + thongTinNuoc.getTenNuocUong()
                + " (" + soLuongNhap + " " + donViChon + " = " + tongLonNhap + " lon), Tổng chi: "
                + String.format("%,.0f", giaNhap) + " VNĐ";

        ChiTieu chitieu = new ChiTieu();
        chitieu.setNgay(new java.sql.Date(System.currentTimeMillis()));
        chitieu.setSoTien(tongLonNhap * giaMoiLon);
        chitieu.setMoTa(ghiChuChiTieu);

        ChiTieuDao chitieuDAO = new ChiTieuDaoImpl();
        chitieuDAO.create(chitieu);

        // Thông báo thành công
        JOptionPane.showMessageDialog(null,
                "✅ Nhập kho thành công:\n"
                + "→ " + soLuongNhap + " × " + heSoQuyDoi + " = " + tongLonNhap + " lon\n"
                + "→ Giá nhập: " + giaNhap + " → đơn giá mỗi lon: " + String.format("%.2f", giaMoiLon)
        );

        // Làm mới giao diện
        fillNuocUong();
        fillDanhSachNhapKhoHomNay();
        txtGiaNhapVao.setText("");
        txtSoLuongNhap.setText("");
        fillChiTietNhapKho(selectedMaNuocUong);
    }

    public void fillChiTietNhapKho(int maNuocUong) {
        DefaultTableModel model = (DefaultTableModel) tblChiTietNhapKho.getModel();
        model.setRowCount(0); // Xoá hết dữ liệu cũ

        KhoNuocUongDAO dao = new KhoNuocUongDAOImpl();
        List<KhoNuocUong> danhSachNhap = dao.findAllByMaNuocUong(maNuocUong); // Lấy toàn bộ lịch sử nhập

        for (KhoNuocUong nuoc : danhSachNhap) {
            if (nuoc != null && nuoc.getNuocUong() != null) {
                String soLuongHienThi = convertSoLuongToThungLe(
                        nuoc.getSoLuong(),
                        nuoc.getDonViTinh(),
                        getQuyCachSoLuong(nuoc.getDonViTinh())
                );

                model.addRow(new Object[]{
                    nuoc.getMaNuocUong(), // mã nước (ẩn)
                    nuoc.getNuocUong().getTenNuocUong(), // tên nước
                    nuoc.getDonViTinh(), // đơn vị
                    soLuongHienThi, // số lượng quy đổi
                    String.format("%.0f", nuoc.getGiaNhap()), // giá nhập
                    nuoc.getNgayNhap() // ngày nhập
                });
            }
        }

        // Ẩn cột mã nước
        TableColumn hiddenCol = tblChiTietNhapKho.getColumnModel().getColumn(0);
        hiddenCol.setMinWidth(0);
        hiddenCol.setMaxWidth(0);
        hiddenCol.setPreferredWidth(0);

        // Căn giữa + set font cho các cột còn lại
        for (int i = 1; i < tblChiTietNhapKho.getColumnCount(); i++) {
            DefaultTableCellRenderer center = new DefaultTableCellRenderer();
            center.setHorizontalAlignment(SwingConstants.CENTER);
            center.setVerticalAlignment(SwingConstants.CENTER);
            center.setFont(new Font("Arial", Font.PLAIN, 16));
            tblChiTietNhapKho.getColumnModel().getColumn(i).setCellRenderer(center);
        }
    }

    private void fillLichSuXuatKho() {
        DefaultTableModel model = (DefaultTableModel) tblXuatKho.getModel();
        model.setRowCount(0); // Xóa bảng

        List<Object[]> list = new XuatKhoNuocUongDAOImpl().getLichSuXuatKho();
        for (Object[] row : list) {
            model.addRow(row);
        }
    }

    public void fillDanhSachNhapKhoHomNay() {
        DefaultTableModel model = (DefaultTableModel) tblDanhSachNhapKhoHomNay.getModel();
        model.setRowCount(0);

        KhoNuocUongDAO dao = new KhoNuocUongDAOImpl();
        LocalDate homNay = LocalDate.now();
        List<KhoNuocUong> danhSach = dao.findByNgayNhap(homNay); // <-- Lấy chính xác hôm nay

        for (KhoNuocUong nuoc : danhSach) {
            String soLuongHienThi = convertSoLuongToThungLe(
                    nuoc.getSoLuong(),
                    nuoc.getDonViTinh(),
                    getQuyCachSoLuong(nuoc.getDonViTinh())
            );

            model.addRow(new Object[]{
                nuoc.getMaNuocUong(),
                nuoc.getNuocUong().getTenNuocUong(),
                soLuongHienThi,
                String.format("%.0f", nuoc.getGiaNhap()),
                nuoc.getNgayNhap()
            });
        }

        // Ẩn mã nước
        TableColumn hiddenCol = tblDanhSachNhapKhoHomNay.getColumnModel().getColumn(0);
        hiddenCol.setMinWidth(0);
        hiddenCol.setMaxWidth(0);
        hiddenCol.setPreferredWidth(0);

        // Căn giữa cột
        for (int i = 1; i < tblDanhSachNhapKhoHomNay.getColumnCount(); i++) {
            DefaultTableCellRenderer centerText = new DefaultTableCellRenderer();
            centerText.setHorizontalAlignment(SwingConstants.CENTER);
            centerText.setVerticalAlignment(SwingConstants.CENTER);
            centerText.setFont(new Font("Arial", Font.PLAIN, 16));
            tblDanhSachNhapKhoHomNay.getColumnModel().getColumn(i).setCellRenderer(centerText);
        }
    }

//    public void fillDanhSachNhapKhoHomNay() {
//        DefaultTableModel model = (DefaultTableModel) tblDanhSachNhapKhoHomNay.getModel();
//        model.setRowCount(0);
//        KhoNuocUongDAO dao = new KhoNuocUongDAOImpl();
//        List<KhoNuocUong> danhSach = dao.findAll();
//
//        LocalDate homNay = LocalDate.now();
//
//        for (KhoNuocUong nuoc : danhSach) {
//            if (nuoc.getNgayNhap() != null) {
//                LocalDate ngayNhap = nuoc.getNgayNhap().toLocalDate();
//
//                if (ngayNhap.isEqual(homNay)) {
//                    String soLuongHienThi = convertSoLuongToThungLe(
//                            nuoc.getSoLuong(),
//                            nuoc.getDonViTinh(),
//                            getQuyCachSoLuong(nuoc.getDonViTinh())
//                    );
//
//                    model.addRow(new Object[]{
//                        nuoc.getMaNuocUong(), // index 0 - ẩn
//                        nuoc.getNuocUong().getTenNuocUong(), // index 1 - tên nước
//                        soLuongHienThi, // index 3 - số lượng mô tả
//                        String.format("%.0f", nuoc.getGiaNhap()), // index 4 - giá nhập
//                        nuoc.getNgayNhap() // index 5 - ngày nhập
//                    });
//                }
//            }
//        }
//
//        // Ẩn cột mã nước uống
//        TableColumn hiddenCol = tblDanhSachNhapKhoHomNay.getColumnModel().getColumn(0);
//        hiddenCol.setMinWidth(0);
//        hiddenCol.setMaxWidth(0);
//        hiddenCol.setPreferredWidth(0);
//
//        // Căn giữa các cột hiển thị
//        for (int i = 1; i < tblDanhSachNhapKhoHomNay.getColumnCount(); i++) {
//            DefaultTableCellRenderer centerText = new DefaultTableCellRenderer();
//            centerText.setHorizontalAlignment(SwingConstants.CENTER);
//            centerText.setVerticalAlignment(SwingConstants.CENTER);
//            centerText.setFont(new Font("Arial", Font.PLAIN, 16));
//            tblDanhSachNhapKhoHomNay.getColumnModel().getColumn(i).setCellRenderer(centerText);
//        }
//    }
    //code em An
    private void fillXuatKhoTheoLoc() {
        DefaultTableModel model = (DefaultTableModel) tblXuatKho.getModel();
        model.setRowCount(0);

        String selected = cboLoc.getSelectedItem() != null ? cboLoc.getSelectedItem().toString() : "";
        String locStr = txtLocNhap.getText().trim();

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String tuNgay = today.format(formatter);
        String denNgay = today.format(formatter);

        switch (selected) {
            case "Hôm nay":
                tuNgay = today.format(formatter);
                denNgay = today.plusDays(1).format(formatter); // end = ngày mai
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
            case "Quý này":
                int currentQuarter = (today.getMonthValue() - 1) / 3 + 1;
                int startMonth = (currentQuarter - 1) * 3 + 1;
                int endMonth = startMonth + 2;
                LocalDate firstDayOfQuarter = LocalDate.of(today.getYear(), startMonth, 1);
                LocalDate lastDayOfQuarter = LocalDate.of(today.getYear(), endMonth,
                        YearMonth.of(today.getYear(), endMonth).lengthOfMonth());
                tuNgay = firstDayOfQuarter.format(formatter);
                denNgay = lastDayOfQuarter.format(formatter);
                break;
            case "Năm nay":
                tuNgay = LocalDate.of(today.getYear(), 1, 1).format(formatter);
                denNgay = LocalDate.of(today.getYear(), 12, 31).format(formatter);
                break;
            default:
                if (!locStr.isEmpty()) {
                    try {
                        if (locStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                            tuNgay = locStr;
                            denNgay = locStr;
                        } else if (locStr.matches("\\d{4}-\\d{2}")) {
                            String[] parts = locStr.split("-");
                            int year = Integer.parseInt(parts[0]);
                            int month = Integer.parseInt(parts[1]);
                            LocalDate firstDay = LocalDate.of(year, month, 1);
                            LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
                            tuNgay = firstDay.format(formatter);
                            denNgay = lastDay.format(formatter);
                        } else if (locStr.matches("\\d{4}")) {
                            int year = Integer.parseInt(locStr);
                            LocalDate firstDay = LocalDate.of(year, 1, 1);
                            LocalDate lastDay = LocalDate.of(year, 12, 31);
                            tuNgay = firstDay.format(formatter);
                            denNgay = lastDay.format(formatter);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Định dạng phải là yyyy hoặc yyyy-MM hoặc yyyy-MM-dd");
                            return;
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Ngày nhập không hợp lệ.");
                        return;
                    }
                }
                break;
        }

        if (tuNgay == null && denNgay == null) {
            tuNgay = today.format(formatter);
            denNgay = today.format(formatter);
        }

        LocalDate start = LocalDate.parse(tuNgay, formatter);
        LocalDate end = LocalDate.parse(denNgay, formatter);

        System.out.println("Lọc từ ngày: " + start + " đến ngày: " + end);

        XuatKhoNuocUongDAOImpl dao = new XuatKhoNuocUongDAOImpl();
        List<Object[]> allData = dao.getLichSuXuatKho();
        List<Object[]> filtered = new ArrayList<>();

        for (Object[] row : allData) {
            LocalDate ngayXuatData = null;
            if (row[3] != null) {
                try {
                    ngayXuatData = ((Date) row[3]).toLocalDate();
                } catch (Exception e) {
                    continue;
                }
            }

            boolean matchNgay = ngayXuatData != null
                    && (!ngayXuatData.isBefore(start))
                    && (ngayXuatData.isBefore(end.plusDays(1))); // FIX

            if (matchNgay) {
                filtered.add(row);
            }
        }

        for (Object[] row : filtered) {
            model.addRow(new Object[]{
                row[0], row[1], row[2], row[3], row[4], row[5]
            });
        }

        if (filtered.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu phù hợp.");
        }
    }

    private void fillNhapKhoTheoLoc() {
        DefaultTableModel model = (DefaultTableModel) tblChiTietNhapKho.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        String selected = cboLocNhap.getSelectedItem() != null ? cboLocNhap.getSelectedItem().toString() : "";
        String locStr = txtLocNhap.getText().trim();

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String tuNgay = today.format(formatter);
        String denNgay = today.format(formatter);

        try {
            switch (selected) {
                case "Hôm nay":
                    tuNgay = today.format(formatter);
                    denNgay = today.plusDays(1).format(formatter); // end = ngày mai
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
                case "Quý này":
                    int currentQuarter = (today.getMonthValue() - 1) / 3 + 1;
                    int startMonth = (currentQuarter - 1) * 3 + 1;
                    int endMonth = startMonth + 2;
                    LocalDate firstDayOfQuarter = LocalDate.of(today.getYear(), startMonth, 1);
                    LocalDate lastDayOfQuarter = LocalDate.of(today.getYear(), endMonth,
                            YearMonth.of(today.getYear(), endMonth).lengthOfMonth());
                    tuNgay = firstDayOfQuarter.format(formatter);
                    denNgay = lastDayOfQuarter.format(formatter);
                    break;
                case "Năm nay":
                    tuNgay = LocalDate.of(today.getYear(), 1, 1).format(formatter);
                    denNgay = LocalDate.of(today.getYear(), 12, 31).format(formatter);
                    break;
                default:
                    // Trường hợp nhập thủ công
                    if (!locStr.isEmpty()) {
                        if (locStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                            tuNgay = locStr;
                            denNgay = locStr;
                        } else if (locStr.matches("\\d{4}-\\d{2}")) {
                            String[] parts = locStr.split("-");
                            int year = Integer.parseInt(parts[0]);
                            int month = Integer.parseInt(parts[1]);
                            LocalDate firstDay = LocalDate.of(year, month, 1);
                            LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
                            tuNgay = firstDay.format(formatter);
                            denNgay = lastDay.format(formatter);
                        } else if (locStr.matches("\\d{4}")) {
                            int year = Integer.parseInt(locStr);
                            LocalDate firstDay = LocalDate.of(year, 1, 1);
                            LocalDate lastDay = LocalDate.of(year, 12, 31);
                            tuNgay = firstDay.format(formatter);
                            denNgay = lastDay.format(formatter);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Định dạng phải là yyyy hoặc yyyy-MM hoặc yyyy-MM-dd");
                            return;
                        }
                    }
                    break;
            }

            if (tuNgay == null || denNgay == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thời gian lọc.");
                return;
            }

            LocalDate start = LocalDate.parse(tuNgay, formatter);
            LocalDate end = LocalDate.parse(denNgay, formatter);

            KhoNuocUongDAOImpl dao = new KhoNuocUongDAOImpl();
            List<KhoNuocUong> allData = dao.findAll();
            List<KhoNuocUong> filtered = new ArrayList<>();

            for (KhoNuocUong kho : allData) {
                LocalDate ngayNhapData = kho.getNgayNhap().toLocalDate();

                boolean matchNgay = (!ngayNhapData.isBefore(start))
                        && (ngayNhapData.isBefore(end.plusDays(1)));

                if (matchNgay) {
                    filtered.add(kho);
                }
            }

            for (KhoNuocUong kho : filtered) {
                model.addRow(new Object[]{
                    kho.getMaKhoNuoc(),
                    kho.getNuocUong() != null ? kho.getNuocUong().getTenNuocUong() : "",
                    kho.getSoLuong(),
                    kho.getDonViTinh(),
                    kho.getGiaNhap(),
                    kho.getNgayNhap()
                });
            }

            if (filtered.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu phù hợp.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lọc dữ liệu: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblBangLoaiNuoc = new javax.swing.JTable();
        lblLoaiNuocUong = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtSoLuongNhap = new javax.swing.JTextField();
        cboLoaiDonVi = new javax.swing.JComboBox<>();
        btnNhapDoUong = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtGiaNhapVao = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblChiTietNhapKho = new javax.swing.JTable();
        lblLoaiDonVi = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblDanhSachNhapKhoHomNay = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblXuatKho = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        btnLoc = new javax.swing.JButton();
        txtLocNhap = new javax.swing.JTextField();
        cboLoc = new javax.swing.JComboBox<>();
        btnLocNhap = new javax.swing.JButton();
        cboLocNhap = new javax.swing.JComboBox<>();
        txtLocXuat = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblBangLoaiNuoc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã Nước Uống", "Hình ảnh", "Tên Nước Uống", "Số Lượng Tồn Kho"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblBangLoaiNuoc);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 570, 330));

        lblLoaiNuocUong.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblLoaiNuocUong.setForeground(new java.awt.Color(0, 0, 0));
        getContentPane().add(lblLoaiNuocUong, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 0, 400, 40));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Danh Sách Nước Uống");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 190, 20));
        getContentPane().add(txtSoLuongNhap, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 40, 90, -1));

        getContentPane().add(cboLoaiDonVi, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 40, 180, -1));

        btnNhapDoUong.setText("Nhập Đồ Uống");
        btnNhapDoUong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNhapDoUongActionPerformed(evt);
            }
        });
        getContentPane().add(btnNhapDoUong, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 140, 190, 30));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Lịch Sử Xuất Kho Đồ Uống");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 380, 190, -1));
        getContentPane().add(txtGiaNhapVao, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 140, 190, -1));

        tblChiTietNhapKho.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã Nước Uống", "Tên Nước Uóng", "Đơn Vị Tính", "Số Lượng", "Gía Nhập 1 lon/chai", "Ngày Nhập"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblChiTietNhapKho);
        if (tblChiTietNhapKho.getColumnModel().getColumnCount() > 0) {
            tblChiTietNhapKho.getColumnModel().getColumn(4).setMinWidth(120);
            tblChiTietNhapKho.getColumnModel().getColumn(4).setMaxWidth(120);
        }

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 410, 570, 200));

        lblLoaiDonVi.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblLoaiDonVi.setForeground(new java.awt.Color(0, 0, 0));
        getContentPane().add(lblLoaiDonVi, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 70, 410, 40));

        tblDanhSachNhapKhoHomNay.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Mã Đồ Uống", "Tên Đồ Uống", "Só Lượng Nhập", "Gía Nhập", "Ngày Nhập"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblDanhSachNhapKhoHomNay);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 210, 530, 160));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Nhập Số Lượng");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 10, 130, 20));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Gía Nhập Vào");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 110, -1, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("VND");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 140, 50, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("lịch Sử Nhập Đồ Uống Trong Ngày");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 180, 330, -1));

        tblXuatKho.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã", "Tên Nước", "Số Lượng", "Ngày Xuất Kho", "Mã HD", "Đơn Vị Tính"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tblXuatKho);

        getContentPane().add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 410, 530, 200));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Lịch Sử Nhập Kho Đồ Uống");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 380, 330, -1));

        btnLoc.setText("Lọc");
        btnLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocActionPerformed(evt);
            }
        });
        getContentPane().add(btnLoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 380, -1, -1));
        getContentPane().add(txtLocNhap, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 380, 110, -1));

        cboLoc.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hôm nay ", "Tuần này", "Tháng này", "Quý này", "Năm nay" }));
        getContentPane().add(cboLoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 380, -1, -1));

        btnLocNhap.setText("Lọc");
        btnLocNhap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocNhapActionPerformed(evt);
            }
        });
        getContentPane().add(btnLocNhap, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 380, -1, -1));

        cboLocNhap.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hôm nay ", "Tuần này", "Tháng này", "Quý này", "Năm nay" }));
        getContentPane().add(cboLocNhap, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 380, -1, -1));
        getContentPane().add(txtLocXuat, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 380, 110, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNhapDoUongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNhapDoUongActionPerformed
        nhapKhoNuocUong();
    }//GEN-LAST:event_btnNhapDoUongActionPerformed

    private void btnLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocActionPerformed
        // TODO add your handling code here:
        fillXuatKhoTheoLoc();
    }//GEN-LAST:event_btnLocActionPerformed

    private void btnLocNhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocNhapActionPerformed
        // TODO add your handling code here:
        fillNhapKhoTheoLoc();
    }//GEN-LAST:event_btnLocNhapActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnLocNhap;
    private javax.swing.JButton btnNhapDoUong;
    private javax.swing.JComboBox<String> cboLoaiDonVi;
    private javax.swing.JComboBox<String> cboLoc;
    private javax.swing.JComboBox<String> cboLocNhap;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblLoaiDonVi;
    private javax.swing.JLabel lblLoaiNuocUong;
    private javax.swing.JTable tblBangLoaiNuoc;
    private javax.swing.JTable tblChiTietNhapKho;
    private javax.swing.JTable tblDanhSachNhapKhoHomNay;
    private javax.swing.JTable tblXuatKho;
    private javax.swing.JTextField txtGiaNhapVao;
    private javax.swing.JTextField txtLocNhap;
    private javax.swing.JTextField txtLocXuat;
    private javax.swing.JTextField txtSoLuongNhap;
    // End of variables declaration//GEN-END:variables

}
