package dao;

import entity.NhanVien;
import java.util.List;

public interface NhanVienDAO {
    void insert(NhanVien nv);
    void update(NhanVien nv);
    void deleteById(Integer id);
    NhanVien findById(Integer id);
    List<NhanVien> findAll();
    List<NhanVien> selectBySql(String sql, Object... args);
public List<Object[]> findAllWithUsername();
public int insertAndReturnId(NhanVien nv);
public boolean existsBySdt(String sdt);
    // thêm hàm này để login hoạt động đúng
//    NhanVien findByTenDangNhap(String tenDangNhap);
//    public NhanVien findNhanVienByTenDangNhap(String tendangnhap);
}
