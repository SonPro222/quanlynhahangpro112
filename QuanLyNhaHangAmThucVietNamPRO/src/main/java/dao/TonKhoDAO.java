/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

/**
 *
 * @author ACER
 */
import entity.TonKho;
import java.util.List;

public interface TonKhoDAO {
    TonKho findByMaNuocUong(int maNuocUong);
    List<TonKho> findAll();
    boolean insert(TonKho tonKho);
    boolean update(TonKho tonKho);
    boolean delete(int maNuocUong);
    boolean laNuocUong(int maMon);
    public void updateSoLuong(int maNuocUong, int soLuongMoi);
}
