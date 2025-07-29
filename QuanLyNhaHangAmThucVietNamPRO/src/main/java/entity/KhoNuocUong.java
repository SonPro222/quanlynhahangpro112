/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author ACER
 */
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhoNuocUong {
    private  int maKhoNuoc;
    private int maNuocUong;
    private NuocUong nuocUong;
    private String donViTinh;
    private int soLuong;
    private double giaNhap;
    private Date ngayNhap;
}
