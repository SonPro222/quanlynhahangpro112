/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package ui.manager;

import dao.ChiTietMonAnDAO;
import dao.HoaDonChiTietDAO;
import dao.HoaDonDAO;
import dao.LoaiNuocUongDAO;
import dao.NuocUongDAO;
import dao.TonKhoDAO;
import dao.XuatKhoNuocUongDAO;
import dao.impl.ChiTietMonAnDAOImpl;
import dao.impl.HoaDonChiTietDAOImpl;
import dao.impl.HoaDonDAOImpl;
import dao.impl.LoaiNuocUongDAOImpl;
import dao.impl.MonAnDAOImpl;
import dao.impl.NuocUongDAOImpl;
import dao.impl.TonKhoDAOImpl;
import dao.impl.XuatKhoNuocUongDAOImpl;
import entity.TonKho;
import entity.ChiTietMonAn;
import entity.HoaDon;
import entity.HoaDonChiTiet;
import entity.LoaiNuocUong;
import entity.MonAn;
import entity.NuocUong;
import entity.XuatKhoNuocUong;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractCellEditor;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import util.XAuth;
import uui.Auth;

/**
 *
 * @author dangt
 */
public class DatMon extends javax.swing.JDialog {

    ArrayList<Double> DonGiaMonAnx = new ArrayList();
    ArrayList<Integer> MaMonAnx = new ArrayList();
    ArrayList<Double> DonGiaNuocUongx = new ArrayList();
    ArrayList<Integer> MaNuocUongx = new ArrayList();
    private int soBan;
    private List<HoaDonChiTiet> dsDatMon = new ArrayList<>();
    private HoaDonDAO hoaDonDAO = new HoaDonDAOImpl();
    private HoaDonChiTietDAO chiTietDAO = new HoaDonChiTietDAOImpl();
    private BanAn parent;
    private HoaDon hoaDonHienTai;
    DecimalFormat df = new DecimalFormat("#,###");

    public DatMon(java.awt.Frame owner, BanAn parent, boolean modal, int soBan) {
        super(owner, modal);
        this.parent = parent;
        this.soBan = soBan;
        this.hoaDonHienTai = hoaDonHienTai;
        initComponents();
        setTitle("Đặt món cho Bàn số " + soBan);
        lblSoBan.setText("Đang đặt món cho Bàn số " + soBan);
        setResizable(false);
        setLocationRelativeTo(null);
        setResizable(false);          // <-- không cho kéo giãn
        setupTableStyles();
        fillLoaiMonAn();
        fillToTableNuocUong();
        loadChiTietMonAn(soBan);
        hoaDonDAO = new HoaDonDAOImpl();
        hoaDonHienTai = hoaDonDAO.findChuaThanhToanTheoBan(soBan);
        if (hoaDonHienTai != null) {
            hienThiHoaDon(hoaDonHienTai);
            loadChiTietMonAn(hoaDonHienTai.getMaHD());
        } else {
            taoHoaDonMoi();
        }
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                xoaHoaDonNeuKhongCoMon(); 
            }
        });
        styleTable(tblBangLoaiMon);
        styleTable(tblBangChiTietMon);
        styleTable(tblHoaDon);
        styleTable(tblDaChon);
        styleTable(tblNuocUong);
        tblBangChiTietMon.getColumnModel().getColumn(0).setMinWidth(0);
        tblBangChiTietMon.getColumnModel().getColumn(0).setMaxWidth(0);
        tblBangChiTietMon.getColumnModel().getColumn(0).setWidth(0);
        tblDaChon.getColumnModel().getColumn(0).setMinWidth(0);
        tblDaChon.getColumnModel().getColumn(0).setMaxWidth(0);
        tblDaChon.getColumnModel().getColumn(0).setWidth(0);
        tblDaChon.getColumnModel().getColumn(5).setMinWidth(0);
        tblDaChon.getColumnModel().getColumn(5).setMaxWidth(0);
        tblDaChon.getColumnModel().getColumn(5).setWidth(0);
      
