/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import entity.TaiKhoan;

/**
 *
 * @author ACER
 */
    public class XAuth {
        public static TaiKhoan user = TaiKhoan.builder()
                .tendangnhap(null)
                .build(); // biến user này sẽ được thay thế sau khi đăng nhập
    }