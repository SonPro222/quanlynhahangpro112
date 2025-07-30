/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package ui.manager;

import dao.ChamCongDAO;
import dao.ChiTieuDao;
import dao.NhanVienDAO;
import dao.PhieuTraLuongDAO;
import dao.impl.ChamCongDAOImpl;
import dao.impl.ChiTieuDaoImpl;
import dao.impl.NhanVienDAOImpl;
import dao.impl.PhieuTraLuongDAOImpl;
import entity.ChamCong;
import entity.ChiTieu;
import entity.NhanVien;
import entity.PhieuTraLuong;
import java.awt.Color;
import java.awt.Font;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.TimeRange;
import util.XDate;

public class QuanLyChamCongNhanVien extends javax.swing.JDialog {

    ChiTieuDao chiTieuDao = new ChiTieuDaoImpl();
    NhanVienDAO nhanVienDAO = new NhanVienDAOImpl(); // khai báo ngoài hàm nếu cần
    ChamCongDAO chamCongDAO = new ChamCongDAOImpl(); // DAO chấm công
    PhieuTraLuongDAO phieuTraLuongDAO = new PhieuTraLuongDAOImpl();
    private String ngayChamCongCu = null;
    boolean daChamCongHomNay = false;
    LocalDate now = LocalDate.now();
    int thang = now.getMonthValue();
    int nam = now.getYear();

    public QuanLyChamCongNhanVien(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        fillTableChamCongTheoNhanVien();
        fillNgayHienTai();
        fillBangLuongTheoThang(thang, nam);
        setResizable(false);          // <-- không cho kéo giãn
        setLocationRelativeTo(null);
        setTitle("Quản Lý Chấm Công");
        fillPhieuTraLuongToTable();
        maunenBang(tblBangLuong);
        maunenBang(tblChamCong);
        maunenBang(tblBangLuongChiTiet1);
        kiemTraTrangThaiChamCong();

    }

    public void maunenBang(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(80, 80, 80)); // xám đậm
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setOpaque(false);

        // Center header text
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

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

