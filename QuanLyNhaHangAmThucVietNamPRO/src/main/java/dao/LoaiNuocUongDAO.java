/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.LoaiNuocUong;
import java.util.List;

/**
 *
 * @author ACER
 */
public interface LoaiNuocUongDAO {
    List<LoaiNuocUong> findAll();
    LoaiNuocUong findById(int id);
    void insert(LoaiNuocUong loai);
    void update(LoaiNuocUong loai);
    void delete(int id);
}
