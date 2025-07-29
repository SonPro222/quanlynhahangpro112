/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.impl;

import java.util.List;
import dao.TaiKhoanDAO;
import entity.NhanVien;
import entity.TaiKhoan;
import java.sql.ResultSet;
import util.XJdbc;
import util.XQuery;

public class TaiKhoanDAOImpl implements TaiKhoanDAO {

    String createSql = "INSERT INTO TAIKHOAN (TENDANGNHAP, MATKHAU, VAITRO, MaNV) VALUES (?, ?, ?, ?)";
    String updateSql = "UPDATE TAIKHOAN SET MATKHAU = ?, VAITRO = ?, MaNV = ? WHERE TENDANGNHAP = ?";
    String deleteSql = "DELETE FROM TAIKHOAN WHERE TENDANGNHAP = ?";
    String findAllSql = "SELECT * FROM TAIKHOAN";
    String findByIdSql = "SELECT * FROM TAIKHOAN WHERE TENDANGNHAP = ?";

    @Override
    public TaiKhoan create(TaiKhoan entity) {
        XJdbc.executeUpdate(createSql,
                entity.getTendangnhap(),
                entity.getMatkhau(),
                entity.getVaitro(),
                entity.getMaNV() // thêm mã nhân viên
        );
        return entity;
    }

    @Override
    public void update(TaiKhoan entity) {
        int rows = XJdbc.executeUpdate(updateSql,
                entity.getMatkhau(),
                entity.getVaitro(),
                entity.getMaNV(), // thêm dòng cập nhật MaNV
                entity.getTendangnhap()
        );
        System.out.println("Đã cập nhật " + rows + " dòng.");
    }

    @Override
    public void deleteById(String username) {
        XJdbc.executeUpdate(deleteSql, username);
    }

    @Override
    public List<TaiKhoan> findAll() {
        return XQuery.getEntityList(TaiKhoan.class, findAllSql);
    }

    public TaiKhoan findById(String username) {
    String sql = "SELECT * FROM TAIKHOAN WHERE TENDANGNHAP = ?";
    try (ResultSet rs = XJdbc.executeQuery(sql, username)) {
        if (rs.next()) {
            TaiKhoan user = new TaiKhoan();
            user.setTendangnhap(rs.getString("TenDangNhap"));
            user.setMatkhau(rs.getString("MatKhau"));
            user.setVaitro(rs.getString("VaiTro"));
            user.setMaNV(rs.getInt("MaNV")); // <-- Bắt buộc có dòng này!
            return user;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}


    @Override
    public boolean exists(String username) {
        String sql = "SELECT COUNT(*) FROM TAIKHOAN WHERE TENDANGNHAP = ?";
        Integer count = (Integer) XJdbc.value(sql, username);
        return count != null && count > 0;
    }

    @Override
    public List findAllWithMonAn() {
        throw new UnsupportedOperationException("Not supported in UserDAOImpl.");
    }@Override
public boolean existsByMaNV(int maNV) {
    String sql = "SELECT COUNT(*) FROM TAIKHOAN WHERE MaNV = ?";
    Integer count = (Integer) XJdbc.value(sql, maNV);
    return count != null && count > 0;
}
@Override
public boolean kiemTraUsernameVaMaNV(String username, int maNV) {
    String sql = "SELECT COUNT(*) FROM TAIKHOAN WHERE TENDANGNHAP = ? AND MaNV = ?";
    Integer count = (Integer) XJdbc.value(sql, username, maNV);
    return count != null && count > 0;
}
    @Override
public void deleteByMaNV(int maNV) {
    String sql = "DELETE FROM TAIKHOAN WHERE MaNV = ?";
    XJdbc.executeUpdate(sql, maNV);
}

}