        // Center cell content for non-boolean columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
        table.setDefaultRenderer(String.class, centerRenderer);
    }
    //========== fill dữ liệu nhân viên =============/

    public void fillTableChamCongTheoNhanVien() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
        String ngayHienTaiStr = sdf.format(XDate.now());
        Date ngayHienTai = XDate.parse(ngayHienTaiStr, "dd/MM/yyyy");
        if (ngayChamCongCu == null || !ngayChamCongCu.equals(ngayHienTaiStr)) {
            DefaultTableModel model = (DefaultTableModel) tblChamCong.getModel();
            model.setRowCount(0);
            List<NhanVien> listNV = nhanVienDAO.findAll();
            boolean tatCaDaCham = true;
            for (NhanVien nv : listNV) {
                boolean daCham = chamCongDAO.exists(nv.getMaNV(), ngayHienTai);
                if (!daCham) {
                    model.addRow(new Object[]{
                        nv.getMaNV(),
                        nv.getTenNV(),
                        ngayHienTaiStr,
                        null,
                        null,
                        ""
                    });
                    tatCaDaCham = false;
                }
            }

            btnChamCong.setEnabled(!tatCaDaCham);
            tblChamCong.setEnabled(!tatCaDaCham);
            ngayChamCongCu = ngayHienTaiStr;
        }
    }

    public void fillNgayHienTai() {
        Date ngayHienTai = TimeRange.today().getBegin(); // Lấy ngày bắt đầu hôm nay
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN")); // format theo tiếng Việt
        txtNgayHienTai.setText(sdf.format(ngayHienTai));
        txtNgayHienTai.setEnabled(false);
    }

    private void chamCongNhanVien() {
        DefaultTableModel model = (DefaultTableModel) tblChamCong.getModel();
        int rowCount = model.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            int maNV = (Integer) model.getValueAt(i, 0);
            String ngayStr = (String) model.getValueAt(i, 2);
            Date ngay = XDate.parse(ngayStr, "dd/MM/yyyy");
            Boolean coMat = (Boolean) model.getValueAt(i, 3);
            Boolean vangMat = (Boolean) model.getValueAt(i, 4);
            String ghiChu = (String) model.getValueAt(i, 5);

            // Kiểm tra: chưa chọn trạng thái
            if (coMat == null && vangMat == null) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn 'Có mặt' hoặc 'Vắng mặt' cho nhân viên mã " + maNV);
                return;
            }

            if (chamCongDAO.exists(maNV, ngay)) {
                continue;
            }

            ChamCong cc = new ChamCong();
            cc.setMaNV(maNV);
            cc.setNgayCham(ngay);
            cc.setCoMat(Boolean.TRUE.equals(coMat)); // đúng logic đã được đảm bảo bởi TableModelListener
            cc.setGhiChu(ghiChu);

            chamCongDAO.insert(cc);
        }

        JOptionPane.showMessageDialog(this, "Đã chấm công thành công!");
        btnChamCong.setVisible(false);
        tblChamCong.setEnabled(false);
        fillBangLuongTheoThang(thang, nam);
        kiemTraTrangThaiChamCong();
    }

    public class ChamCongService {

        private ChamCongDAO chamCongDAO = new ChamCongDAOImpl();
        private NhanVienDAO nhanVienDAO = new NhanVienDAOImpl();

        public List<Object[]> thongKeCongVaLuongTheoThang(int thang, int nam) {
            List<Object[]> ketQua = new ArrayList<>();
            List<NhanVien> listNV = nhanVienDAO.findAll();

            for (NhanVien nv : listNV) {
                List<ChamCong> listCC = chamCongDAO.findByNhanVienAndMonth(nv.getMaNV(), thang, nam);

                int soNgayLam = 0;
                int soNgayNghi = 0;
                for (ChamCong cc : listCC) {
                    if (cc.isCoMat()) {
                        soNgayLam++;
                    } else {
                        soNgayNghi++;
                    }
                }

                int soNghiBiTru = Math.max(0, soNgayNghi - 2); // Trừ từ ngày thứ 3
                double luongCoDinh = nv.getLuong();
                double luong1Ngay = luongCoDinh / 28.0; // CHIA 28
                double truLuong = soNghiBiTru * luong1Ngay;
                double tongLuong = soNgayLam * luong1Ngay;

                DecimalFormat df = new DecimalFormat("#,###.##");
                ketQua.add(new Object[]{
                    nv.getMaNV(),
                    nv.getTenNV(),
                    findNgayBatDau(nv.getMaNV()),
                    soNgayLam,
                    soNgayNghi,
                    df.format(truLuong),
                    df.format(tongLuong)
                });
            }

            return ketQua;
        }

        private Date findNgayBatDau(int maNV) {
            return chamCongDAO.findNgayBatDau(maNV); // cần viết trong DAO
        }
    }

    public void fillBangLuongTheoThang(int thang, int nam) {
        ChamCongService chamCongService = new ChamCongService();
        List<Object[]> data = chamCongService.thongKeCongVaLuongTheoThang(thang, nam);

        DefaultTableModel model = (DefaultTableModel) tblBangLuong.getModel();
        model.setRowCount(0);

        for (Object[] row : data) {
            model.addRow(row);
        }
    }

    //==================KIỂM TRA TRẠNG THÁI CHẤM CÔNG =======================//
    private void kiemTraTrangThaiChamCong() {
        DefaultTableModel model = (DefaultTableModel) tblChamCong.getModel();
        int rowCount = model.getRowCount();
        int chuaCham = 0;

        for (int i = 0; i < rowCount; i++) {
            Boolean coMat = (Boolean) model.getValueAt(i, 3);
            Boolean vangMat = (Boolean) model.getValueAt(i, 4);

            if (coMat == null && vangMat == null) {
                chuaCham++;
            }
        }

        if (chuaCham > 0) {
            jblChamCong.setText("HÔM NAY CHƯA CHẤM CÔNG XONG (" + chuaCham + " nhân viên)");
        } else {
            jblChamCong.setText("HÔM NAY ĐÃ CHẤM CÔNG XONG");
        }
    }

    //====================TRẢ LƯƠNG NHÂN VIÊN =====================//
    public void paySalary() {
        // Lấy ngày tháng hiện tại
        Date currentDate = new Date();  // Ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        int month = calendar.get(Calendar.MONTH) + 1;  // Tháng
        int year = calendar.get(Calendar.YEAR);

        if (calendar.get(Calendar.DAY_OF_MONTH) != 1) {
            JOptionPane.showMessageDialog(null, "Lương chỉ được trả vào ngày 1 của mỗi tháng.");
            return;
        }

        List<NhanVien> listNV = nhanVienDAO.findAll();

        // Khởi tạo Workbook cho Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Lương tháng " + month);

        // Tiêu đề cho bảng Excel
        Row headerRow = sheet.createRow(0);
        String[] columns = {"Mã NV", "Tên NV", "Ngày Thanh Toán", "Tổng Lương", "Trừ Lương"};
        for (int i = 0; i < columns.length; i++) {
            headerRow.createCell(i).setCellValue(columns[i]);
        }

        double totalSalaryPaid = 0;

        int rowNum = 1;
        for (NhanVien nv : listNV) {
            List<ChamCong> listCC = chamCongDAO.findByNhanVienAndMonth(nv.getMaNV(), month, year);

            int soNgayLam = 0;
            int soNgayNghi = 0;
            for (ChamCong cc : listCC) {
                if (cc.isCoMat()) {
                    soNgayLam++;
                } else {
                    soNgayNghi++;
                }
            }

            int soNghiBiTru = Math.max(0, soNgayNghi - 2); // Trừ từ ngày nghỉ thứ 3
            double luongCoDinh = nv.getLuong();
            double luong1Ngay = luongCoDinh / 28.0;  // Chia cho 28 để tính lương ngày
            double truLuong = soNghiBiTru * luong1Ngay;
            double tongLuong = soNgayLam * luong1Ngay;

            // Thêm dòng vào bảng Excel
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(nv.getMaNV());
            row.createCell(1).setCellValue(nv.getTenNV());
            row.createCell(2).setCellValue(currentDate.toString()); // Chuyển ngày thành chuỗi
            row.createCell(3).setCellValue(tongLuong);
            row.createCell(4).setCellValue(truLuong);

            totalSalaryPaid += tongLuong;

            // Lưu thông tin trả lương vào bảng PHIEUTRALUONG
            PhieuTraLuong phieu = new PhieuTraLuong();
            phieu.setMaNV(nv.getMaNV());
            phieu.setNgayThanhToan(currentDate);
            phieu.setTongLuong(tongLuong);
            phieu.setLuongTru(truLuong);
            phieu.setGhiChu("Trả lương tháng " + month);

            // Gọi phương thức insert để lưu vào cơ sở dữ liệu
            phieuTraLuongDAO.insert(phieu);
        }

        // Ghi tổng lương vào bảng ThuChi
        ChiTieu thuChi = new ChiTieu();  // Tạo đối tượng ThuChi đúng
        thuChi.setSoTien(totalSalaryPaid);  // Gán tổng số tiền trả lương vào chi tiêu
        thuChi.setNgay(currentDate);  // Sử dụng ngày hiện tại cho ThuChi
        thuChi.setMoTa("Trả lương nhân viên tháng " + month);

        // Gọi phương thức lưu vào bảng ThuChi
        ChiTieuDao thuChiDao = new ChiTieuDaoImpl();
        thuChiDao.create(thuChi);  // Lưu thông tin vào bảng ThuChi

        // Xóa bảng chấm công (tblBangLuong) sau khi trả lương
        tblBangLuong.clearSelection();  // Nếu bạn sử dụng JTable, làm mới bảng
        // Gọi phương thức xóa hoặc làm trống bảng chấm công (tùy thuộc vào yêu cầu)

        // Xuất dữ liệu ra file Excel
        try (FileOutputStream fileOut = new FileOutputStream("LuongThang" + month + ".xlsx")) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fillPhieuTraLuongToTable();
        JOptionPane.showMessageDialog(null, "Lương đã được trả cho tất cả nhân viên và xuất Excel thành công.");
    }

    public void fillPhieuTraLuongToTable() {
        // Lấy model của bảng
        DefaultTableModel model = (DefaultTableModel) tblBangLuongChiTiet1.getModel();

        // Xóa dữ liệu cũ trong bảng
        model.setRowCount(0);

        // Truy vấn dữ liệu từ DAO
        PhieuTraLuongDAO phieuTraLuongDAO = new PhieuTraLuongDAOImpl();
        List<PhieuTraLuong> listPhieuTraLuong = phieuTraLuongDAO.findAll();

        // Duyệt qua danh sách và điền dữ liệu vào bảng
        for (PhieuTraLuong phieu : listPhieuTraLuong) {
            // Thêm một dòng mới vào bảng
            model.addRow(new Object[]{
                phieu.getMaPhieuLuong(),
                phieu.getMaNV(),
                phieu.getTenNV(),
                phieu.getNgayThanhToan(), // Ngày trả lương
                phieu.getLuongTru(),
                phieu.getTongLuong(),
                phieu.getGhiChu() // Ghi chú
            });
        }
    }

    //=====bangluong=============//
