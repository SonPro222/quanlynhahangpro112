/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author ACER
 */
import lombok.Data;

@Data
public class NuocUong {
    private int maNuocUong;
    private LoaiNuocUong loaiNuocUong;
    private String tenNuocUong;
    private double giaBan;
    private String hinhAnh;
    private String donViTinh;
}

