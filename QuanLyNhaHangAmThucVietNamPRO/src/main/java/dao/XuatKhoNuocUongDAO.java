/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

/**
 *
 * @author ACER
 */
import entity.XuatKhoNuocUong;
import java.util.List;

public interface XuatKhoNuocUongDAO {
    void insert(XuatKhoNuocUong xk);
    void update(XuatKhoNuocUong xk);
    void delete(int maXuatKho);
    XuatKhoNuocUong selectById(int maXuatKho);
    List<XuatKhoNuocUong> selectAll();
    public XuatKhoNuocUong findByMaNuocUongAndMaHD(int maNuocUong, int maHD);
}
