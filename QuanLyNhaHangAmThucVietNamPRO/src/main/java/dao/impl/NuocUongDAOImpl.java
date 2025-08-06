/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.impl;

import dao.LoaiNuocUongDAO;
import dao.NuocUongDAO;
import entity.LoaiNuocUong;
import entity.NuocUong;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.XJdbc;

/**
 *
 * @author ACER
 */
public class NuocUongDAOImpl implements NuocUongDAO {

    @Override
    public List<NuocUong> findAll() {
        String sql = "SELECT * FROM NUOCUONG";
        List<NuocUong> list = new ArrayList<>();
        try (Connection con = XJdbc.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NuocUong nuoc = new NuocUong();
                nuoc.setMaNuocUong(rs.getInt("MaNuocUong"));
                nuoc.setTenNuocUong(rs.getString("TenNuocUong"));
                nuoc.setGiaBan(rs.getDouble("GiaBan"));
                nuoc.setHinhAnh(rs.getString("HinhAnh"));
                nuoc.setDonViTinh(rs.getString("DonViTinh"));
                // Load loại nước uống
                LoaiNuocUongDAO loaiDAO = new LoaiNuocUongDAOImpl();
                LoaiNuocUong loai = loaiDAO.findById(rs.getInt("MaLoaiNuoc"));
                nuoc.setLoaiNuocUong(loai);
                list.add(nuoc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<NuocUong> findByLoaiNuocUongId(int maLoaiNuocUong) {
        List<NuocUong> list = new ArrayList<>();
        String sql = "SELECT * FROM NUOCUONG WHERE MaLoaiNuoc = ?";

        try (Connection con = XJdbc.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maLoaiNuocUong);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NuocUong nuoc = new NuocUong();
                    nuoc.setMaNuocUong(rs.getInt("MaNuocUong"));
                    nuoc.setTenNuocUong(rs.getString("TenNuocUong"));
                    nuoc.setGiaBan(rs.getDouble("GiaBan"));
                    nuoc.setHinhAnh(rs.getString("HinhAnh"));
                    nuoc.setDonViTinh(rs.getString("DonViTinh"));
                    LoaiNuocUongDAO loaiDAO = new LoaiNuocUongDAOImpl();
                    LoaiNuocUong loai = loaiDAO.findById(maLoaiNuocUong);
                    nuoc.setLoaiNuocUong(loai);

                    list.add(nuoc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void insert(NuocUong nuoc) {
        String sql = "INSERT INTO NUOCUONG (MaLoaiNuoc, TenNuocUong, GiaBan, HinhAnh) VALUES (?, ?, ?, ?)";
        try (Connection con = XJdbc.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuoc.getLoaiNuocUong().getMaLoaiNuoc());
            ps.setString(2, nuoc.getTenNuocUong());
            ps.setDouble(3, nuoc.getGiaBan());
            ps.setString(4, nuoc.getHinhAnh());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(NuocUong nuoc) {
        String sql = "UPDATE NUOCUONG SET MaLoaiNuoc = ?, TenNuocUong = ?, GiaBan = ?, HinhAnh = ?, DonViTinh = ?, WHERE MaNuocUong = ?";
        try (Connection con = XJdbc.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuoc.getLoaiNuocUong().getMaLoaiNuoc());
            ps.setString(2, nuoc.getTenNuocUong());
            ps.setDouble(3, nuoc.getGiaBan());
            ps.setString(4, nuoc.getHinhAnh());
            ps.setString(5, nuoc.getDonViTinh());
            ps.setInt(6, nuoc.getMaNuocUong());
            
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM NUOCUONG WHERE MaNuocUong = ?";
        try (Connection con = XJdbc.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NuocUong create(NuocUong nuoc) {
        String sql = "INSERT INTO NuocUong (TenNuocUong, GiaBan,HinhAnh, MaNuocUong) VALUES (?, ?, ?, ?)";
        try (Connection conn = XJdbc.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuoc.getTenNuocUong());
            ps.setDouble(2, nuoc.getGiaBan());
            ps.setString(3, nuoc.getHinhAnh());

            ps.setInt(4, nuoc.getMaNuocUong());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nuoc;
    }
    public NuocUong findByTenNuoc(String tenNuoc) {
        String sql = "SELECT * FROM NuocUong WHERE TenNuocUong = ?";
        try (Connection con = XJdbc.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tenNuoc);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                NuocUong nuoc = new NuocUong();
                nuoc.setMaNuocUong(rs.getInt("MaNuocUong"));
                nuoc.setTenNuocUong(rs.getString("TenNuocUong"));
                nuoc.setGiaBan(rs.getDouble("GiaBan"));
                // Gán thêm nếu có hình ảnh, loại, mô tả...
                return nuoc;
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi tìm nước uống theo tên: " + e.getMessage());
        }
        return null;
    }
    private final String FIND_BY_TENMON_SQL = "SELECT * FROM NUOCUONG WHERE TenNuocUong = ?";

    @Override
    public NuocUong findByTenMon(String tenMon) {
        NuocUong nuocUong = null;

        try (
                Connection con = XJdbc.getConnection(); PreparedStatement ps = con.prepareStatement(FIND_BY_TENMON_SQL)) {
            // Gán tên món vào truy vấn
            ps.setString(1, tenMon);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Nếu tìm thấy món, tạo đối tượng NuocUong và gán dữ liệu từ ResultSet
                nuocUong = new NuocUong();
                nuocUong.setMaNuocUong(rs.getInt("MaNuocUong"));
                nuocUong.setTenNuocUong(rs.getString("TenNuocUong"));
                nuocUong.setGiaBan(rs.getDouble("GiaBan"));
                nuocUong.setHinhAnh(rs.getString("HinhAnh"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm kiếm nước uống theo tên món: " + e.getMessage(), e);
        }

        return nuocUong;

    }

    @Override
    public void truSoLuongTon(int maNuocUong, int soLuongTru) {
        String sql = "UPDATE TONKHO SET SoLuongTong = SoLuongTong - ? WHERE MaNuocUong = ?";
        try (
                Connection conn = XJdbc.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soLuongTru);
            ps.setInt(2, maNuocUong);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//public void truSoLuongTon(int maNuocUong, int soLuongTru) {
//    String selectSQL = "SELECT * FROM KHO_NUOCUONG WHERE MaNuocUong = ? AND SoLuong > 0 ORDER BY NgayNhap ASC";
//    String updateSQL = "UPDATE KHO_NUOCUONG SET SoLuong = ? WHERE MaNuocUong = ? AND NgayNhap = ?";
//    
//    try (Connection conn = XJdbc.getConnection();
//         PreparedStatement psSelect = conn.prepareStatement(selectSQL);
//         PreparedStatement psUpdate = conn.prepareStatement(updateSQL)) {
//        
//        conn.setAutoCommit(false);  // dùng transaction
//        
//        psSelect.setInt(1, maNuocUong);
//        ResultSet rs = psSelect.executeQuery();
//        
//        while (rs.next() && soLuongTru > 0) {
//            int soLuongHienTai = rs.getInt("SoLuong");
//            Date ngayNhap = rs.getDate("NgayNhap");
//            int soLuongCapNhat = Math.max(0, soLuongHienTai - soLuongTru);
//            int daTru = Math.min(soLuongHienTai, soLuongTru);
//            soLuongTru -= daTru;
//            
//            psUpdate.setInt(1, soLuongCapNhat);
//            psUpdate.setInt(2, maNuocUong);
//            psUpdate.setDate(3, ngayNhap);
//            psUpdate.executeUpdate();
//        }
//        
//        conn.commit();
//        
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//}
    @Override
    public NuocUong findById(int maNuocUong) {
        String sql = "SELECT * FROM NUOCUONG WHERE MaNuocUong = ?";
        try (
                Connection con = XJdbc.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maNuocUong);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    NuocUong nuoc = new NuocUong();
                    nuoc.setMaNuocUong(rs.getInt("MaNuocUong"));
                    nuoc.setTenNuocUong(rs.getString("TenNuocUong"));
                    nuoc.setGiaBan(rs.getDouble("GiaBan"));
                    nuoc.setHinhAnh(rs.getString("HinhAnh"));

                    // Load loại nước uống từ MaLoaiNuoc
                    LoaiNuocUongDAO loaiDAO = new LoaiNuocUongDAOImpl();
                    LoaiNuocUong loai = loaiDAO.findById(rs.getInt("MaLoaiNuoc"));
                    nuoc.setLoaiNuocUong(loai);

                    return nuoc;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

// Triển khai kết nối DB, join với LoaiNuocUong nếu cần

