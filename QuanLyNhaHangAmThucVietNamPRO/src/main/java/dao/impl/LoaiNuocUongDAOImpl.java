/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.impl;

import dao.LoaiNuocUongDAO;
import entity.LoaiNuocUong;
import entity.MonAn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import util.XJdbc;

/**
 *
 * @author ACER
 */

 public class LoaiNuocUongDAOImpl implements LoaiNuocUongDAO {
    @Override
    public List<LoaiNuocUong> findAll() {
        String sql = "SELECT * FROM LOAINUOCUONG";
        List<LoaiNuocUong> list = new ArrayList<>();

        try (Connection con = XJdbc.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LoaiNuocUong loai = new LoaiNuocUong();
                loai.setMaLoaiNuoc(rs.getInt("MaLoaiNuoc"));
                loai.setTenLoai(rs.getString("TenLoai"));
                loai.setHinhAnh(rs.getString("HinhAnh"));
                list.add(loai);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public LoaiNuocUong findById(int id) {
        String sql = "SELECT * FROM LOAINUOCUONG WHERE MaLoaiNuoc = ?";
        try (Connection con = XJdbc.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LoaiNuocUong loai = new LoaiNuocUong();
                    loai.setMaLoaiNuoc(id);
                    loai.setTenLoai(rs.getString("TenLoai"));
                    loai.setHinhAnh(rs.getString("HinhAnh"));
                    return loai;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insert(LoaiNuocUong loai) {
        String sql = "INSERT INTO LOAINUOCUONG (TenLoai, HinhAnh) VALUES (?, ?)";
        try (Connection con = XJdbc.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, loai.getTenLoai());
            ps.setString(2, loai.getHinhAnh());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(LoaiNuocUong loai) {
        String sql = "UPDATE LOAINUOCUONG SET TenLoai = ?, HinhAnh = ? WHERE MaLoaiNuoc = ?";
        try (Connection con = XJdbc.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, loai.getTenLoai());
            ps.setString(2, loai.getHinhAnh());
            ps.setInt(3, loai.getMaLoaiNuoc());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM LOAINUOCUONG WHERE MaLoaiNuoc = ?";
        try (Connection con = XJdbc.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public LoaiNuocUong create(LoaiNuocUong loai) {
        String sql = "INSERT INTO LoaiNuocUong (TenLoai, HinhAnh) VALUES (?, ?)";
        try (Connection conn = XJdbc.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, loai.getTenLoai());
            ps.setString(2, loai.getHinhAnh());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    loai.setMaLoaiNuoc(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loai;
    }
    @Override
public LoaiNuocUong findByTen(String tenLoai) {
    String sql = "SELECT * FROM LoaiNuocUong WHERE TenLoai = ?";
    try (Connection conn = XJdbc.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, tenLoai);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                LoaiNuocUong nuoc = new LoaiNuocUong();
                nuoc.setMaLoaiNuoc(rs.getInt("MaLoaiNuoc"));
                nuoc.setTenLoai(rs.getString("TenLoai"));
                nuoc.setHinhAnh(rs.getString("HinhAnh"));
                return nuoc;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}
}