tblHoaDon.getColumnModel().getColumn(3).setCellEditor(new SpinnerEditorGiamSoLuong(tblHoaDon, new SpinnerEditorGiamSoLuong.GiamSoLuongCallback() {
    @Override
    public void onSoLuongThayDoi(int row, int soLuongMoi) {
        DefaultTableModel model = (DefaultTableModel) tblHoaDon.getModel();
        int maHD = Integer.parseInt(txtMaHD.getText()); // Mã hóa đơn
        int maNuoc = (int) model.getValueAt(row, 1);     // Mã nước uống (cột 1)
        int maChiTiet = (int) model.getValueAt(row, 0);  // Mã chi tiết (cột 0)
        double donGia = (double) model.getValueAt(row, 4); // Đơn giá (cột 4)

        int soLuongCu = (int) model.getValueAt(row, 3);  // Số lượng cũ
        int chenhlech = soLuongCu - soLuongMoi;

        // Nếu khách giảm số lượng -> cộng lại vào tồn kho
        if (chenhlech > 0) {
            TonKhoDAO tonKhoDAO = new TonKhoDAOImpl();
            TonKho tonKho = tonKhoDAO.findByMaNuocUong(maNuoc);
            if (tonKho != null) {
                int soLuongMoiTonKho = tonKho.getSoLuongTong() + chenhlech;
                tonKhoDAO.updateSoLuong(maNuoc, soLuongMoiTonKho);
            }
        }

        // Cập nhật số lượng và thành tiền trong bảng
        model.setValueAt(soLuongMoi, row, 3);
        model.setValueAt(soLuongMoi * donGia, row, 5);

        // Cập nhật DB
        new HoaDonChiTietDAOImpl().updateSoLuongTheoMaHDVaMaNuoc(maHD, maNuoc, soLuongMoi);

        // Load lại chi tiết + tổng tiền
        loadChiTietMonAn(maHD);
        capNhatTongTienHoaDon();
    }
}));

    }

    //====================== ĐỊNH DẠNG BẢNG ==================//
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

    private void loadChiTietMonAn(int maHD) {
        List<HoaDonChiTiet> list = chiTietDAO.findByHoaDonId(maHD); // lấy từ DB
        dsDatMon.clear();
        dsDatMon.addAll(list);

        DefaultTableModel model = (DefaultTableModel) tblHoaDon.getModel();
        model.setRowCount(0); // xóa cũ

        for (HoaDonChiTiet ct : dsDatMon) {
            double thanhTien = ct.getSoLuong() * ct.getDonGia();
            model.addRow(new Object[]{
                ct.getMaChiTiet(),
                ct.getMaNuocUong(),
                ct.getTenMon(),
                ct.getSoLuong(),
                ct.getDonGia(),
                thanhTien
            });
        }
        System.out.println("Load lại chi tiết hóa đơn MaHD: " + maHD + ", Số món: " + list.size());

    }

    private void setupTableStyles() {
        tblBangLoaiMon.setFont(new Font("Arial", Font.BOLD, 20));
        tblBangChiTietMon.setFont(new Font("Arial", Font.BOLD, 15));
        tblBangLoaiMon.setShowHorizontalLines(false);
        tblBangLoaiMon.setShowVerticalLines(false);
        tblBangChiTietMon.setShowHorizontalLines(false);
        tblBangChiTietMon.setShowVerticalLines(false);
    }

    public void fillLoaiMonAn() {
        DefaultTableModel model = (DefaultTableModel) tblBangLoaiMon.getModel();
        model.setRowCount(0); // Xóa các dòng hiện tại trong bảng
        MonAnDAOImpl dao = new MonAnDAOImpl();
        List<MonAn> monAnList = dao.findAll(); // Lấy tất cả món ăn

        for (MonAn monAn : monAnList) {
            ImageIcon icon = null;
            try {
                if (monAn.getHinhAnh() != null && !monAn.getHinhAnh().isEmpty()) {
                    URL imgURL = getClass().getResource("/images/" + monAn.getHinhAnh());
                    if (imgURL != null) {
                        icon = new ImageIcon(new ImageIcon(imgURL)
                                .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                    }
                }
            } catch (Exception e) {
                System.out.println("Lỗi load ảnh cho món " + monAn.getTenMonAn() + ": " + e.getMessage());
            }
            model.addRow(new Object[]{icon, monAn.getTenMonAn()});
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
                if (row >= 0) {
                    // Lấy MonAn từ danh sách ban đầu để có MaMonAn chính xác
                    // Đảm bảo rằng monAnList vẫn có sẵn hoặc lấy lại từ DAO
                    MonAn selectedMonAn = monAnList.get(row);
                    fillChiTietMonAnTheoMonAn(selectedMonAn.getMaMonAn());
                }
            }
        });
    }

    private void fillChiTietMonAnTheoMonAn(int maMonAn) {
        DonGiaMonAnx.clear();
        MaMonAnx.clear();
        DefaultTableModel model = (DefaultTableModel) tblBangChiTietMon.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ
        ChiTietMonAnDAO dao = new ChiTietMonAnDAOImpl();
        List<ChiTietMonAn> list = dao.findByMonAnId(maMonAn); // Truyền maMonAn để lọc
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
                System.out.println("Lỗi load ảnh cho chi tiết món " + ct.getTenMon() + ": " + e.getMessage());
            }
            DonGiaMonAnx.add(ct.getGia());
            MaMonAnx.add(ct.getMaChiTiet());
            Object[] row = {
                ct.getMaChiTiet(),
                icon,
                ct.getTenMon(),
                ct.getGia(),
                1, // số lượng mặc định
                "Món ăn" // Loại món
            };
            model.addRow(row);
        }
        tblBangChiTietMon.setRowHeight(100);
        if (tblBangChiTietMon.getColumnCount() >= 4) {
            tblBangChiTietMon.getColumnModel().getColumn(4).setCellEditor(new SpinnerEditor());
        }

        tblBangChiTietMon.getColumnModel().getColumn(0).setMinWidth(0);
        tblBangChiTietMon.getColumnModel().getColumn(0).setMaxWidth(0);
        tblBangChiTietMon.getColumnModel().getColumn(0).setWidth(0);

        // Renderer ảnh (cột 1)
        tblBangChiTietMon.getColumnModel().getColumn(1).setCellRenderer(new TableCellRenderer() {
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
        for (int i = 2; i < tblBangChiTietMon.getColumnCount(); i++) {
            tblBangChiTietMon.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    public void fillToTableNuocUong() {
        DefaultTableModel model = (DefaultTableModel) tblNuocUong.getModel();
        model.setRowCount(0); // Xoá dữ liệu cũ

        LoaiNuocUongDAO dao = new LoaiNuocUongDAOImpl();
        List<LoaiNuocUong> nuocUongList = dao.findAll();

        for (LoaiNuocUong nuocUong : nuocUongList) {
            ImageIcon icon = null;
            try {
                if (nuocUong.getHinhAnh() != null && !nuocUong.getHinhAnh().isEmpty()) {
                    URL imgURL = getClass().getResource("/images/" + nuocUong.getHinhAnh());
                    if (imgURL != null) {
                        icon = new ImageIcon(new ImageIcon(imgURL)
                                .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                    }
                }
            } catch (Exception e) {
                System.out.println("Lỗi load ảnh cho " + nuocUong.getTenLoai() + ": " + e.getMessage());
            }

            // Thêm dòng vào bảng: mã (ẩn), ảnh, tên
            model.addRow(new Object[]{
                nuocUong.getMaLoaiNuoc(),
                icon,
                nuocUong.getTenLoai()
            });
        }

        // 👉 Ẩn cột mã loại nước uống (cột 0)
        tblNuocUong.getColumnModel().getColumn(0).setMinWidth(0);
        tblNuocUong.getColumnModel().getColumn(0).setMaxWidth(0);
        tblNuocUong.getColumnModel().getColumn(0).setWidth(0);

        // 👉 Renderer hình ảnh (cột 1)
        tblNuocUong.getColumnModel().getColumn(1).setPreferredWidth(110);
        tblNuocUong.setRowHeight(110);
        tblNuocUong.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                setHorizontalAlignment(SwingConstants.CENTER);
                setVerticalAlignment(SwingConstants.CENTER);
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

        // 👉 Renderer căn giữa tên loại nước uống (cột 2)
        DefaultTableCellRenderer centerTextRenderer = new DefaultTableCellRenderer();
        centerTextRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerTextRenderer.setVerticalAlignment(SwingConstants.CENTER);
        centerTextRenderer.setFont(new Font("Arial", Font.PLAIN, 18));
        tblNuocUong.getColumnModel().getColumn(2).setCellRenderer(centerTextRenderer);

        // 👉 Sự kiện click chuột để lấy chi tiết theo mã
        tblNuocUong.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblNuocUong.getSelectedRow();
                if (row >= 0) {
                    LoaiNuocUong selected = nuocUongList.get(row);
                    fillChiTietNuocUongTheoMa(selected.getMaLoaiNuoc());
                }
            }
        });
    }

    private void fillChiTietNuocUongTheoMa(int maLoaiNuocUong) {
        DonGiaNuocUongx.clear();
        MaNuocUongx.clear();
        DefaultTableModel model = (DefaultTableModel) tblBangChiTietMon.getModel();
        model.setRowCount(0); // Xoá dữ liệu cũ
        NuocUongDAO dao = new NuocUongDAOImpl();
        List<NuocUong> list = dao.findByLoaiNuocUongId(maLoaiNuocUong);

        for (NuocUong ct : list) {
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
                System.out.println("Lỗi load ảnh cho nước uống " + ct.getTenNuocUong() + ": " + e.getMessage());
            }
            DonGiaNuocUongx.add(ct.getGiaBan());
            MaNuocUongx.add(ct.getMaNuocUong());
            Object[] row = {
                ct.getMaNuocUong(),
                icon,
                ct.getTenNuocUong(),
                ct.getGiaBan(),
                1, // Số lượng mặc định
                "Nước uống" // Loại món
            };
            model.addRow(row);
        }

        tblBangChiTietMon.setRowHeight(100);
        if (tblBangChiTietMon.getColumnCount() >= 4) {
            tblBangChiTietMon.getColumnModel().getColumn(4).setCellEditor(new SpinnerEditor());
        }

        // Ẩn cột mã
        tblBangChiTietMon.getColumnModel().getColumn(0).setMinWidth(0);
        tblBangChiTietMon.getColumnModel().getColumn(0).setMaxWidth(0);
        tblBangChiTietMon.getColumnModel().getColumn(0).setWidth(0);

        // Renderer ảnh
        tblBangChiTietMon.getColumnModel().getColumn(1).setCellRenderer(new TableCellRenderer() {
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

        // Renderer text căn giữa các cột còn lại
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 2; i < tblBangChiTietMon.getColumnCount(); i++) {
            tblBangChiTietMon.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
private void themMonVaoBangDaChon() {
    int selectedRow = tblBangChiTietMon.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn trước khi thêm!");
        return;
    }

    int maMon = 0;
    int maNuoc = 0;
    Object tenLoaiObj = tblBangChiTietMon.getValueAt(selectedRow, 5); // "Món ăn" hoặc "Nước uống"
    Object maObj = tblBangChiTietMon.getValueAt(selectedRow, 0); // mã món hoặc nước

    if (maObj != null && tenLoaiObj != null) {
        String tenLoai = tenLoaiObj.toString().trim();
        int maInt = Integer.parseInt(maObj.toString());
        if (tenLoai.equalsIgnoreCase("Món ăn")) {
            maMon = maInt;
        } else if (tenLoai.equalsIgnoreCase("Nước uống")) {
            maNuoc = maInt;
        }
    }

    String tenMon = tblBangChiTietMon.getValueAt(selectedRow, 2).toString();
    double donGia = 0;
    Object giaObj = tblBangChiTietMon.getValueAt(selectedRow, 3);
    if (giaObj != null) {
        try {
            donGia = Double.parseDouble(giaObj.toString().replace(",", ""));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá không hợp lệ!");
            return;
        }
    }

    int soLuong = 1;
    Object soLuongObj = tblBangChiTietMon.getValueAt(selectedRow, 4);
    if (soLuongObj != null) {
        try {
            soLuong = Integer.parseInt(soLuongObj.toString());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!");
            return;
        }
    }

    if (soLuong <= 0) {
        JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!");
        return;
    }

    // ===== Kiểm tra tồn kho nếu là nước uống =====
    if (maNuoc != 0) {
        TonKhoDAO tonKhoDAO = new TonKhoDAOImpl();
        TonKho tonKho = tonKhoDAO.findByMaNuocUong(maNuoc);
        if (tonKho == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tồn kho của nước uống: " + tenMon);
            return;
        }

        int tonKhoHienTai = tonKho.getSoLuongTong();
        int tongSoLuongDaChon = soLuong;

        // Tính tổng số lượng đã có trong bảng DaChon (nếu có món này rồi)
        DefaultTableModel model = (DefaultTableModel) tblDaChon.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            Object maItem = model.getValueAt(i, 0);
            Object loaiItem = model.getValueAt(i, 5);
            if (maItem != null && loaiItem != null
                    && loaiItem.toString().equalsIgnoreCase("Nước uống")
                    && Integer.parseInt(maItem.toString()) == maNuoc) {
                tongSoLuongDaChon += Integer.parseInt(model.getValueAt(i, 2).toString());
            }
        }

        // Kiểm tra tồn kho tổng
        if (tongSoLuongDaChon > tonKhoHienTai) {
            JOptionPane.showMessageDialog(this,
                    "Số lượng nước uống \"" + tenMon + "\" vượt quá tồn kho!\n"
                    + "Tồn kho: " + tonKhoHienTai + "\n"
                    + "Đã chọn trước: " + (tongSoLuongDaChon - soLuong) + "\n"
                    + "Bạn định đặt thêm: " + soLuong + "\n"
                    + "Tổng cộng: " + tongSoLuongDaChon + "\n"
                    + "Vượt quá: " + (tongSoLuongDaChon - tonKhoHienTai));
            return;
        }
    }

    // ===== Kiểm tra món đã tồn tại trong tblDaChon thì cộng dồn =====
    DefaultTableModel model = (DefaultTableModel) tblDaChon.getModel();
    boolean daTonTai = false;
    for (int i = 0; i < model.getRowCount(); i++) {
        Object maItem = model.getValueAt(i, 0);
        Object loaiItem = model.getValueAt(i, 5);
        if (maItem != null && loaiItem != null
                && maItem.toString().equals(maObj.toString())
                && loaiItem.toString().equalsIgnoreCase(tenLoaiObj.toString())) {

            int soLuongCu = Integer.parseInt(model.getValueAt(i, 2).toString());
            int soLuongMoi = soLuongCu + soLuong;
            model.setValueAt(soLuongMoi, i, 2); // cập nhật số lượng mới
            model.setValueAt(soLuongMoi * donGia, i, 4); // cập nhật thành tiền mới
            daTonTai = true;
            break;
        }
    }

    if (!daTonTai) {
        double thanhTien = donGia * soLuong;
        Object[] row = {
            (maMon != 0) ? maMon : maNuoc,
            tenMon,
            soLuong,
            donGia,
            thanhTien,
            tenLoaiObj
        };
        model.addRow(row);
    }

    System.out.println(">> Đã thêm: " + tenMon + " | SL: " + soLuong + " | Giá: " + donGia);
}


    private void datMon() {
        try {
            HoaDonDAO hoaDonDAO = new HoaDonDAOImpl();
            HoaDonChiTietDAO chiTietDAO = new HoaDonChiTietDAOImpl();
            TonKhoDAO tonKhoDAO = new TonKhoDAOImpl();
            XuatKhoNuocUongDAO xuatKhoNuocUongDAO = new XuatKhoNuocUongDAOImpl();

            if (Auth.nhanVienDangNhap == null) {
                JOptionPane.showMessageDialog(this, "Không có nhân viên đăng nhập.");
                return;
            }

            DefaultTableModel modelDaChon = (DefaultTableModel) tblDaChon.getModel();
            if (modelDaChon.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Bạn chưa chọn món nào để đặt.");
                return;
            }

            if (hoaDonHienTai == null || hoaDonHienTai.getMaHD() == 0) {
                HoaDon hd = new HoaDon();
                hd.setMaBan(soBan);
                hd.setMaNV(Auth.nhanVienDangNhap.getMaNV());
                hd.setNgayLap(new Date());
                hd.setTrangThai("Chưa thanh toán");

                int maHD = hoaDonDAO.insertReturnId(hd);
                if (maHD <= 0) {
                    JOptionPane.showMessageDialog(this, "Không thể tạo hóa đơn mới.");
                    return;
                }
                hoaDonHienTai = hoaDonDAO.findById(maHD);
                System.out.println("Tạo hóa đơn mới thành công, mã HD: " + maHD);
            }

            List<HoaDonChiTiet> chiTietHienTai = chiTietDAO.findByHoaDonId(hoaDonHienTai.getMaHD());

            for (int i = 0; i < modelDaChon.getRowCount(); i++) {
                try {
                    Object maObj = modelDaChon.getValueAt(i, 0);
                    String tenMon = modelDaChon.getValueAt(i, 1).toString();
                    int soLuongMoi = ((Number) modelDaChon.getValueAt(i, 2)).intValue();
                    double donGia = Double.parseDouble(modelDaChon.getValueAt(i, 3).toString().replace(",", ""));
                    String loai = modelDaChon.getValueAt(i, 5).toString();

                    Integer maChiTiet = null;
                    Integer maNuocUong = null;

                    if ("Món ăn".equalsIgnoreCase(loai)) {
                        maChiTiet = Integer.parseInt(maObj.toString());
                    } else if ("Nước uống".equalsIgnoreCase(loai)) {
                        maNuocUong = Integer.parseInt(maObj.toString());
                    }

                    boolean daTonTai = false;

                    for (HoaDonChiTiet ctDaCo : chiTietHienTai) {
                        boolean trungMon = (maChiTiet != null && maChiTiet.equals(ctDaCo.getMaChiTiet()))
                                || (maNuocUong != null && maNuocUong.equals(ctDaCo.getMaNuocUong()));

                        if (trungMon) {
                            int tongSoLuong = ctDaCo.getSoLuong() + soLuongMoi;
                            ctDaCo.setSoLuong(tongSoLuong);

                            int rowAffected = chiTietDAO.updateSoLuong(ctDaCo);
                            if (rowAffected == 0) {
                                System.err.println("❌ Không cập nhật được chi tiết hóa đơn.");
                            } else {
                                System.out.println("✅ Cập nhật số lượng: " + tenMon + ", Tổng SL: " + tongSoLuong);
                            }

                            daTonTai = true;
                            break;
                        }
                    }

                    if (!daTonTai) {
                        HoaDonChiTiet ctMoi = new HoaDonChiTiet();
                        ctMoi.setMaHD(hoaDonHienTai.getMaHD());
                        ctMoi.setTenMon(tenMon);
                        ctMoi.setSoLuong(soLuongMoi);
                        ctMoi.setDonGia(donGia);
                        if (maChiTiet != null) {
                            ctMoi.setMaChiTiet(maChiTiet);
                        }
                        if (maNuocUong != null) {
                            ctMoi.setMaNuocUong(maNuocUong);
                        }

                        chiTietDAO.insert(ctMoi);
                        chiTietHienTai.add(ctMoi);
                        System.out.println("➕ Thêm món mới: " + tenMon + ", SL: " + soLuongMoi);
                    }

                    // Nếu là nước uống thì trừ tồn kho và ghi xuất kho
                    if ("Nước uống".equalsIgnoreCase(loai) && maNuocUong != null) {
                        TonKho tonKho = tonKhoDAO.findByMaNuocUong(maNuocUong);
                        if (tonKho != null) {
                            int tonHienTai = tonKho.getSoLuongTong();
                            if (soLuongMoi > tonHienTai) {
                                JOptionPane.showMessageDialog(this,
                                        "❗ Số lượng nước uống '" + tenMon + "' vượt quá tồn kho (" + tonHienTai + ").");
                                continue; // bỏ qua lần đặt này
                            }

                            tonKho.setSoLuongTong(tonHienTai - soLuongMoi);
                            tonKhoDAO.update(tonKho);
                            System.out.println("📦 Đã trừ tồn kho: " + tenMon + ", còn lại: " + (tonHienTai - soLuongMoi));
                        } else {
                            System.err.println("❌ Không tìm thấy tồn kho nước uống: " + tenMon);
                        }

                        // Xuất kho
                        XuatKhoNuocUong xk = xuatKhoNuocUongDAO.findByMaNuocUongAndMaHD(maNuocUong, hoaDonHienTai.getMaHD());
                        if (xk != null) {
                            xk.setSoLuong(xk.getSoLuong() + soLuongMoi);
                            xuatKhoNuocUongDAO.update(xk);
                            System.out.println("📝 Cập nhật SL xuất kho: " + tenMon);
                        } else {
                            XuatKhoNuocUong xkMoi = new XuatKhoNuocUong();
                            xkMoi.setMaNuocUong(maNuocUong);
                            xkMoi.setTenNuocUong(tenMon);
                            xkMoi.setSoLuong(soLuongMoi);
                            xkMoi.setNgayXuat(new java.sql.Date(System.currentTimeMillis()));
                            xkMoi.setMaHD(hoaDonHienTai.getMaHD());
                            xuatKhoNuocUongDAO.insert(xkMoi);
                            System.out.println("🆕 Xuất kho mới: " + tenMon);
                        }
                    }

                } catch (Exception e) {
                    System.err.println("❌ Lỗi xử lý món: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            loadChiTietMonAn(hoaDonHienTai.getMaHD());
            parent.capNhatTrangThaiBan(soBan);
            modelDaChon.setRowCount(0);
            capNhatTongTienHoaDon();
            JOptionPane.showMessageDialog(this, "✅ Đặt món thành công!");
        } catch (Exception ex) {
            System.err.println("❌ LỖI toàn hàm datMon: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi đặt món: " + ex.getMessage());
        }
    }


    private void thanhToan() {
    int maBan = soBan;

    try {
        HoaDon hoaDon = hoaDonDAO.findChuaThanhToanTheoBan(maBan);
        if (hoaDon == null) {
            JOptionPane.showMessageDialog(this, "❌ Không tìm thấy hóa đơn cần thanh toán cho bàn " + maBan);
            return;
        }

        List<HoaDonChiTiet> chiTietList = hoaDonDAO.findByHoaDonId(hoaDon.getMaHD());
        if (chiTietList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ Không có món nào để thanh toán.");
            return;
        }

        double tongTien = 0;
        DecimalFormat df = new DecimalFormat("#,###");

        for (HoaDonChiTiet ct : chiTietList) {
            tongTien += ct.getSoLuong() * ct.getDonGia();
        }

        // Gán thông tin hóa đơn
        hoaDon.setTenNV(Auth.nhanVienDangNhap.getTenNV());
        hoaDon.setTongTien(tongTien);
        hoaDon.setTrangThai("Đã thanh toán");
        hoaDon.setMaNV(Auth.nhanVienDangNhap.getMaNV());

        boolean updateTien = hoaDonDAO.updateTongTien(hoaDon);
        boolean updateTrangThai = hoaDonDAO.updateTrangThai(hoaDon);
        boolean updateNhanVien = hoaDonDAO.updateNhanVienThanhToan(hoaDon);

        if (updateTien && updateTrangThai && updateNhanVien) {
            JOptionPane.showMessageDialog(this, "✅ Thanh toán thành công. Tổng tiền: " + df.format(tongTien) + " VNĐ");
            lamMoiBangMonAn();
            resetTongTien();
            parent.capNhatToanBoBanAn();
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Thanh toán thất bại (không cập nhật được hóa đơn)!");
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "❌ Lỗi nghiêm trọng trong quá trình thanh toán: " + e.getMessage());
    }
}


    private void capNhatTongTienHoaDon() {
        DefaultTableModel model = (DefaultTableModel) tblHoaDon.getModel();
        int ma = Integer.parseInt(txtMaHD.getText());
        double tongTien = 0;
        DecimalFormat df = new DecimalFormat("#,###");
        HoaDonDAO hd = new HoaDonDAOImpl();
        HoaDon hdd = hd.findById(ma);
        double tongtien = hdd.getTongTien();
       
//        for (int i = 0; i < model.getRowCount(); i++) {
//            Object ttObj = model.getValueAt(i, 5); // Thành tiền
//            if (ttObj instanceof Number) {
//                tongTien += ((Number) ttObj).doubleValue();
//            }
//        }
        lblTongTien.setText("TỔNG TIỀN: " + df.format(tongtien) + " VNĐ");
    }

    private void lamMoiBangMonAn() {
        DefaultTableModel model = (DefaultTableModel) tblDaChon.getModel();
        model.setRowCount(0);
    }

    private void resetTongTien() {
        lblTongTien.setText("0 VNĐ");
    }
//======= hàm xóa hóa đơn rác ======

    private void xoaHoaDonNeuKhongCoMon() {
        if (hoaDonHienTai != null) {
            List<HoaDonChiTiet> chiTietList = chiTietDAO.findByHoaDonId(hoaDonHienTai.getMaHD());
            if (chiTietList.isEmpty()) {
                System.out.println("🗑 Xóa hóa đơn rác MaHD = " + hoaDonHienTai.getMaHD());

                chiTietDAO.deleteByHoaDonId(hoaDonHienTai.getMaHD()); // Phòng trường hợp có dữ liệu dư
                hoaDonDAO.deleteById(hoaDonHienTai.getMaHD());        // Xóa hóa đơn
                parent.capNhatToanBoBanAn(); // Cập nhật lại màu bàn
            }
        }
    }
    // nút xóa món tạm 

    private void HuyMonDaChon() {
        int selectedRow = tblDaChon.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món muốn xóa khỏi danh sách đã chọn!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa món này không?", "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DefaultTableModel model = (DefaultTableModel) tblDaChon.getModel();
            model.removeRow(selectedRow);
        }
    }
//======================BẢNG HÓA ĐƠN =====================

    private void hienThiHoaDon(HoaDon hd) {
        txtMaHD.setText(String.valueOf(hd.getMaHD()));
        txtNgayLap.setText(hd.getNgayLap().toString());

        // ✅ Luôn đặt trạng thái là "Chưa thanh toán" khi hiển thị
        txtTrangThai.setText(hd.getTrangThai());
        loadChiTietMonAn(hd.getMaHD());
        hienthitenNV();
    }

    private void hienthitenNV() {
        if (Auth.nhanVienDangNhap != null) {
            txtHoTenNV.setText(Auth.nhanVienDangNhap.getTenNV());
        } else {
            txtHoTenNV.setText("Chưa đăng nhập");
        }
    }

    private void taoHoaDonMoi() {
        HoaDon hd = new HoaDon();
        hd.setMaBan(soBan);
        hd.setMaNV(Auth.nhanVienDangNhap.getMaNV()); // lấy đúng người đang login
        hd.setNgayLap(new Date());
        hd.setTrangThai("Chưa thanh toán"); //  BẮT BUỘC
        int maHD = hoaDonDAO.insertReturnId(hd);

        if (maHD > 0) {
            hoaDonHienTai = hoaDonDAO.findById(maHD);

            // ✅ Cập nhật màu bàn sau khi tạo hóa đơn
            parent.capNhatToanBoBanAn();

            hienThiHoaDon(hoaDonHienTai);
        } else {
            JOptionPane.showMessageDialog(this, "Không thể tạo hóa đơn mới.");
            dispose();
        }
    }

    public void setHoaDonHienTai(HoaDon hd) {
        this.hoaDonHienTai = hd;
        hienThiHoaDon(hd); // load lại dữ liệu nếu có
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblSoBan = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblBangLoaiMon = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBangChiTietMon = new javax.swing.JTable();
        lblTongTien = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblHoaDon = new javax.swing.JTable();
        btnThanhToan = new javax.swing.JButton();
        btnDat = new javax.swing.JButton();
        btnHuy = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDaChon = new javax.swing.JTable();
        btnThem = new javax.swing.JButton();
        txtHoTenNV = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtTrangThai = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtNgayLap = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtMaHD = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblNuocUong = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblSoBan.setText("Số bàn");
        getContentPane().add(lblSoBan, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 640, 190, 20));

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

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(-1, 0, 370, 230));

        tblBangChiTietMon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "", "Hình Ảnh", "Tên Món Ăn", "Đơn Giá VND", "Số lượng", "Loại"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false
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
            tblBangChiTietMon.getColumnModel().getColumn(2).setMinWidth(300);
            tblBangChiTietMon.getColumnModel().getColumn(2).setMaxWidth(300);
            tblBangChiTietMon.getColumnModel().getColumn(4).setMinWidth(80);
            tblBangChiTietMon.getColumnModel().getColumn(4).setMaxWidth(80);
        }

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 0, 790, 400));

        lblTongTien.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTongTien.setText("TỔNG TIỀN");
        getContentPane().add(lblTongTien, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 640, -1, -1));

        tblHoaDon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã Món", "Mã Nước", "Tên Món Ăn", "Số Lượng", "Đơn Giá VND", "Thành Tiền"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblHoaDon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblHoaDonMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tblHoaDon);
        if (tblHoaDon.getColumnModel().getColumnCount() > 0) {
            tblHoaDon.getColumnModel().getColumn(5).setPreferredWidth(100);
        }

        getContentPane().add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 450, 680, 180));

        btnThanhToan.setBackground(new java.awt.Color(204, 204, 204));
        btnThanhToan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnThanhToan.setForeground(new java.awt.Color(0, 0, 0));
        btnThanhToan.setText("Thanh Toán");
        btnThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThanhToanActionPerformed(evt);
            }
        });
        getContentPane().add(btnThanhToan, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 640, 180, -1));

        btnDat.setBackground(new java.awt.Color(204, 204, 204));
        btnDat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnDat.setForeground(new java.awt.Color(0, 0, 0));
        btnDat.setText("Đặt Món");
        btnDat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDatActionPerformed(evt);
            }
        });
        getContentPane().add(btnDat, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 640, 210, -1));

        btnHuy.setBackground(new java.awt.Color(204, 204, 204));
        btnHuy.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnHuy.setForeground(new java.awt.Color(0, 0, 0));
        btnHuy.setText("Hủy Món ");
        btnHuy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHuyActionPerformed(evt);
            }
        });
        getContentPane().add(btnHuy, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 640, 200, -1));

        tblDaChon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Tên ", "Số Lượng", "Đơn Gía", "Thành Tiền", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class
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
        jScrollPane1.setViewportView(tblDaChon);
        if (tblDaChon.getColumnModel().getColumnCount() > 0) {
            tblDaChon.getColumnModel().getColumn(2).setMinWidth(60);
            tblDaChon.getColumnModel().getColumn(2).setMaxWidth(60);
        }

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 450, 470, 180));

        btnThem.setBackground(new java.awt.Color(204, 204, 204));
        btnThem.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnThem.setForeground(new java.awt.Color(0, 0, 0));
        btnThem.setText("Thêm Món Ăn");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });
        getContentPane().add(btnThem, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 410, 140, 30));
        getContentPane().add(txtHoTenNV, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 420, 140, -1));

        jLabel6.setText("Tên Nhân Viên");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 400, -1, 20));
        getContentPane().add(txtTrangThai, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 420, 140, -1));

        jLabel4.setText("Trạng thái");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 400, -1, 20));
        getContentPane().add(txtNgayLap, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 420, 140, -1));

        jLabel3.setText("Ngày lập");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 400, -1, 20));

        txtMaHD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaHDActionPerformed(evt);
            }
        });
        getContentPane().add(txtMaHD, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 420, 90, -1));

        jLabel1.setText("Mã HĐ");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 400, -1, 20));

        tblNuocUong.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Mã", "Hình ảnh", "Tên loại đồ uống"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane6.setViewportView(tblNuocUong);

        getContentPane().add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 370, 220));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtMaHDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaHDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaHDActionPerformed

    private void tblBangChiTietMonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBangChiTietMonMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblBangChiTietMonMouseClicked

    private void btnThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThanhToanActionPerformed
        thanhToan();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnThanhToanActionPerformed

    private void tblHoaDonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblHoaDonMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblHoaDonMouseClicked

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        themMonVaoBangDaChon();   // TODO add your handling code here:
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnHuyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHuyActionPerformed
        HuyMonDaChon();  // TODO add your handling code here:
    }//GEN-LAST:event_btnHuyActionPerformed

    private void btnDatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDatActionPerformed
        datMon();  // TODO add your handling code here:
    }//GEN-LAST:event_btnDatActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDat;
    private javax.swing.JButton btnHuy;
    private javax.swing.JButton btnThanhToan;
    private javax.swing.JButton btnThem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JLabel lblSoBan;
    private javax.swing.JLabel lblTongTien;
    private javax.swing.JTable tblBangChiTietMon;
    private javax.swing.JTable tblBangLoaiMon;
    private javax.swing.JTable tblDaChon;
    private javax.swing.JTable tblHoaDon;
    private javax.swing.JTable tblNuocUong;
    private javax.swing.JTextField txtHoTenNV;
    private javax.swing.JTextField txtMaHD;
    private javax.swing.JTextField txtNgayLap;
    private javax.swing.JTextField txtTrangThai;
    // End of variables declaration//GEN-END:variables
}

