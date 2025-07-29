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
public class XuatKhoNuocUong {
    private int maXuatKho;
    private int maNuocUong;
    private  String tenNuocUong;
    private int soLuong;
    private Date ngayXuat;
    private int maHD; // Mã hóa đơn
}
