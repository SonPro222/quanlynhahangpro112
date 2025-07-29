/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.impl;

/**
 *
 * @author ACER
 */
import dao.TonKhoDAO;
import entity.TonKho;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import util.XJdbc;

public class TonKhoDAOImpl implements TonKhoDAO {

    @Override
    public TonKho findByMaNuocUong(int maNuocUong) {
        String sql = "SELECT MaNuocUong, SoLuongTong FROM TONKHO WHERE MaNuocUong = ?";
        try (Connection conn = XJdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNuocUong);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new TonKho(rs.getInt("MaNuocUong"), rs.getInt("SoLuongTong"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TonKho> findAll() {
        List<TonKho> list = new ArrayList<>();
        String sql = "SELECT MaNuocUong, SoLuongTong FROM TONKHO";
        try (Connection conn = XJdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new TonKho(rs.getInt("MaNuocUong"), rs.getInt("SoLuongTong")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean insert(TonKho tonKho) {
        String sql = "INSERT INTO TONKHO (MaNuocUong, SoLuongTong) VALUES (?, ?)";
        try (Connection conn = XJdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tonKho.getMaNuocUong());
            ps.setInt(2, tonKho.getSoLuongTong());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(TonKho tonKho) {
        String sql = "UPDATE TONKHO SET SoLuongTong = ? WHERE MaNuocUong = ?";
        try (Connection conn = XJdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tonKho.getSoLuongTong());
            ps.setInt(2, tonKho.getMaNuocUong());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int maNuocUong) {
        String sql = "DELETE FROM TONKHO WHERE MaNuocUong = ?";
        try (Connection conn = XJdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNuocUong);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
   public boolean laNuocUong(int maMon) {
    String sql = "SELECT COUNT(*) FROM NuocUong WHERE MaNuocUong = ?";
    ResultSet rs = XJdbc.executeQuery(sql, maMon);
    try {
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}@Override
public void updateSoLuong(int maNuocUong, int soLuongMoi) {
    String sql = "UPDATE TonKho SET SoLuongTong = ? WHERE MaNuocUong = ?";
    XJdbc.executeUpdate(sql, soLuongMoi, maNuocUong);
}

   
}