class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {

    private final JSpinner spinner;
    private final TonKhoDAO tonKhoDAO = new TonKhoDAOImpl();
    private int tonKhoMax = 100;
public SpinnerEditor() {
    spinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    spinner.setFont(new Font("Arial", Font.PLAIN, 16));

    spinner.addChangeListener(e -> {
        int value = (int) spinner.getValue();
        if (value > tonKhoMax && tonKhoMax != Integer.MAX_VALUE) {
            JOptionPane.showMessageDialog(null,
                    "⚠️ Số lượng vượt quá tồn kho! Trong kho còn: " + tonKhoMax,
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);

            // Nếu tồn kho còn > 0 thì set về tồn kho, còn nếu <= 0 thì set về 1
            if (tonKhoMax > 0) {
                spinner.setValue(tonKhoMax);
            } else {
                spinner.setValue(1);
            }
        }
    });
}

    @Override
    public Object getCellEditorValue() {
        return spinner.getValue();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        try {
            int maMon = (int) table.getValueAt(row, 0); // Cột 0: mã món
            String loai = table.getValueAt(row, 5).toString(); // Cột 5: loại ("Món ăn" hoặc "Nước uống")
            int currentValue = (value instanceof Integer) ? (int) value : 1;

            if ("Nước uống".equalsIgnoreCase(loai)) {
                TonKho tonKho = tonKhoDAO.findByMaNuocUong(maMon);
                tonKhoMax = (tonKho != null) ? tonKho.getSoLuongTong() : 1;

                spinner.setModel(new SpinnerNumberModel(currentValue, 1, tonKhoMax, 1));
            } else {
                // Món ăn hoặc bất kỳ loại nào khác không giới hạn
                tonKhoMax = Integer.MAX_VALUE;
                spinner.setModel(new SpinnerNumberModel(currentValue, 1, Integer.MAX_VALUE, 1));
            }

            spinner.setValue(currentValue);

        } catch (Exception ex) {
            tonKhoMax = 100;
            spinner.setModel(new SpinnerNumberModel(1, 1, tonKhoMax, 1));
            spinner.setValue(1);
        }

        return spinner;
    }
}


