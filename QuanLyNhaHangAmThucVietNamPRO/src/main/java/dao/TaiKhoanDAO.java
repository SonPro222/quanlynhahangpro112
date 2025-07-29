/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author ACER
 */
import entity.NhanVien;
import entity.TaiKhoan;

public interface TaiKhoanDAO extends CrudDAO<TaiKhoan, String> {
     TaiKhoan create(TaiKhoan entity);
     boolean exists(String username);
     public boolean existsByMaNV(int maNV);
     public boolean kiemTraUsernameVaMaNV(String username, int maNV);
     public void deleteByMaNV(int maNV);
}