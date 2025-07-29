/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package ui.manager;

import dao.ChiTietMonAnDAO;
import dao.HoaDonDAO;
import dao.LoaiNuocUongDAO;
import dao.MonAnDAO;
import dao.NuocUongDAO;
import dao.impl.ChiTietMonAnDAOImpl;
import dao.impl.HoaDonDAOImpl;
import dao.impl.LoaiNuocUongDAOImpl;
import dao.impl.MonAnDAOImpl;
import dao.impl.NuocUongDAOImpl;
import entity.ChiTietMonAn;
import entity.LoaiNuocUong;
import entity.MonAn;
import entity.NuocUong;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import static java.nio.file.Files.list;
import java.nio.file.StandardCopyOption;
import static java.rmi.Naming.list;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import static java.util.Collections.list;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import pro.RoundedPanel;
import util.XJdbc;
import entity.NuocUong;

/**
 *
 * @author
 */
public class QuanLyMonAn extends javax.swing.JDialog {

    private int maMonAnHienTai = -1;

    private String duongDanAnhLoai = null;
    private String tenAnhLoai = null;
    private String tenAnh = null;
    ChiTietMonAnDAO monAnDAO = new ChiTietMonAnDAOImpl();
    private String duongDanAnh = null;

    public QuanLyMonAn(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setTitle("Quản Lý Món An");
        setResizable(false);
        setLocationRelativeTo(null);
        tblBangLoaiMon.setBackground(new Color(200, 200, 200)); // Màu xám nhạt
        tblBangChiTietMon.setBackground(new Color(200, 200, 200)); // Màu xám nhạt
        getContentPane().setBackground(new Color(200, 200, 200)); // Màu xám vừa

        fillLoaiMonAn();
        fillChiTietMonAn(ABORT);
        tblBangLoaiMon.setFont(new Font("Arial", Font.BOLD, 20));
        tblBangChiTietMon.setFont(new Font("Arial", Font.BOLD, 15));
        tblBangLoaiMon.setShowHorizontalLines(false); // Ẩn đường ngang
        tblBangLoaiMon.setShowVerticalLines(false);   // Ẩn đường dọc
        tblBangChiTietMon.setShowVerticalLines(false);   // Ẩn đường dọc
        tblBangChiTietMon.setShowHorizontalLines(false); // Ẩn đường ngang
        fillComboBoxLoaiMon();
        styleTable(tblBangLoaiMon);
        styleTable(tblBangChiTietMon);
        fillLoaiNuocUong();
        
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

    //-------Thêm Hình Anh Sắc Nét-----------------
    public ImageIcon resizeImageIcon(URL imageUrl, int width, int height) {
        ImageIcon icon = new ImageIcon(imageUrl);
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
    List<Integer> danhSachMaMonAn = new ArrayList<>();

    public void fillLoaiMonAn() {
        DefaultTableModel model = (DefaultTableModel) tblBangLoaiMon.getModel();
        model.setRowCount(0);  // Xóa các dòng hiện tại trong bảng

        MonAnDAOImpl dao = new MonAnDAOImpl();
        List<MonAn> monAnList = dao.findAll();

        //-------Thêm Hình Anh Sắc Nét-----------------
        for (MonAn monAn : monAnList) {
            ImageIcon icon = null;

            try {
                if (monAn.getHinhAnh() != null && !monAn.getHinhAnh().isEmpty()) {
                    URL imgURL = getClass().getResource("/images/" + monAn.getHinhAnh());
                    if (imgURL != null) {
                        icon = resizeImageIcon(imgURL, 100, 100); // dùng hàm đã viết
                    } else {
                        System.out.println("Không tìm thấy ảnh: " + monAn.getHinhAnh());
                    }

                }
            } catch (Exception e) {
                System.out.println("Lỗi load ảnh: " + e.getMessage());
            }
            model.addRow(new Object[]{icon, monAn.getTenMonAn(), monAn.getMaMonAn()});
            danhSachMaMonAn.add(monAn.getMaMonAn());
        }

        // Căn giữa ảnh (cột 0)
        tblBangLoaiMon.getColumnModel().getColumn(0).setPreferredWidth(110);
        tblBangLoaiMon.setRowHeight(110);
        tblBangLoaiMon.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
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

        // Căn giữa tên món ăn (cột số 1)
        DefaultTableCellRenderer centerTextRenderer = new DefaultTableCellRenderer();
        centerTextRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerTextRenderer.setVerticalAlignment(SwingConstants.CENTER);
        centerTextRenderer.setFont(new Font("Arial", Font.PLAIN, 20));
        tblBangLoaiMon.getColumnModel().getColumn(1).setCellRenderer(centerTextRenderer);

        // Bắt sự kiện click dòng → load chi tiết món ăn
        tblBangLoaiMon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblBangLoaiMon.getSelectedRow();
                if (row >= 0 && row < monAnList.size()) {
                    int maMonAn = monAnList.get(row).getMaMonAn();
                    fillChiTietMonAn(maMonAn);
                } else {
                    System.out.println("Row out of bounds: " + row + ", monAnList size: " + monAnList.size());
                }
            }

        });
    }
    List<Integer> danhSachMaChiTiet = new ArrayList<>();

    private void fillChiTietMonAn(int maMonAn) {
        DefaultTableModel model = (DefaultTableModel) tblBangChiTietMon.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        // 🧨 FIX QUAN TRỌNG: Clear danh sách mã chi tiết trước khi load lại
        danhSachMaChiTiet.clear();

        ChiTietMonAnDAO dao = new ChiTietMonAnDAOImpl();
        List<ChiTietMonAn> list = dao.findByMonAnId(maMonAn);

        for (ChiTietMonAn ct : list) {
            ImageIcon icon = null;
            try {
                if (ct.getHinhAnh() != null && !ct.getHinhAnh().isEmpty()) {
                    URL imgURL = getClass().getResource("/images/" + ct.getHinhAnh());
                    if (imgURL != null) {
                        icon = new ImageIcon(new ImageIcon(imgURL)
                                .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                    }
                }
            } catch (Exception e) {
                System.out.println("Lỗi load ảnh: " + e.getMessage());
            }
            DecimalFormat df = new DecimalFormat("#,###");
            Object[] row = {
                icon,
                ct.getTenMon(),
                df.format(ct.getGia())
            };
            model.addRow(row);
            danhSachMaChiTiet.add(ct.getMaChiTiet()); // danh sách được làm mới
        }

        tblBangChiTietMon.setRowHeight(100);
        tblBangChiTietMon.getColumnModel().getColumn(0).setPreferredWidth(100);

        // (Renderer giữ nguyên)
        // Renderer hiển thị ảnh
        tblBangChiTietMon.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel lbl = new JLabel();
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setVerticalAlignment(SwingConstants.CENTER);
                if (value instanceof ImageIcon) {
                    lbl.setIcon((ImageIcon) value);
                }
                return lbl;
            }
        });

        // Renderer căn giữa các cột còn lại
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < tblBangChiTietMon.getColumnCount(); i++) {
            tblBangChiTietMon.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    LoaiNuocUongDAO loaiDAO = new LoaiNuocUongDAOImpl();

public void fillLoaiNuocUong() {
    List<LoaiNuocUong> list = loaiDAO.findAll();
    DefaultTableModel model = (DefaultTableModel) tblLoaiNuoc.getModel();
    model.setRowCount(0);
    for (LoaiNuocUong loai : list) {
        model.addRow(new Object[]{
            loai.getMaLoaiNuoc(),
            loai.getHinhAnh(),
            loai.getTenLoai()
            
        });
    }tblLoaiNuoc.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        int row = tblLoaiNuoc.getSelectedRow();
        if (row != -1) {
            int maLoai = (int) tblLoaiNuoc.getValueAt(row, 0);
            fillNuocUongTheoLoai(maLoai);
        }
    }
});

}NuocUongDAO nuocDAO = new NuocUongDAOImpl();