class SpinnerEditorGiamSoLuong extends AbstractCellEditor implements TableCellEditor {

    private final JSpinner spinner;
    private JTable table;
    private GiamSoLuongCallback callback;
    private int row;

    public interface GiamSoLuongCallback {

        void onSoLuongThayDoi(int row, int soLuongMoi);
    }

    public SpinnerEditorGiamSoLuong(JTable table, GiamSoLuongCallback callback) {
        this.table = table;
        this.callback = callback;
        spinner = new JSpinner();
        spinner.setFont(new Font("Arial", Font.PLAIN, 16));
    }

    @Override
    public Object getCellEditorValue() {
        int value = (int) spinner.getValue();
        if (callback != null) {
            callback.onSoLuongThayDoi(row, value);
        }
        return value;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row;
        int currentValue = (value instanceof Integer) ? (int) value : 1;

        SpinnerNumberModel model = new SpinnerNumberModel(currentValue, 1, currentValue, 1);
        spinner.setModel(model);

        // Disable nút tăng
        disableIncreaseButton(spinner);
        return spinner;
    }

    private void disableIncreaseButton(JSpinner spinner) {
        for (Component comp : spinner.getComponents()) {
            if (comp instanceof JPanel) {
                for (Component sub : ((JPanel) comp).getComponents()) {
                    if (sub instanceof JButton) {
                        JButton btn = (JButton) sub;
                        if ("Spinner.nextButton".equals(btn.getName())) {
                            btn.setEnabled(false);
                            btn.setVisible(false);
                        }
                    }
                }
            }
        }
    }
}

