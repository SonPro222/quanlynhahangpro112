package dao.impl;

import dao.XuatKhoNuocUongDAO;
import entity.XuatKhoNuocUong;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.XJdbc;

public class XuatKhoNuocUongDAOImpl implements XuatKhoNuocUongDAO {

    @Override
    public void insert(XuatKhoNuocUong xk) {
        String sql = "INSERT INTO XuatKhoNuocUong (maNuocUong, tenNuocUong, soLuong, ngayXuat, maHD) VALUES (?, ?, ?, ?, ?)";
        XJdbc.update(sql,
            xk.getMaNuocUong(),
            xk.getTenNuocUong(),
            xk.getSoLuong(),
            new java.sql.Date(xk.getNgayXuat().getTime()),
            xk.getMaHD()
        );
    }

    @Override
    public void update(XuatKhoNuocUong xk) {
        String sql = "UPDATE XuatKhoNuocUong SET maNuocUong=?, tenNuocUong=?, soLuong=?, ngayXuat=?, maHD=? WHERE maXuatKho=?";
        XJdbc.update(sql,
            xk.getMaNuocUong(),
            xk.getTenNuocUong(),
            xk.getSoLuong(),
            new java.sql.Date(xk.getNgayXuat().getTime()),
            xk.getMaHD(),
            xk.getMaXuatKho()
        );
    }

    @Override
    public void delete(int maXuatKho) {
        String sql = "DELETE FROM XuatKhoNuocUong WHERE maXuatKho=?";
        XJdbc.update(sql, maXuatKho);
    }

    @Override
    public XuatKhoNuocUong selectById(int maXuatKho) {
        String sql = "SELECT * FROM XuatKhoNuocUong WHERE maXuatKho=?";
        List<XuatKhoNuocUong> list = this.selectBySql(sql, maXuatKho);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<XuatKhoNuocUong> selectAll() {
        String sql = "SELECT * FROM XuatKhoNuocUong";
        return selectBySql(sql);
    }

    private List<XuatKhoNuocUong> selectBySql(String sql, Object... args) {
        List<XuatKhoNuocUong> list = new ArrayList<>();
        try {
            ResultSet rs = XJdbc.query(sql, args);
            while (rs.next()) {
                XuatKhoNuocUong xk = new XuatKhoNuocUong();
                xk.setMaXuatKho(rs.getInt("maXuatKho"));
                xk.setMaNuocUong(rs.getInt("maNuocUong"));
                xk.setTenNuocUong(rs.getString("tenNuocUong"));
                xk.setSoLuong(rs.getInt("soLuong"));
                xk.setNgayXuat(rs.getDate("ngayXuat"));
                xk.setMaHD(rs.getInt("maHD"));
                list.add(xk);
            }
            rs.getStatement().getConnection().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public List<Object[]> getLichSuXuatKho() {
    String sql = """
        SELECT xk.maXuatKho, nu.tenNuocUong, xk.soLuong, xk.ngayXuat, xk.maHD, nu.donViTinh
        FROM XuatKhoNuocUong xk
        JOIN NuocUong nu ON xk.maNuocUong = nu.maNuocUong
    """;
    return XJdbc.queryArray(sql);
}
    @Override
    public XuatKhoNuocUong findByMaNuocUongAndMaHD(int maNuocUong, int maHD) {
    String sql = "SELECT * FROM XuatKhoNuocUong WHERE maNuocUong = ? AND maHD = ?";
    List<XuatKhoNuocUong> list = selectBySql(sql, maNuocUong, maHD);
    return list.isEmpty() ? null : list.get(0);
}


}