public void fillNuocUongTheoLoai(int maLoai) {
    List<NuocUong> list = nuocDAO.findByLoaiNuocUongId(maLoai);
    DefaultTableModel model = (DefaultTableModel) tblNuocUong.getModel();
    model.setRowCount(0);
    for (NuocUong nuoc : list) {
        model.addRow(new Object[]{
            nuoc.getMaNuocUong(),
             nuoc.getHinhAnh(),
            nuoc.getTenNuocUong(),
            nuoc.getGiaBan(),
        });
    }
}

    private void lamMoiLoai() {
        txtTenLoai.setText("");
        lblHinhLoai.setIcon(null);
        tenAnhLoai = null;
        duongDanAnhLoai = null;
        JOptionPane.showMessageDialog(null, "Đã làm mới toàn bộ thông tin.", "Làm mới", JOptionPane.INFORMATION_MESSAGE);
    }

    private void LamMoiChiTietMon() {
        txtTen.setText("");
        txtPrice.setText("");
        cboLoai.setSelectedIndex(0);
        lblHinhChiTiet.setIcon(null);
        tenAnh = null;
        duongDanAnh = null;
    }

    private void themMoiLoaiMonAn() {
        try {
            String tenLoai = txtTenLoai.getText().trim();

            if (tenLoai.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập tên loại món.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (tenAnhLoai == null || duongDanAnhLoai == null) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn hình ảnh cho loại món ăn.", "Thiếu hình ảnh", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Tạo thư mục ảnh nếu chưa có
            File folder = new File("images");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Sao chép ảnh vào thư mục
            File source = new File(duongDanAnhLoai);
            File dest = new File("images/" + tenAnhLoai);
            Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Gọi DAO để thêm món ăn
            MonAn monAn = new MonAn();
            monAn.setTenMonAn(tenLoai);
            monAn.setHinhAnh(tenAnhLoai);

            MonAnDAO monAnDAO = new MonAnDAOImpl();
            MonAn monDaThem = monAnDAO.create(monAn);

            if (monDaThem.getMaMonAn() != 0) {
                JOptionPane.showMessageDialog(null, "✔️ Thêm loại món ăn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);

                // Reset form
                txtTenLoai.setText("");
                lblHinhLoai.setIcon(null);
                tenAnhLoai = null;
                duongDanAnhLoai = null;

                // Load lại bảng và combobox
//                fillChiTietMonAn(ABORT);
                loadComboBoxLoai();
            } else {
                JOptionPane.showMessageDialog(null, "❌ Thêm loại món ăn thất bại!", "Thất bại", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "⚠️ Lỗi: " + ex.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }

        fillLoaiMonAn();
    }

    private void themChiTietMonAnMoi() {
        try {
            // Lấy dữ liệu từ form
            String tenMon = txtTen.getText().trim();
            String giaStr = txtPrice.getText().trim();
            String tenLoaiMon = cboLoai.getSelectedItem().toString();

            // Kiểm tra đầu vào
            if (tenMon.isEmpty() || giaStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Không được để trống tên món hoặc giá.");
                return;
            }

            if (tenAnh == null || duongDanAnh == null || tenAnh.isEmpty() || duongDanAnh.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn ảnh món ăn.");
                return;
            }

            double gia;
            try {
                gia = Double.parseDouble(giaStr);
                if (gia <= 0) {
                    JOptionPane.showMessageDialog(null, "Giá phải lớn hơn 0.");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Giá không hợp lệ.");
                return;
            }

            // Tạo thư mục nếu chưa có
            File imagesDir = new File("images");
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }

            // Đường dẫn gốc và đích
            File sourceFile = new File(duongDanAnh);
            File destFile = new File(imagesDir, tenAnh);

            // Chỉ copy nếu file gốc và đích khác nhau
            if (!sourceFile.getAbsolutePath().equals(destFile.getAbsolutePath())) {
                try {
                    Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ioEx) {
                    JOptionPane.showMessageDialog(null, "Không thể sao chép ảnh: " + ioEx.getMessage());
                    return;
                }
            }

            // Lấy mã món ăn từ tên loại món
            MonAnDAO monAnDAO = new MonAnDAOImpl();
            MonAn monAn = monAnDAO.findByTen(tenLoaiMon);

            if (monAn == null) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy loại món: " + tenLoaiMon);
                return;
            }

            // Tạo đối tượng chi tiết món ăn
            ChiTietMonAn chiTiet = new ChiTietMonAn();
            chiTiet.setTenMon(tenMon);
            chiTiet.setGia(gia);
            chiTiet.setHinhAnh(tenAnh); // chỉ lưu tên file
            chiTiet.setMaMonAn(monAn.getMaMonAn());
            System.out.println("" + chiTiet);
            // Gọi DAO để lưu vào DB
            ChiTietMonAnDAO chiTietDAO = new ChiTietMonAnDAOImpl();
            chiTietDAO.create(chiTiet);
            JOptionPane.showMessageDialog(null, "✔️ Thêm món ăn thành công!");

            // Reset form
            txtTen.setText("");
            txtPrice.setText("");
            lblHinhChiTiet.setIcon(null);
            tenAnh = null;
            duongDanAnh = null;

            // Load lại bảng
            fillChiTietMonAn(monAn.getMaMonAn());

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi: " + ex.getMessage());
        }
    }

    private void fillComboBoxLoaiMon() {
        cboLoai.removeAllItems(); // Xóa dữ liệu cũ

        MonAnDAOImpl dao = new MonAnDAOImpl();
        List<MonAn> list = dao.findAll();

        for (MonAn mon : list) {
            cboLoai.addItem(mon.getTenMonAn());
        }
    }

    private int getMaMonDangChon() {
        int selectedRow = tblBangLoaiMon.getSelectedRow(); // bảng bên trái chứa danh sách món
        if (selectedRow == -1) {
            return -1; // hoặc throw exception nếu cần
        }
        return danhSachMaMonAn.get(selectedRow); // danh sách chứa mã món ăn
    }

    private void xoaChiTietMonAn() {
        int selectedRow = tblBangChiTietMon.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn món ăn để xóa.");
            return;
        }

        int maChiTiet = danhSachMaChiTiet.get(selectedRow);
        int maMonAn = getMaMonDangChon();
        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Bạn có chắc muốn xóa món ăn này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        System.out.println("ádasdasd"+maChiTiet);
        try {
            ChiTietMonAnDAO monAnDAO = new ChiTietMonAnDAOImpl();
            
            monAnDAO.deleteById(maChiTiet);
            fillChiTietMonAn(maMonAn);
            JOptionPane.showMessageDialog(null, "✅ Xóa món ăn thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "❌ Lỗi khi xóa món ăn: " + e.getMessage());
        }
    }

    private void clearBangChiTietMon() {
        DefaultTableModel model = (DefaultTableModel) tblBangChiTietMon.getModel();
        model.setRowCount(0); // Xóa hết tất cả các dòng
    }

    public void xoaMonAnVaChiTietTheoMa() {
        int maMonAn = getMaMonDangChon();

        if (maMonAn == -1) {
            JOptionPane.showMessageDialog(null, "️ Vui lòng chọn một món ăn để xóa.");
            return;
        }

        int xacNhan = JOptionPane.showConfirmDialog(
                null,
                "⚠️ Bạn có chắc chắn muốn xóa món ăn có mã: " + maMonAn + " không?\n"
                + "Thao tác này sẽ xóa toàn bộ chi tiết liên quan.",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (xacNhan != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            HoaDonDAO hoadon = new HoaDonDAOImpl();
            ChiTietMonAnDAO chiTietDAO = new ChiTietMonAnDAOImpl();
        

            MonAnDAO monAnDAO = new MonAnDAOImpl();
           
            if (chiTietDAO == null || monAnDAO == null) {
                JOptionPane.showMessageDialog(null, " DAO chưa được khởi tạo đúng.");
                return;
            }
            
            chiTietDAO.deleteByMaMonAn(maMonAn);

            monAnDAO.deleteById(maMonAn);
            
            fillLoaiMonAn();
            fillComboBoxLoaiMon();
            clearBangChiTietMon();

            JOptionPane.showMessageDialog(null, "Đã xóa món ăn và các chi tiết liên quan thành công.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, " Lỗi không xác định khi xóa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void suaGiaMonAn() {
        int selectedRow = tblBangChiTietMon.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn món ăn để sửa giá.");
            return;
        }

        // Lấy tên món từ bảng (cột 1 là tên món)
        String tenMon = tblBangChiTietMon.getValueAt(selectedRow, 1).toString();

        ChiTietMonAnDAO dao = new ChiTietMonAnDAOImpl();
        ChiTietMonAn mon = dao.findByTenMon(tenMon);

        if (mon == null) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy món ăn trong CSDL.");
            return;
        }

        String giaStr = JOptionPane.showInputDialog(null, "Nhập giá mới cho món: " + tenMon, "Sửa giá", JOptionPane.QUESTION_MESSAGE);
        if (giaStr == null || giaStr.trim().isEmpty()) {
            return;
        }
        try {
            double giaMoi = Double.parseDouble(giaStr.trim());
            if (giaMoi <= 0) {
                JOptionPane.showMessageDialog(null, "Giá phải lớn hơn 0.");
                return;
            }
            mon.setGia(giaMoi);
            dao.update(mon);  // ✅ gọi DAO update
            JOptionPane.showMessageDialog(null, " Đã cập nhật giá món ăn!");
            fillChiTietMonAn(getMaMonDangChon());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Giá phải là số hợp lệ.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, " Lỗi khi cập nhật: " + ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblBangLoaiMon = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBangChiTietMon = new javax.swing.JTable();
        btnXoaLoaiMon = new javax.swing.JButton();
        btnXoaMonAn = new javax.swing.JButton();
        btnChange = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        lblHinhLoai = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtTenLoai = new javax.swing.JTextField();
        btnThemMoiLoai = new javax.swing.JButton();
        btnClearLoai = new javax.swing.JButton();
        btnMoDuongDanLoai = new javax.swing.JButton();
        lblHinhChiTiet = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnMoDuongDan = new javax.swing.JButton();
        txtPrice = new javax.swing.JTextField();
        cboLoai = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtTen = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        btnThemMoi = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblNuocUong = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblLoaiNuoc = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();

        jLabel4.setText("jLabel2");

        jTextField5.setText("jTextField3");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản Lý Món Ăn- Đồ Uống");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane2.setBackground(new java.awt.Color(102, 102, 102));

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblBangLoaiMon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Hình Ảnh", "Loại Món Ăn"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblBangLoaiMon);
        if (tblBangLoaiMon.getColumnModel().getColumnCount() > 0) {
            tblBangLoaiMon.getColumnModel().getColumn(0).setMinWidth(130);
            tblBangLoaiMon.getColumnModel().getColumn(0).setMaxWidth(200);
        }

        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 40, 360, 460));

        tblBangChiTietMon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Hình Ảnh", "Tên Món Ăn", "Đơn Giá VND"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblBangChiTietMon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBangChiTietMonMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblBangChiTietMon);
        if (tblBangChiTietMon.getColumnModel().getColumnCount() > 0) {
            tblBangChiTietMon.getColumnModel().getColumn(0).setMinWidth(130);
            tblBangChiTietMon.getColumnModel().getColumn(0).setMaxWidth(150);
            tblBangChiTietMon.getColumnModel().getColumn(1).setMinWidth(230);
            tblBangChiTietMon.getColumnModel().getColumn(1).setMaxWidth(230);
        }

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 40, 570, 460));

        btnXoaLoaiMon.setBackground(new java.awt.Color(153, 153, 153));
        btnXoaLoaiMon.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnXoaLoaiMon.setForeground(new java.awt.Color(255, 255, 255));
        btnXoaLoaiMon.setText("Xóa Loại Món");
        btnXoaLoaiMon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaLoaiMonActionPerformed(evt);
            }
        });
        jPanel1.add(btnXoaLoaiMon, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 520, 310, -1));

        btnXoaMonAn.setBackground(new java.awt.Color(153, 153, 153));
        btnXoaMonAn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnXoaMonAn.setForeground(new java.awt.Color(255, 255, 255));
        btnXoaMonAn.setText("Xóa Món Ăn");
        btnXoaMonAn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaMonAnActionPerformed(evt);
            }
        });
        jPanel1.add(btnXoaMonAn, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 520, 280, -1));

        btnChange.setBackground(new java.awt.Color(153, 153, 153));
        btnChange.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnChange.setForeground(new java.awt.Color(255, 255, 255));
        btnChange.setText("Sửa Giá");
        btnChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeActionPerformed(evt);
            }
        });
        jPanel1.add(btnChange, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 520, 220, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(204, 204, 204));
        jLabel2.setText("Danh Sách Món Ăn");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 10, 170, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(204, 204, 204));
        jLabel9.setText("Danh Sách Loại Món Ăn");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 170, -1));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/quanlymonanpro (2).png"))); // NOI18N
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1060, 570));

        jTabbedPane2.addTab("Món Ăn", jPanel1);

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(204, 204, 204));
        jLabel11.setText("Hình Ảnh");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, 26));

        lblHinhLoai.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.add(lblHinhLoai, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 120, 150));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(204, 204, 204));
        jLabel12.setText("Tên Loại:");
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, -1, 26));

        txtTenLoai.setBackground(new java.awt.Color(102, 102, 102));
        txtTenLoai.setForeground(new java.awt.Color(255, 255, 255));
        txtTenLoai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenLoaiActionPerformed(evt);
            }
        });
        jPanel2.add(txtTenLoai, new org.netbeans.lib.awtextra.AbsoluteConstraints(171, 46, 220, 28));

        btnThemMoiLoai.setBackground(new java.awt.Color(102, 102, 102));
        btnThemMoiLoai.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThemMoiLoai.setForeground(new java.awt.Color(204, 204, 204));
        btnThemMoiLoai.setText("Thêm Mới");
        btnThemMoiLoai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemMoiLoaiActionPerformed(evt);
            }
        });
        jPanel2.add(btnThemMoiLoai, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 90, -1, 30));

        btnClearLoai.setBackground(new java.awt.Color(102, 102, 102));
        btnClearLoai.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnClearLoai.setForeground(new java.awt.Color(204, 204, 204));
        btnClearLoai.setText("Làm Mới");
        btnClearLoai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearLoaiActionPerformed(evt);
            }
        });
        jPanel2.add(btnClearLoai, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 90, 85, 30));

        btnMoDuongDanLoai.setBackground(new java.awt.Color(102, 102, 102));
        btnMoDuongDanLoai.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMoDuongDanLoai.setForeground(new java.awt.Color(204, 204, 204));
        btnMoDuongDanLoai.setText("Chọn Đường Dẫn Ảnh");
        btnMoDuongDanLoai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoDuongDanLoaiActionPerformed(evt);
            }
        });
        jPanel2.add(btnMoDuongDanLoai, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 170, 220, -1));

        lblHinhChiTiet.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.add(lblHinhChiTiet, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, 120, 150));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(204, 204, 204));
        jLabel5.setText("Hình Ảnh");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 270, -1, 26));

        btnMoDuongDan.setBackground(new java.awt.Color(102, 102, 102));
        btnMoDuongDan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMoDuongDan.setForeground(new java.awt.Color(204, 204, 204));
        btnMoDuongDan.setText("Chọn Đường Dẫn Ảnh");
        btnMoDuongDan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoDuongDanActionPerformed(evt);
            }
        });
        jPanel2.add(btnMoDuongDan, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 470, -1, -1));

        txtPrice.setBackground(new java.awt.Color(102, 102, 102));
        txtPrice.setForeground(new java.awt.Color(255, 255, 255));
        txtPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPriceActionPerformed(evt);
            }
        });
        jPanel2.add(txtPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 420, 220, 28));

        cboLoai.setBackground(new java.awt.Color(102, 102, 102));
        cboLoai.setForeground(new java.awt.Color(255, 255, 255));
        cboLoai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLoaiActionPerformed(evt);
            }
        });
        jPanel2.add(cboLoai, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 360, 220, 30));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(204, 204, 204));
        jLabel6.setText("Đơn Giá:");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 390, -1, 26));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(204, 204, 204));
        jLabel3.setText("Loại:");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 330, -1, 26));

        txtTen.setBackground(new java.awt.Color(102, 102, 102));
        txtTen.setForeground(new java.awt.Color(255, 255, 255));
        txtTen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenActionPerformed(evt);
            }
        });
        jPanel2.add(txtTen, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 300, 220, 28));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(204, 204, 204));
        jLabel7.setText("Tên Món Ăn/Đồ Uống:");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 270, -1, 26));

        btnThemMoi.setBackground(new java.awt.Color(102, 102, 102));
        btnThemMoi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThemMoi.setForeground(new java.awt.Color(204, 204, 204));
        btnThemMoi.setText("Thêm Mới");
        btnThemMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemMoiActionPerformed(evt);
            }
        });
        jPanel2.add(btnThemMoi, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 470, -1, 30));

        btnClear.setBackground(new java.awt.Color(102, 102, 102));
        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnClear.setForeground(new java.awt.Color(204, 204, 204));
        btnClear.setText("Làm Mới");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        jPanel2.add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 470, 100, 30));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/quanlynhanvienok (2).png"))); // NOI18N
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 0, 1080, 570));

        jTabbedPane2.addTab("Thêm Món Ăn", jPanel2);

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblNuocUong.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã  Nước", "Hình Ảnh", "Tên Nước Uống", "Đơn Gía"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblNuocUong);

        jPanel3.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 20, 630, -1));

        tblLoaiNuoc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Mã Loại Nước", "Hình Ảnh", "Tên Loại Nước Uống"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tblLoaiNuoc);

        jPanel3.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 410, -1));

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Mã Loại Nước", "Hình Ảnh", "Tên Nước Uống"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(jTable3);

        jPanel3.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 390, -1));

        jTabbedPane2.addTab("Nước Uống", jPanel3);

        getContentPane().add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1070, 600));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClearLoaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearLoaiActionPerformed
        // TODO add your handling code here:
        lamMoiLoai();
    }//GEN-LAST:event_btnClearLoaiActionPerformed

    private void btnThemMoiLoaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemMoiLoaiActionPerformed
        themMoiLoaiMonAn();// TODO add your handling code here:

    }//GEN-LAST:event_btnThemMoiLoaiActionPerformed

    private void btnMoDuongDanLoaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoDuongDanLoaiActionPerformed
        ActionListener[] listeners = btnMoDuongDanLoai.getActionListeners();
        for (ActionListener listener : listeners) {
            btnMoDuongDanLoai.removeActionListener(listener);
        }
        btnMoDuongDanLoai.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Chọn hình ảnh");

                // Chỉ cho phép chọn các file ảnh
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Hình ảnh", "jpg", "jpeg", "png", "gif");
                fileChooser.setFileFilter(filter);

                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    tenAnhLoai = selectedFile.getName();
                    duongDanAnhLoai = selectedFile.getAbsolutePath();

                    try {
                        ImageIcon icon = new ImageIcon(duongDanAnhLoai);
                        // Nếu label có width và height bằng 0 thì scale sẽ lỗi, nên cần check
                        int width = lblHinhLoai.getWidth() > 0 ? lblHinhLoai.getWidth() : 100;
                        int height = lblHinhLoai.getHeight() > 0 ? lblHinhLoai.getHeight() : 100;

                        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        lblHinhLoai.setIcon(new ImageIcon(img));

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Không thể hiển thị hình ảnh: " + ex.getMessage(), "Lỗi hiển thị", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }//GEN-LAST:event_btnMoDuongDanLoaiActionPerformed

    private void txtTenLoaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenLoaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenLoaiActionPerformed

    private void cboLoaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLoaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboLoaiActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        LamMoiChiTietMon();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnThemMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemMoiActionPerformed
        themChiTietMonAnMoi();// TODO add your handling code here:
    }//GEN-LAST:event_btnThemMoiActionPerformed

    private void txtPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPriceActionPerformed

    }//GEN-LAST:event_txtPriceActionPerformed

    private void txtTenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenActionPerformed

    private void btnMoDuongDanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoDuongDanActionPerformed
        ActionListener[] listeners = btnMoDuongDan.getActionListeners();
        for (ActionListener listener : listeners) {
            btnMoDuongDan.removeActionListener(listener);
        }//TODO add your handling code here:
        btnMoDuongDan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Chọn hình ảnh");

                FileNameExtensionFilter filter = new FileNameExtensionFilter("Hình ảnh", "jpg", "jpeg", "png", "gif");
                fileChooser.setFileFilter(filter);

                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    tenAnh = selectedFile.getName();
                    duongDanAnh = selectedFile.getAbsolutePath();

                    // Hiển thị ảnh lên giao diện
                    ImageIcon icon = new ImageIcon(duongDanAnh);
                    Image img = icon.getImage().getScaledInstance(lblHinhChiTiet.getWidth(), lblHinhChiTiet.getHeight(), Image.SCALE_SMOOTH);
                    lblHinhChiTiet.setIcon(new ImageIcon(img));
                }
            }
        });
    }//GEN-LAST:event_btnMoDuongDanActionPerformed

    private void btnChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeActionPerformed
        suaGiaMonAn();  // TODO add your handling code here:
    }//GEN-LAST:event_btnChangeActionPerformed

    private void btnXoaLoaiMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaLoaiMonActionPerformed
        xoaMonAnVaChiTietTheoMa();
    }//GEN-LAST:event_btnXoaLoaiMonActionPerformed


    private void btnXoaMonAnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaMonAnActionPerformed
        // TODO add your handling code here:
        xoaChiTietMonAn();

    }//GEN-LAST:event_btnXoaMonAnActionPerformed

    private void tblBangChiTietMonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBangChiTietMonMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblBangChiTietMonMouseClicked

