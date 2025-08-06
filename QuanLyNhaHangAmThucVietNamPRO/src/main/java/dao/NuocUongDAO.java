/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.NuocUong;
import java.util.List;

/**
 *
 * @author ACER
 */
public interface NuocUongDAO {
     public void truSoLuongTon(int maNuocUong, int soLuongTru);
    List<NuocUong> findAll();
 public NuocUong findByTenMon(String tenMon);
    List<NuocUong> findByLoaiNuocUongId(int maLoaiNuocUong);
    void insert(NuocUong nuoc);
    void update(NuocUong nuoc);
    void delete(int id);
    NuocUong create(NuocUong nuoc);
    public NuocUong findByTenNuoc(String tenNuoc);
     NuocUong findById(int maNuocUong);
     
}
