/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

/**
 *
 * @author ACER
 */
import entity.KhoNuocUong;
import java.time.LocalDate;
import java.util.List;

public interface KhoNuocUongDAO {
    List<KhoNuocUong> findAllTongHop();
    List<KhoNuocUong> findAll();
    KhoNuocUong findById(int maNuocUong);
    void insert(KhoNuocUong kho);
    void update(KhoNuocUong kho);
    void delete(int maNuocUong);
     List<KhoNuocUong> findByNgayNhap(LocalDate ngayNhap);
     public List<KhoNuocUong> findAllTonKho();
     List<KhoNuocUong> findAllByMaNuocUong(int maNuocUong);
}