//    private void fillChiTietMonAnTheoMonAn() {
//        int loaiIndex = cboLoai.getSelectedIndex();
//        if (loaiIndex == -1) {
//            return;
//        }
//
//        int maMonAn = loaiIndex + 1;
//        ChiTietMonAnDAO dao = new ChiTietMonAnDAOImpl(); // hoặc inject sẵn
//
//        List<ChiTietMonAn> list = dao.findByMaMonAn(maMonAn);
//        DefaultTableModel model = (DefaultTableModel) tblBangChiTietMon.getModel();
//        model.setRowCount(0);
//
//        for (ChiTietMonAn ct : list) {
//            ImageIcon icon = null;
//            File imgFile = new File("images/" + ct.getHinhAnh());
//            if (imgFile.exists()) {
//                Image img = new ImageIcon(imgFile.getAbsolutePath())
//                        .getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
//                icon = new ImageIcon(img);
//            }
//
//            model.addRow(new Object[]{
//                icon != null ? icon : "Không có ảnh",
//                ct.getTenMon(),
//                ct.getGia(),
//                ct.getMaChiTiet() // nằm cột 4
//            });
//        }
//    }
    private void loadComboBoxLoai() {
        try {
            cboLoai.removeAllItems(); // Xóa các mục cũ

            String sql = "SELECT TenMonAn FROM MonAn";
            ResultSet rs = XJdbc.executeQuery(sql);
            while (rs.next()) {
                cboLoai.addItem(rs.getString("TenMonAn")); // hoặc MaMonAn nếu cần
            }
            rs.getStatement().getConnection().close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi load combobox loại món ăn: " + e.getMessage());
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChange;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnClearLoai;
    private javax.swing.JButton btnMoDuongDan;
    private javax.swing.JButton btnMoDuongDanLoai;
    private javax.swing.JButton btnThemMoi;
    private javax.swing.JButton btnThemMoiLoai;
    private javax.swing.JButton btnXoaLoaiMon;
    private javax.swing.JButton btnXoaMonAn;
    private javax.swing.JComboBox<String> cboLoai;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JLabel lblHinhChiTiet;
    private javax.swing.JLabel lblHinhLoai;
    private javax.swing.JTable tblBangChiTietMon;
    private javax.swing.JTable tblBangLoaiMon;
    private javax.swing.JTable tblLoaiNuoc;
    private javax.swing.JTable tblNuocUong;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtTen;
    private javax.swing.JTextField txtTenLoai;
    // End of variables declaration//GEN-END:variables
}
