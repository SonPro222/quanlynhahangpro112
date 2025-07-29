package dao;

import entity.HoaDonChiTiet;
import java.util.List;

public interface HoaDonChiTietDAO {
    void insert(HoaDonChiTiet ct);
    List<HoaDonChiTiet> findByHoaDonId(int maHD);
    public void deleteByHoaDonId(int maCT);
     int updateSoLuong(HoaDonChiTiet ct);
     double tinhTongTienTheoHoaDon(int maCT);
     public boolean deleteChiTietByMaHDAndMon(int maHD, int maMon, String loai);
     public boolean updateSoLuongByMon(int maHD, int maMon, int soLuong, String loai);
     void updateSoLuongTheoMaHDVaMaMon(int maHD, int maMon, int soLuongMoi);
      void  updateSoLuongTheoMaHDVaMaNuoc(int maHD, int maNuoc, int soLuongMoi);
      
      int laySoLuongNuocTheoMaHD(int maHD, int maNuoc);
}
