package dao.impl;

import dao.HoaDonChiTietDAO;
import entity.HoaDonChiTiet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import util.XJdbc;

public class HoaDonChiTietDAOImpl implements HoaDonChiTietDAO {

   private final String INSERT_SQL = "INSERT INTO CT_HOADON (MaHD, MaChiTiet, TenMon, SoLuong, DonGia,MaNuocUong) VALUES (?, ?, ?, ?, ?, ?)";


@Override // null
public void insert(HoaDonChiTiet ct) {
    try (
        Connection con = XJdbc.getConnection();
        PreparedStatement ps = con.prepareStatement(INSERT_SQL)
    ) {
        ps.setInt(1, ct.getMaHD());
//        ps.setInt(2, ct.getMaChiTiet());
        if (ct.getMaChiTiet() == null) {    
            ps.setNull(2, java.sql.Types.INTEGER);
        } else {
            ps.setInt(2, ct.getMaChiTiet());
        }
        ps.setString(3, ct.getTenMon());
        ps.setInt(4, ct.getSoLuong());
        ps.setDouble(5, ct.getDonGia());

        // ✅ Kiểm tra MaNuocUong = 0 thì setNull
       if (ct.getMaNuocUong() == null) {
    ps.setNull(6, java.sql.Types.INTEGER);
} else {
    ps.setInt(6, ct.getMaNuocUong());
}


        ps.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi khi thêm CT_HOADON: " + e.getMessage(), e);
    }
}

@Override
public List<HoaDonChiTiet> findByHoaDonId(int maHD) {
    List<HoaDonChiTiet> list = new ArrayList<>();
    String sql = "SELECT * FROM CT_HOADON WHERE maHD = ?";

    try (
        Connection conn = XJdbc.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)
    ) {
        ps.setInt(1, maHD);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            HoaDonChiTiet ct = new HoaDonChiTiet();
            ct.setMaCt(rs.getInt("MaCT"));
            ct.setMaChiTiet(rs.getInt("MaChiTiet"));
            ct.setMaHD(rs.getInt("MaHD"));
            ct.setTenMon(rs.getString("TenMon"));
            ct.setSoLuong(rs.getInt("SoLuong"));
            ct.setDonGia(rs.getDouble("DonGia"));
            ct.setMaNuocUong(rs.getInt("MaNuocUong"));
                
            list.add(ct);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

@Override
public void deleteByHoaDonId(int maHD) {
    String sql = "DELETE FROM CT_HOADON WHERE MaHD = ?";
    try (
        Connection conn = XJdbc.getConnection(); 
        PreparedStatement stmt = conn.prepareStatement(sql)
    ) {
        stmt.setInt(1, maHD);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Lỗi khi xóa chi tiết hóa đơn theo MaHD: " + maHD, e);
    }
}

@Override
public int updateSoLuong(HoaDonChiTiet ct) {
    String sql = "UPDATE CT_HOADON SET SoLuong = ? WHERE MaHD = ? AND (MaChiTiet = ? OR MaNuocUong = ?)";
    try (
        Connection conn = XJdbc.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)
    ) {
        ps.setInt(1, ct.getSoLuong());
        ps.setInt(2, ct.getMaHD());
        ps.setObject(3, ct.getMaChiTiet());  // dùng setObject để tránh lỗi NullPointer nếu 1 trong 2 là null
        ps.setObject(4, ct.getMaNuocUong());
        return ps.executeUpdate(); // trả về số dòng bị ảnh hưởng
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Lỗi khi cập nhật số lượng món: " + e.getMessage(), e);
    }
}


@Override
public double tinhTongTienTheoHoaDon(int maHD) {
    String sql = "SELECT SUM(SoLuong * DonGia) AS TongTien FROM CT_HOADON WHERE MaCT = ?";
    try (
        Connection conn = XJdbc.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)
    ) {
        ps.setInt(1, maHD);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getDouble("TongTien");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Lỗi khi tính tổng tiền hóa đơn: " + e.getMessage(), e);
    }
    return 0;
}
@Override
public boolean deleteChiTietByMaHDAndMon(int maHD, int maMon, String loai) {
    String sql = loai.equalsIgnoreCase("Nước uống") ?
        "DELETE FROM HOADONCHITIET WHERE MaHD = ? AND MaNuocUong = ?" :
        "DELETE FROM HOADONCHITIET WHERE MaHD = ? AND MaChiTiet = ?";
    try (Connection conn = XJdbc.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, maHD);
        ps.setInt(2, maMon);
        return ps.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}

@Override
public boolean updateSoLuongByMon(int maHD, int maMon, int soLuong, String loai) {
    String sql = loai.equalsIgnoreCase("Nước uống") ?
        "UPDATE CT_HOADON SET SoLuong = ? WHERE MaHD = ? AND MaNuocUong = ?" :
        "UPDATE CT_HOADON SET SoLuong = ? WHERE MaHD = ? AND MaChiTiet = ?";
    try (Connection conn = XJdbc.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, soLuong);
        ps.setInt(2, maHD);
        ps.setInt(3, maMon);
        return ps.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}@Override
public void updateSoLuongTheoMaHDVaMaMon(int maHD, int maMon, int soLuongMoi) {
    String sql = "UPDATE CT_HOADON SET SoLuong = ? " +
                 "WHERE MaHD = ? AND (MaChiTiet = ? OR MaNuocUong = ?)";
    try (Connection conn = XJdbc.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, soLuongMoi);
        ps.setInt(2, maHD);
        ps.setInt(3, maMon);
        ps.setInt(4, maMon);
        

        int rows = ps.executeUpdate();
        if (rows == 0) {
            System.err.println("⚠️ Không tìm thấy món cần cập nhật với MaHD = " + maHD + ", MaMon = " + maMon);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("❌ Lỗi khi cập nhật số lượng theo MaHD và MaMon: " + e.getMessage(), e);
    }
    
}
@Override
public void updateSoLuongTheoMaHDVaMaNuoc(int maHD, int maNuoc, int soLuongMoi) {
    String sql = "UPDATE CT_HOADON SET SoLuong = ? WHERE MaNuocUong = ? AND MaHD = ?";
    XJdbc.executeUpdate(sql, soLuongMoi, maNuoc, maHD);
}
@Override
public int laySoLuongNuocTheoMaHD(int maHD, int maNuoc) {
    String sql = "SELECT SoLuong FROM CT_HOADON WHERE MaHD = ? AND MaNuocUong = ?";
    try (
        Connection conn = XJdbc.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)
    ) {
        ps.setInt(1, maHD);
        ps.setInt(2, maNuoc);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("SoLuong");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return 0;
}


}




