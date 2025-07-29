/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.impl;

/**
 *
 * @author ACER
 */
import dao.KhoNuocUongDAO;
import dao.NuocUongDAO;
import entity.KhoNuocUong;
import entity.NuocUong;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import util.XJdbc;

public class KhoNuocUongDAOImpl implements KhoNuocUongDAO {
    @Override
   public List<KhoNuocUong> findAll() {
    String sql = "SELECT * FROM KHO_NUOCUONG";
    List<KhoNuocUong> list = new ArrayList<>();

    try (Connection con = XJdbc.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            KhoNuocUong kho = new KhoNuocUong();
            kho.setMaKhoNuoc(rs.getInt("MaKhoNuoc"));
            kho.setMaNuocUong(rs.getInt("MaNuocUong"));
            kho.setDonViTinh(rs.getString("DonViTinh"));
            kho.setSoLuong(rs.getInt("SoLuong"));
            kho.setGiaNhap(rs.getDouble("GiaNhap"));
            kho.setNgayNhap(rs.getDate("NgayNhap"));

            // Gán thông tin nước uống nếu cần
            NuocUongDAO nuocDAO = new NuocUongDAOImpl();
            NuocUong nuoc = nuocDAO.findById(kho.getMaNuocUong());
            kho.setNuocUong(nuoc);

            list.add(kho);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}


    @Override
  public KhoNuocUong findById(int maKhoNuoc) {
    String sql = "SELECT * FROM KHO_NUOCUONG WHERE MaKhoNuoc = ?";
    try (Connection con = XJdbc.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, maKhoNuoc);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                KhoNuocUong kho = new KhoNuocUong();
                kho.setMaKhoNuoc(maKhoNuoc);
                kho.setMaNuocUong(rs.getInt("MaNuocUong"));
                kho.setDonViTinh(rs.getString("DonViTinh"));
                kho.setSoLuong(rs.getInt("SoLuong"));
                kho.setGiaNhap(rs.getDouble("GiaNhap"));
                kho.setNgayNhap(rs.getDate("NgayNhap"));

                NuocUongDAO nuocDAO = new NuocUongDAOImpl();
                NuocUong nuoc = nuocDAO.findById(kho.getMaNuocUong());
                kho.setNuocUong(nuoc);

                return kho;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}



//    @Override
//   public void insert(KhoNuocUong kho) {
//    String sql = "INSERT INTO KHO_NUOCUONG (MaNuocUong, DonViTinh, SoLuong, GiaNhap, NgayNhap) VALUES (?, ?, ?, ?, ?)";
//    try (Connection con = XJdbc.getConnection();
//         PreparedStatement ps = con.prepareStatement(sql)) {
//        ps.setInt(1, kho.getMaNuocUong());
//        ps.setString(2, kho.getDonViTinh());
//        ps.setInt(3, kho.getSoLuong());
//        ps.setDouble(4, kho.getGiaNhap());
//        ps.setDate(5, kho.getNgayNhap());
//        ps.executeUpdate();
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//}
   @Override
public void insert(KhoNuocUong kho) {
    String sqlInsert = "INSERT INTO KHO_NUOCUONG (MaNuocUong, DonViTinh, SoLuong, GiaNhap, NgayNhap) VALUES (?, ?, ?, ?, ?)";
    String sqlUpdateTonKho = """
        MERGE INTO TONKHO AS target
        USING (SELECT ? AS MaNuocUong) AS source
        ON target.MaNuocUong = source.MaNuocUong
        WHEN MATCHED THEN UPDATE SET SoLuongTong = SoLuongTong + ?
        WHEN NOT MATCHED THEN INSERT (MaNuocUong, SoLuongTong) VALUES (?, ?);
    """;

    try (Connection con = XJdbc.getConnection()) {
        con.setAutoCommit(false); // bắt đầu transaction

        // Thêm dữ liệu nhập kho
        try (PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
            psInsert.setInt(1, kho.getMaNuocUong());
            psInsert.setString(2, kho.getDonViTinh());
            psInsert.setInt(3, kho.getSoLuong());
            psInsert.setDouble(4, kho.getGiaNhap());
            psInsert.setDate(5, kho.getNgayNhap());
            psInsert.executeUpdate();
        }

        // Cập nhật tồn kho
        try (PreparedStatement psUpdateTonKho = con.prepareStatement(sqlUpdateTonKho)) {
            psUpdateTonKho.setInt(1, kho.getMaNuocUong());  // SELECT ? AS MaNuocUong
            psUpdateTonKho.setInt(2, kho.getSoLuong());     // UPDATE SoLuongTong = SoLuongTong + ?
            psUpdateTonKho.setInt(3, kho.getMaNuocUong());  // INSERT MaNuocUong
            psUpdateTonKho.setInt(4, kho.getSoLuong());     // INSERT SoLuongTong
            psUpdateTonKho.executeUpdate();
        }

        con.commit(); // commit transaction
    } catch (Exception e) {
        e.printStackTrace();
        try {
            // rollback khi lỗi
            if (!XJdbc.getConnection().getAutoCommit()) {
                XJdbc.getConnection().rollback();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}



    @Override
   public void update(KhoNuocUong kho) {
    String sql = "UPDATE KHO_NUOCUONG SET MaNuocUong = ?, DonViTinh = ?, SoLuong = ?, GiaNhap = ?, NgayNhap = ? WHERE MaKhoNuoc = ?";
    try (Connection con = XJdbc.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, kho.getMaNuocUong());
        ps.setString(2, kho.getDonViTinh());
        ps.setInt(3, kho.getSoLuong());
        ps.setDouble(4, kho.getGiaNhap());
        ps.setDate(5, kho.getNgayNhap());
        ps.setInt(6, kho.getMaKhoNuoc());
        ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    @Override
   public void delete(int maKhoNuoc) {
    String sql = "DELETE FROM KHO_NUOCUONG WHERE MaKhoNuoc = ?";
    try (Connection con = XJdbc.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, maKhoNuoc);
        ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}@Override
public List<KhoNuocUong> findByNgayNhap(LocalDate ngayNhap) {
    List<KhoNuocUong> list = new ArrayList<>();

    String sql = "SELECT * FROM KHO_NUOCUONG WHERE CONVERT(date, NgayNhap) = ?";
    try (Connection con = XJdbc.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setDate(1, java.sql.Date.valueOf(ngayNhap));
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            KhoNuocUong nuoc = mapResultSetToKho(rs); // Giả sử bạn có hàm này để map dữ liệu
            list.add(nuoc);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}private KhoNuocUong mapResultSetToKho(ResultSet rs) throws SQLException {
    KhoNuocUong kho = new KhoNuocUong();

    kho.setMaNuocUong(rs.getInt("MaNuocUong"));
    kho.setDonViTinh(rs.getString("DonViTinh"));
    kho.setSoLuong(rs.getInt("SoLuong"));
    kho.setGiaNhap(rs.getDouble("GiaNhap"));
    kho.setNgayNhap(rs.getDate("NgayNhap"));
    kho.setMaKhoNuoc(rs.getInt("MaKhoNuoc"));

    // Nếu có quan hệ với bảng NuocUong thì set thêm:
    NuocUongDAO nuocUongDAO = new NuocUongDAOImpl();
    NuocUong nuocUong = nuocUongDAO.findById(kho.getMaNuocUong());
    kho.setNuocUong(nuocUong);

    return kho;
}@Override
public List<KhoNuocUong> findAllTonKho() {
    List<KhoNuocUong> list = new ArrayList<>();
    String sql = "SELECT MaNuocUong, SoLuongTong FROM TONKHO";

    try (Connection conn = XJdbc.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            KhoNuocUong kho = new KhoNuocUong();
            kho.setMaNuocUong(rs.getInt("MaNuocUong"));
            kho.setSoLuong(rs.getInt("SoLuongTong"));
            kho.setDonViTinh("lon"); // Gán mặc định hoặc lấy từ đâu đó nếu cần
            list.add(kho);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}


//@Override
//public List<KhoNuocUong> findAllTonKho() {
//    List<KhoNuocUong> list = new ArrayList<>();
//    String sql = """
//        SELECT MaNuocUong, SUM(SoLuong) AS TongSoLuong, DonViTinh
//        FROM KHO_NUOCUONG
//        GROUP BY MaNuocUong, DonViTinh
//    """;
//
//    try (Connection conn = XJdbc.getConnection();
//         PreparedStatement stmt = conn.prepareStatement(sql);
//         ResultSet rs = stmt.executeQuery()) {
//        while (rs.next()) {
//            KhoNuocUong kho = new KhoNuocUong();
//            kho.setMaNuocUong(rs.getInt("MaNuocUong"));
//            kho.setSoLuong(rs.getInt("TongSoLuong"));
//            kho.setDonViTinh(rs.getString("DonViTinh"));
//            list.add(kho);
//        }
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//    return list;
//}
@Override
public List<KhoNuocUong> findAllTongHop() {
    List<KhoNuocUong> ds = new ArrayList<>();
    String sql = "SELECT n.MaNuocUong, n.TenNuocUong, ISNULL(SUM(k.SoLuong), 0) AS SoLuong, MAX(k.DonViTinh) AS DonViTinh " +
                 "FROM NUOCUONG n " +
                 "LEFT JOIN KHO_NUOCUONG k ON n.MaNuocUong = k.MaNuocUong " +
                 "GROUP BY n.MaNuocUong, n.TenNuocUong";

    try (
        Connection conn = XJdbc.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
    ) {
        while (rs.next()) {
            KhoNuocUong kho = new KhoNuocUong();
            kho.setMaNuocUong(rs.getInt("MaNuocUong"));
            kho.setSoLuong(rs.getInt("SoLuong"));
            kho.setDonViTinh(rs.getString("DonViTinh"));
            ds.add(kho);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return ds;
}
@Override
public List<KhoNuocUong> findAllByMaNuocUong(int maNuocUong) {
    List<KhoNuocUong> list = new ArrayList<>();
    String sql = "SELECT * FROM KHO_NUOCUONG WHERE MaNuocUong = ? ORDER BY NgayNhap DESC";
    try (Connection con = XJdbc.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, maNuocUong);
        try (ResultSet rs = ps.executeQuery()) {
            NuocUongDAO nuocDAO = new NuocUongDAOImpl();
            while (rs.next()) {
                KhoNuocUong kho = new KhoNuocUong();
                kho.setMaKhoNuoc(rs.getInt("MaKhoNuoc"));
                kho.setMaNuocUong(rs.getInt("MaNuocUong"));
                kho.setDonViTinh(rs.getString("DonViTinh"));
                kho.setSoLuong(rs.getInt("SoLuong"));
                kho.setGiaNhap(rs.getDouble("GiaNhap"));
                kho.setNgayNhap(rs.getDate("NgayNhap"));

                kho.setNuocUong(nuocDAO.findById(kho.getMaNuocUong()));

                list.add(kho);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}







}