//    private void fillBangLuong1() {
//        DefaultTableModel model = (DefaultTableModel) tblBangLuong.getModel();
//        model.setRowCount(0); // Xóa dữ liệu cũ
//        String maNVStr = txtMaNhanVien.getText().trim();
//        String tenNVKeyword = txtNgayBatDau.getText().trim().toLowerCase();
//        Integer maNVFilter = null;
//        if (!maNVStr.isEmpty()) {
//            try {
//                maNVFilter = Integer.parseInt(maNVStr);
//            } catch (NumberFormatException e) {
//                JOptionPane.showMessageDialog(this, "Mã nhân viên phải là số nguyên.");
//                return;
//            }
//        }
//
//        ChamCongService chamCongService = new ChamCongService();
//        LocalDate now = LocalDate.now();
//        int thang = now.getMonthValue();
//        int nam = now.getYear();
//
//        List<Object[]> allData = chamCongService.thongKeCongVaLuongTheoThang(thang, nam);
//        List<Object[]> filtered = new ArrayList<>();
//
//        for (Object[] row : allData) {
//            Integer maNVData;
//            String tenNVData;
//
//            try {
//                maNVData = Integer.valueOf(row[0].toString().trim());
//            } catch (Exception e) {
//                continue;
//            }
//
//            tenNVData = (row[1] != null) ? row[1].toString().trim().toLowerCase() : "";
//
//            boolean matchMa = (maNVFilter == null) || maNVData.equals(maNVFilter);
//            boolean matchTen = false;
//
//            if (tenNVKeyword.isEmpty()) {
//                matchTen = true; // không nhập tên thì bỏ qua
//            } else {
//                if (tenNVData.equals(tenNVKeyword)) {
//                    matchTen = true; // khớp toàn bộ
//                } else {
//                    String[] tenParts = tenNVData.split("\\s+");
//                    String ho = tenParts.length > 0 ? tenParts[0] : "";
//                    String ten = tenParts.length > 0 ? tenParts[tenParts.length - 1] : "";
//
//                    // khớp từ đầu hoặc tên cuối
//                    if (tenNVKeyword.equals(ho) || tenNVKeyword.equals(ten)) {
//                        matchTen = true;
//                    }
//                }
//            }
//
//            if (matchMa && matchTen) {
//                filtered.add(row);
//            }
//        }
//
//        for (Object[] row : filtered) {
//            model.addRow(new Object[]{
//                row[0], // Mã NV
//                row[1], // Tên NV
//                row[2], // Ngày bắt đầu
//                row[3], // Số ngày làm
//                row[4], // Số ngày nghỉ
//                row[5], // Trừ lương
//                row[6] // Tổng lương
//            });
//        }
//
//        if (filtered.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên phù hợp.");
//        }
//    }
    private void fillBangLuong1() {
        DefaultTableModel model = (DefaultTableModel) tblBangLuong.getModel();
        model.setRowCount(0);
        String maNVStr = txtMaNhanVien.getText().trim();
        String ngayBatDauStr = txtNgayBatDau.getText().trim();

        Integer maNVFilter = null;
        Integer yearFilter = null;
        Integer monthFilter = null;
        LocalDate dateFilter = null;

        // Lọc theo mã nhân viên
        if (!maNVStr.isEmpty()) {
            try {
                maNVFilter = Integer.parseInt(maNVStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Mã nhân viên phải là số nguyên.");
                return;
            }
        }

        // Lọc theo ngày/tháng/năm
        if (!ngayBatDauStr.isEmpty()) {
            try {
                if (ngayBatDauStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    // yyyy-MM-dd
                    dateFilter = LocalDate.parse(ngayBatDauStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } else if (ngayBatDauStr.matches("\\d{4}-\\d{2}")) {
                    // yyyy-MM
                    String[] parts = ngayBatDauStr.split("-");
                    yearFilter = Integer.parseInt(parts[0]);
                    monthFilter = Integer.parseInt(parts[1]);
                } else if (ngayBatDauStr.matches("\\d{4}")) {
                    // yyyy
                    yearFilter = Integer.parseInt(ngayBatDauStr);
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

        ChamCongService chamCongService = new ChamCongService();
        LocalDate now = LocalDate.now();
        int thang = now.getMonthValue();
        int nam = now.getYear();

        List<Object[]> allData = chamCongService.thongKeCongVaLuongTheoThang(thang, nam);
        List<Object[]> filtered = new ArrayList<>();

        for (Object[] row : allData) {
            Integer maNVData;
            LocalDate ngayBatDauData = null;

            try {
                maNVData = Integer.valueOf(row[0].toString().trim());
            } catch (Exception e) {
                continue;
            }

            if (row[2] != null) {
                try {
                    ngayBatDauData = LocalDate.parse(row[2].toString().trim());
                } catch (Exception e) {
                }
            }

            boolean matchMa = (maNVFilter == null) || maNVData.equals(maNVFilter);
            boolean matchNgay = true;

            if (dateFilter != null) {
                matchNgay = ngayBatDauData != null && ngayBatDauData.equals(dateFilter);
            } else if (yearFilter != null && monthFilter != null) {
                matchNgay = ngayBatDauData != null
                        && ngayBatDauData.getYear() == yearFilter
                        && ngayBatDauData.getMonthValue() == monthFilter;
            } else if (yearFilter != null) {
                matchNgay = ngayBatDauData != null
                        && ngayBatDauData.getYear() == yearFilter;
            }

            if (matchMa && matchNgay) {
                filtered.add(row);
            }
        }

        for (Object[] row : filtered) {
            model.addRow(new Object[]{
                row[0], // Mã NV
                row[1], // Tên NV
                row[2], // Ngày bắt đầu
                row[3], // Số ngày làm
                row[4], // Số ngày nghỉ
                row[5], // Trừ lương
                row[6] // Tổng lương
            });
        }

        if (filtered.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên phù hợp.");
        }
    }

//================BangLuongChiTiet=======//
    private void fillToTableBangLuongChiTiet1() {
        DefaultTableModel model = (DefaultTableModel) tblBangLuongChiTiet1.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        String maNVStr = txtMaNV.getText().trim();
        String tenNVKeyword = txtTennhanvien.getText().trim().toLowerCase();
        String ngayThanhToanStr = txtNgaythanhtoan.getText().trim();

        PhieuTraLuongDAO dao = new PhieuTraLuongDAOImpl();
        List<PhieuTraLuong> danhSach = dao.findAll();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (PhieuTraLuong ptl : danhSach) {
            boolean matchMaNV = true;
            boolean matchTenNV = true;
            boolean matchNgay = true;

            // So sánh mã nhân viên nếu có nhập
            if (!maNVStr.isEmpty()) {
                try {
                    int maNV = Integer.parseInt(maNVStr);
                    matchMaNV = ptl.getMaNV() == maNV;
                } catch (NumberFormatException e) {
                    matchMaNV = false;
                }
            }

            // So sánh tên nhân viên tuyệt đối
            if (!tenNVKeyword.isEmpty()) {
                String tenNVData = ptl.getTenNV().trim().toLowerCase();

                if (tenNVData.equals(tenNVKeyword)) {
                    matchTenNV = true;
                } else {
                    String[] parts = tenNVData.split("\\s+");
                    String ho = parts.length > 0 ? parts[0] : "";
                    String ten = parts.length > 0 ? parts[parts.length - 1] : "";

                    if (tenNVKeyword.equals(ho) || tenNVKeyword.equals(ten)) {
                        matchTenNV = true;
                    } else {
                        matchTenNV = false;
                    }
                }
            }

            // So sánh ngày thanh toán nếu có nhập
            if (!ngayThanhToanStr.isEmpty()) {
                try {
                    String ngayPTL = sdf.format(ptl.getNgayThanhToan());
                    matchNgay = ngayPTL.contains(ngayThanhToanStr);
                } catch (Exception e) {
                    matchNgay = false;
                }
            }

            // Nếu thỏa tất cả điều kiện thì add vào bảng
            if (matchMaNV && matchTenNV && matchNgay) {
                model.addRow(new Object[]{
                    ptl.getMaPhieuLuong(),
                    ptl.getMaNV(),
                    ptl.getTenNV(),
                    sdf.format(ptl.getNgayThanhToan()),
                    ptl.getLuongTru(),
                    ptl.getTongLuong(),
                    ptl.getGhiChu()
                });
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton2 = new javax.swing.JButton();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNgayHienTai = new javax.swing.JTextField();
        btnChamCong = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblChamCong = new javax.swing.JTable();
        jblChamCong = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBangLuong = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtNgayBatDau = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtMaNhanVien = new javax.swing.JTextField();
        btnBangLuong = new javax.swing.JButton();
        txtHienThi = new javax.swing.JButton();
        txtTenNhanVien1 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblBangLuongChiTiet1 = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        txtNgaythanhtoan = new javax.swing.JTextField();
        txtMaNV = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtTennhanvien = new javax.swing.JTextField();
        btnHienthi = new javax.swing.JButton();
        btnLocbangluong = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        jButton2.setText("jButton2");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 204, 204));
        jLabel1.setText("Chấm Công Ngày :");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, 130, -1));

        txtNgayHienTai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtNgayHienTai.setForeground(new java.awt.Color(0, 0, 0));
        jPanel2.add(txtNgayHienTai, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 20, 170, -1));

        btnChamCong.setBackground(new java.awt.Color(153, 153, 153));
        btnChamCong.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnChamCong.setForeground(new java.awt.Color(0, 0, 0));
        btnChamCong.setText("Chấm Công");
        btnChamCong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChamCongActionPerformed(evt);
            }
        });
        jPanel2.add(btnChamCong, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 467, 300, 30));

        tblChamCong.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã NV", "Tên NV", "Ngày ", " Có Mặt", "Vắng Mặt", "Ghi chú"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblChamCong);
        if (tblChamCong.getColumnModel().getColumnCount() > 0) {
            tblChamCong.getColumnModel().getColumn(0).setMinWidth(50);
            tblChamCong.getColumnModel().getColumn(0).setMaxWidth(50);
            tblChamCong.getColumnModel().getColumn(4).setMinWidth(70);
            tblChamCong.getColumnModel().getColumn(4).setMaxWidth(70);
        }

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 60, 880, 380));

        jblChamCong.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jblChamCong.setForeground(new java.awt.Color(204, 204, 204));
        jPanel2.add(jblChamCong, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 20, 360, 30));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/quanlychamcongpro (2).png"))); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, -40, 1000, 560));

        jTabbedPane2.addTab("Chấm Công", jPanel2);

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblBangLuong.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã NV", "Tên NV", " Ngày Bắt Đầu Làm", "Số Ngày Làm", "Só Ngày Nghỉ", "Trừ Lương", "Tổng Lương"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblBangLuong);
        if (tblBangLuong.getColumnModel().getColumnCount() > 0) {
            tblBangLuong.getColumnModel().getColumn(0).setMinWidth(60);
            tblBangLuong.getColumnModel().getColumn(0).setMaxWidth(60);
            tblBangLuong.getColumnModel().getColumn(3).setMinWidth(100);
            tblBangLuong.getColumnModel().getColumn(3).setMaxWidth(100);
            tblBangLuong.getColumnModel().getColumn(4).setMinWidth(100);
            tblBangLuong.getColumnModel().getColumn(4).setMaxWidth(100);
        }

        jPanel4.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 900, 400));

        jButton1.setBackground(new java.awt.Color(153, 153, 153));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Trả Lương Nhân Viên");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 470, -1, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Ngày Bắt Đầu Làm :");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 440, -1, -1));

        txtNgayBatDau.setBackground(new java.awt.Color(153, 153, 153));
        txtNgayBatDau.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtNgayBatDau.setForeground(new java.awt.Color(255, 255, 255));
        txtNgayBatDau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNgayBatDauActionPerformed(evt);
            }
        });
        jPanel4.add(txtNgayBatDau, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 470, 130, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Mã Nhân Viên :");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 440, -1, -1));

        txtMaNhanVien.setBackground(new java.awt.Color(153, 153, 153));
        txtMaNhanVien.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtMaNhanVien.setForeground(new java.awt.Color(0, 0, 0));
        txtMaNhanVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaNhanVienActionPerformed(evt);
            }
        });
        jPanel4.add(txtMaNhanVien, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 470, 110, -1));

        btnBangLuong.setBackground(new java.awt.Color(153, 153, 153));
        btnBangLuong.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBangLuong.setForeground(new java.awt.Color(255, 255, 255));
        btnBangLuong.setText("Lọc");
        btnBangLuong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBangLuongActionPerformed(evt);
            }
        });
        jPanel4.add(btnBangLuong, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 470, -1, -1));

        txtHienThi.setBackground(new java.awt.Color(153, 153, 153));
        txtHienThi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtHienThi.setForeground(new java.awt.Color(255, 255, 255));
        txtHienThi.setText("Tất cả");
        txtHienThi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHienThiActionPerformed(evt);
            }
        });
        jPanel4.add(txtHienThi, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 470, -1, -1));

        txtTenNhanVien1.setBackground(new java.awt.Color(153, 153, 153));
        txtTenNhanVien1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtTenNhanVien1.setForeground(new java.awt.Color(255, 255, 255));
        jPanel4.add(txtTenNhanVien1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 470, 130, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Tên Nhân Viên :");
        jPanel4.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 440, -1, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/quanlymonanpro (2).png"))); // NOI18N
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 520));

        jTabbedPane2.addTab("Bảng Lương", jPanel4);

        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblBangLuongChiTiet1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã ", "MaNV", "Tên Nhân Viên", "Ngày Thanh Toán", "Trù Lương", "Tổng Lương", "Ghi Chú"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tblBangLuongChiTiet1);
        if (tblBangLuongChiTiet1.getColumnModel().getColumnCount() > 0) {
            tblBangLuongChiTiet1.getColumnModel().getColumn(0).setMinWidth(50);
            tblBangLuongChiTiet1.getColumnModel().getColumn(0).setMaxWidth(50);
            tblBangLuongChiTiet1.getColumnModel().getColumn(1).setMinWidth(60);
            tblBangLuongChiTiet1.getColumnModel().getColumn(1).setMaxWidth(60);
        }

        jPanel5.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, 880, 390));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Mã Nhân Viên :");
        jPanel5.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 430, -1, -1));

        txtNgaythanhtoan.setBackground(new java.awt.Color(204, 204, 204));
        txtNgaythanhtoan.setForeground(new java.awt.Color(0, 0, 0));
        txtNgaythanhtoan.setText("2025-07-20");
        txtNgaythanhtoan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNgaythanhtoanActionPerformed(evt);
            }
        });
        jPanel5.add(txtNgaythanhtoan, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 450, 120, -1));

        txtMaNV.setBackground(new java.awt.Color(204, 204, 204));
        txtMaNV.setForeground(new java.awt.Color(0, 0, 0));
        jPanel5.add(txtMaNV, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 450, 120, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Tên Nhân Viên :");
        jPanel5.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 430, -1, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Ngày Thanh Toán: ");
        jPanel5.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 430, -1, -1));

        txtTennhanvien.setBackground(new java.awt.Color(204, 204, 204));
        txtTennhanvien.setForeground(new java.awt.Color(0, 0, 0));
        jPanel5.add(txtTennhanvien, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 450, 120, -1));

        btnHienthi.setBackground(new java.awt.Color(153, 153, 153));
        btnHienthi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnHienthi.setForeground(new java.awt.Color(255, 255, 255));
        btnHienthi.setText("Hiển thị tất cả");
        btnHienthi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHienthiActionPerformed(evt);
            }
        });
        jPanel5.add(btnHienthi, new org.netbeans.lib.awtextra.AbsoluteConstraints(787, 447, 150, 30));

        btnLocbangluong.setBackground(new java.awt.Color(153, 153, 153));
        btnLocbangluong.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLocbangluong.setForeground(new java.awt.Color(255, 255, 255));
        btnLocbangluong.setText("Lọc");
        btnLocbangluong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocbangluongActionPerformed(evt);
            }
        });
        jPanel5.add(btnLocbangluong, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 450, -1, 30));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/quanlychamcongpro (2).png"))); // NOI18N
        jPanel5.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 0, 1010, 520));

        jTabbedPane2.addTab("Chi Tiết Bảng Lương", jPanel5);

        getContentPane().add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 550));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnChamCongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChamCongActionPerformed
        chamCongNhanVien(); // TODO add your handling code here:
    }//GEN-LAST:event_btnChamCongActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        paySalary();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtMaNhanVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaNhanVienActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaNhanVienActionPerformed

    private void btnBangLuongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBangLuongActionPerformed
        // TODO add your handling code here:
        fillBangLuong1();
    }//GEN-LAST:event_btnBangLuongActionPerformed

    private void btnLocbangluongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocbangluongActionPerformed
        // TODO add your handling code here:
        fillToTableBangLuongChiTiet1();
    }//GEN-LAST:event_btnLocbangluongActionPerformed

    private void btnHienthiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHienthiActionPerformed
        // TODO add your handling code here:
        fillPhieuTraLuongToTable();
    }//GEN-LAST:event_btnHienthiActionPerformed

    private void txtHienThiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHienThiActionPerformed
        // TODO add your handling code here:
        fillBangLuongTheoThang(thang, nam);
    }//GEN-LAST:event_txtHienThiActionPerformed

    private void txtNgayBatDauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNgayBatDauActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNgayBatDauActionPerformed

    private void txtNgaythanhtoanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNgaythanhtoanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNgaythanhtoanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBangLuong;
    private javax.swing.JButton btnChamCong;
    private javax.swing.JButton btnHienthi;
    private javax.swing.JButton btnLocbangluong;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel jblChamCong;
    private javax.swing.JTable tblBangLuong;
    private javax.swing.JTable tblBangLuongChiTiet1;
    private javax.swing.JTable tblChamCong;
    private javax.swing.JButton txtHienThi;
    private javax.swing.JTextField txtMaNV;
    private javax.swing.JTextField txtMaNhanVien;
    private javax.swing.JTextField txtNgayBatDau;
    private javax.swing.JTextField txtNgayHienTai;
    private javax.swing.JTextField txtNgaythanhtoan;
    private javax.swing.JTextField txtTenNhanVien1;
    private javax.swing.JTextField txtTennhanvien;
    // End of variables declaration//GEN-END:variables
}