//    public SpinnerEditor(JTable tblBangChiTietMon) {
//        this.tblBangChiTietMon = tblBangChiTietMon;
//
//        spinner = new JSpinner();
//        spinner.setFont(new Font("Arial", Font.PLAIN, 16));
//
//        // Không giới hạn max trong SpinnerNumberModel
//        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
//        spinner.setModel(model);
//
//        // Lắng nghe thay đổi
//        spinner.addChangeListener(e -> {
//            int value = (int) spinner.getValue();
//            if (value > tonKhoMax) {
//                JOptionPane.showMessageDialog(null,
//                        "Số lượng vượt quá tồn kho! Trong Kho Còn: " + tonKhoMax,
//                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
//                spinner.setValue(tonKhoMax); // Gán lại về tồn kho
//            }
//        });
//    }
//class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
//    private final JSpinner spinner;
//    private final JTable tblBangChiTietMon; // truyền từ ngoài vào
//    private final TonKhoDAO tonKhoDAO = new TonKhoDAOImpl();
//    private int tonKhoMax = 100;
//
//    public SpinnerEditor(JTable tblBangChiTietMon) {
//        this.tblBangChiTietMon = tblBangChiTietMon;
//
//        spinner = new JSpinner();
//        spinner.setFont(new Font("Arial", Font.PLAIN, 16));
//
//        // Lắng nghe người dùng thay đổi số lượng
//        spinner.addChangeListener(e -> {
//            int value = (int) spinner.getValue();
//            if (value > tonKhoMax) {
//                JOptionPane.showMessageDialog(null, "Số lượng vượt quá tồn kho! Tối đa: " + tonKhoMax);
//                spinner.setValue(tonKhoMax);
//            }
//        });
//    }
//
//    @Override
//    public Object getCellEditorValue() {
//        return spinner.getValue();
//    }
//
//    @Override
//    public Component getTableCellEditorComponent(JTable table, Object value,
//            boolean isSelected, int row, int column) {
//
//        try {
//            // Lấy mã nước uống từ cột 0 của bảng
//            int maNuocUong = (int) tblBangChiTietMon.getValueAt(row, 0);
//
//            // Tìm tồn kho theo mã nước uống
//            TonKho tonKho = tonKhoDAO.findByMaNuocUong(maNuocUong);
//            tonKhoMax = (tonKho != null) ? tonKho.getSoLuongTong() : 1;
//
//            // Lấy giá trị hiện tại nếu có
//            int currentValue = (value instanceof Integer) ? (int) value : 1;
//
//            // Nếu vượt tồn kho thì cảnh báo
//            if (currentValue > tonKhoMax) {
//                JOptionPane.showMessageDialog(null, "Số lượng vượt quá tồn kho! Tối đa: " + tonKhoMax);
//                currentValue = tonKhoMax;
//            }
//
//            // Set spinner model theo tồn kho
//            SpinnerNumberModel model = new SpinnerNumberModel(currentValue, 1, tonKhoMax, 1);
//            spinner.setModel(model);
//
//        } catch (Exception ex) {
//            // fallback nếu lỗi
//            tonKhoMax = 100;
//            spinner.setModel(new SpinnerNumberModel(1, 1, tonKhoMax, 1));
//        }
//
//        return spinner;
//    }
//}

