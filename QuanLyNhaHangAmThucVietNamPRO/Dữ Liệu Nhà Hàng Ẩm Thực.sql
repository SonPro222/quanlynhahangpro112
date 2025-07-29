
﻿-- Tạo database
CREATE DATABASE QUANLYNHAHANGAMTHUCVIETNAM;
GO

USE QUANLYNHAHANGAMTHUCVIETNAM;

Use master;

ALTER DATABASE QUANLYNHAHANGAMTHUCVIETNAM
SET SINGLE_USER
WITH ROLLBACK IMMEDIATE;
GO

go

DROP DATABASE  QUANLYNHAHANGAMTHUCVIETNAM;

-- Bảng tài khoản
CREATE TABLE TAIKHOAN (
    TENDANGNHAP VARCHAR(50) PRIMARY KEY,
    MATKHAU VARCHAR(100) NOT NULL,
    VAITRO NVARCHAR(20) NOT NULL,
	MaNV INT,
	FOREIGN KEY(MaNV) REFERENCES NHANVIEN(MaNV)
);

INSERT INTO TAIKHOAN (TENDANGNHAP, MATKHAU, VAITRO) VALUES
('son', '1', N'Quản lý'),
('an', '1', N'Nhân Viên');

-- Bảng món ăn (MaMonAn tự tăng)
CREATE TABLE MonAn (
    MaMonAn INT IDENTITY(1,1) PRIMARY KEY,
    TenMonAn NVARCHAR(100),
    HinhAnh NVARCHAR(255)
);

INSERT INTO MonAn (TenMonAn, HinhAnh) VALUES
(N'Lẩu', 'Lau.png'),
(N'Cá', 'Ca.png'),
(N'Thịt gà', 'Thit_ga.png'),
(N'Thịt heo', 'Thit_heo.png'),
(N'Thịt dê', 'Thit_de.png'),
(N'Bia', 'Bia.png'),
(N'Rượu', 'Ruou.png'),
(N'Nước ngọt', 'Nuoc_ngot.png'),
(N'Khăn lạnh', 'Khan_lanh.png'),
(N'Thịt bò', 'Thit_bo.png'),
(N'Tôm', 'Tom.png'),
(N'Ếch', 'Ech.png'),
(N'Ốc các loại', 'Oc.png'),
(N'Rau củ', 'Rau_cu.png');

-- Bảng chi tiết món ăn (MaChiTiet tự tăng)
CREATE TABLE ChiTietMonAn (
    MaChiTiet INT IDENTITY(1,1) PRIMARY KEY,
    TenMon NVARCHAR(100) NOT NULL,
    Gia FLOAT NOT NULL,
    MaMonAn INT NOT NULL,
	HinhAnh NVARCHAR(255),
    FOREIGN KEY (MaMonAn) REFERENCES MonAn(MaMonAn)
);

-- Thêm dữ liệu chi tiết món ăn
-- Thêm dữ liệu chi tiết món ăn với hình ảnh
INSERT INTO ChiTietMonAn (TenMon, Gia, MaMonAn, HinhAnh) VALUES
(N'Lẩu Thái chua cay', 280000, 1, 'Lau_Thai_Chua_Cay.png'),
(N'Lẩu hải sản', 320000, 1, 'Lau_Hai_San.png'),
(N'Lẩu bò nhúng giấm', 290000, 1, 'Lau_Bo_Nhung_Giam.png'),
(N'Lẩu gà lá é', 270000, 1, 'Lau_Ga_La_E.png'),
(N'Lẩu cá kèo lá giang', 260000, 1, 'Lau_Ca_Keo_La_Giang.png'),
(N'Lẩu ếch măng cay', 275000, 1, 'Lau_Ech_Mang_Cay.png'),
(N'Lẩu gà nấm', 280000, 1, 'Lau_Ga_Nam.png'),
(N'Lẩu cua đồng bắp bò', 310000, 1, 'Lau_Cua_Dong_Bap_Bo.png'),
(N'Lẩu thập cẩm', 350000, 1, 'Lau_Thap_Cam.png'),
(N'Lẩu mắm miền Tây', 290000, 1, 'Lau_Mam_Mien_Tay.png'),

-- Cá (2)
(N'Cá lóc nướng trui', 180000, 2, 'Ca_Loc_Nuong_Trui.png'),
(N'Cá lóc kho tộ', 170000, 2, 'Ca_Loc_Kho_To.png'),
(N'Cá lóc hấp bầu', 190000, 2, 'Ca_Loc_Hap_Bau.png'),
(N'Cá lóc chiên xù', 175000, 2, 'Ca_Loc_Chien_Xu.png'),
(N'Cá lóc nấu canh chua', 165000, 2, 'Ca_Loc_Nau_Canh_Chua.png'),
(N'Cá bống kho tiêu', 160000, 2, 'Ca_Bong_Kho_Tieu.png'),
(N'Cá bống chiên giòn', 155000, 2, 'Ca_Bong_Chien_Gion.png'),
(N'Cá bống kho nghệ', 165000, 2, 'Ca_Bong_Kho_Nge.png'),
(N'Cá bống rim mặn', 150000, 2, 'Ca_Bong_Rim_Man.png'),
(N'Cá bống nấu dưa chua', 145000, 2, 'Ca_Bong_Nau_Dua_Chua.png'),

-- Thịt gà (3)
(N'Gà luộc lá chanh', 150000, 3, 'Ga_Luoc_La_Chanh.png'),
(N'Gà rang muối', 160000, 3, 'Ga_Rang_Muoi.png'),
(N'Gà xào sả ớt', 155000, 3, 'Ga_Xao_Sa_0t.png'),
(N'Gà hấp mắm nhĩ', 170000, 3, 'Ga_Hap_Mam_Nhi.png'),
(N'Gà nướng mật ong', 180000, 3, 'Ga_Nuong_Mat_Ong.png'),

-- Thịt bò (10)
(N'Bò lúc lắc', 180000, 10, 'Bo_Luc_Lac.png'),
(N'Bò xào bông cải', 170000, 10, 'Bo_Xao_Bong_Cai.png'),
(N'Bò né trứng ốp la', 165000, 10, 'Bo_Ne_Trung_Op_La.png'),
(N'Bò nướng lá lốt', 175000, 10, 'Bo_Nuong_La_Lot.png'),
(N'Bò kho tiêu', 185000, 10, 'Bo_Kho_Tieu.png'),

-- Tôm (11)
(N'Tôm hấp nước dừa', 160000, 11, 'Tom_Hap_Nuoc_Dua.png'),
(N'Tôm chiên xù', 155000, 11, 'Tom_Chien_Xu.png'),
(N'Tôm sốt me', 165000, 11, 'Tom_Sot_Me.png'),
(N'Tôm rang muối', 170000, 11, 'Tom_Rang_Muoi.png'),
(N'Tôm nướng mọi', 175000, 11, 'Tom_Nuong_Moi.png'),

-- Ếch (12)
(N'Ếch xào sả ớt', 160000, 12, 'Ech_Xao_Sa_0t.png'),
(N'Ếch chiên nước mắm', 165000, 12, 'Ech_Chien_Nuoc_Mam.png'),
(N'Ếch kho tiêu', 170000, 12, 'Ech_Kho_Tieu.png'),
(N'Ếch nướng lá lốt', 180000, 12, 'Ech_Nuong_La_Lot.png'),
(N'Ếch nấu cà ri', 185000, 12, 'Ech_Nau_Ca_Ri.png'),

-- Ốc (13)
(N'Ốc hương xào bơ tỏi', 130000, 13, 'Oc_Huong_Xao_Bo_Toi.png'),
(N'Ốc móng tay xào rau muống', 125000, 13, 'Oc_Mong_Tay_Xao_Rau_Muong.png'),
(N'Ốc len xào dừa', 135000, 13, 'Oc_Len_Xao_Dua.png'),
(N'Ốc gai hấp sả', 140000, 13, 'Oc_Gai_Hap_Sa.png'),
(N'Ốc mỡ xào me', 130000, 13, 'Oc_Mo_Xao_Me.png'),

-- Rau củ (14)
(N'Rau muống xào tỏi', 45000, 14, 'Rau_Muong_Xao_Toi.png'),
(N'Cải thìa sốt dầu hào', 50000, 14, 'Cai_Thia_Sot_Dau_Hao.png'),
(N'Cải ngọt luộc', 40000, 14, 'Cai_Ngot_Luoc.png'),
(N'Đậu bắp luộc', 35000, 14, 'Dau_Bap_Luoc.png'),
(N'Cà tím nướng mỡ hành', 55000, 14, 'Ca_Tim_Nuong_Mo_Hanh.png'),

-- Nước ngọt (8)
(N'Coca-Cola', 15000, 8, 'Coca_Cola.png'),
(N'Pepsi', 15000, 8, 'Pepsi.png'),
(N'7Up', 15000, 8, '7Up.png'),
(N'Nước cam ép', 25000, 8, 'Nuoc_Cam_Ep.png'),
(N'Trà tắc', 20000, 8, 'Tra_Tac.png'),

-- Bia - Rượu (6 & 7)
(N'Bia Tiger', 20000, 6, 'Bia_Tiger.png'),
(N'Bia Heineken', 25000, 6, 'Bia_Heineken.png'),
(N'Bia Sài Gòn', 18000, 6, 'Bia_Sai_Gon.png'),
(N'Rượu nếp cái hoa vàng', 150000, 7, 'Ruou_Nep_Cai_Hoa_Vang.png'),
(N'Rượu Soju Hàn Quốc', 120000, 7, 'Ruou_Soju_Han_Quoc.png'),

-- Khăn lạnh (9)
(N'Khăn lạnh', 5000, 9, 'Khan_Lanh.png');
DELETE FROM ChiTietMonAn WHERE MaMonAn = 4;

-- Món từ Thịt heo (MaMonAn = 4)
INSERT INTO ChiTietMonAn (TenMon, Gia, MaMonAn, HinhAnh) VALUES
(N'Thịt heo kho tiêu', 85000, 4, 'heo_kho_tieu.png'),
(N'Thịt heo nướng sả', 90000, 4, 'heo_nuong_sa.png'),
(N'Thịt heo quay giòn bì', 95000, 4, 'heo_quay_gion_bi.png'),
(N'Thịt heo xào chua ngọt', 88000, 4, 'heo_xao_chua_ngot.png'),
(N'Thịt heo kho trứng', 87000, 4, 'heo_kho_trung.png'),
(N'Thịt heo hấp hành', 89000, 4, 'heo_hap_hanh.png'),
(N'Thịt heo xào sả ớt', 86000, 4, 'heo_xao_sa_ot.png');

-- Món từ Thịt dê (MaMonAn = 5)
INSERT INTO ChiTietMonAn (TenMon, Gia, MaMonAn, HinhAnh) VALUES
(N'Dê xào lăn', 105000, 5, 'de_xao_lan.png'),
(N'Dê hấp gừng', 110000, 5, 'de_hap_gung.png'),
(N'Dê nướng ngũ vị', 120000, 5, 'de_nuong_ngu_vi.png'),
(N'Lẩu dê', 150000, 5, 'lau_de.png'),
(N'Dê tái chanh', 115000, 5, 'de_tai_chanh.png'),
(N'Dê xào sả ớt', 108000, 5, 'de_xao_sa_ot.png'),
(N'Dê nướng mọi', 125000, 5, 'de_nuong_moi.png');

INSERT INTO ChiTietMonAn (TenMon, Gia, MaMonAn, HinhAnh) VALUES
-- BIA (MaMonAn = 6)
(N'Bia Budweiser', 27000, 6, 'Bia_Budweiser.png'),
(N'Bia Larue', 16000, 6, 'Bia_Larue.png'),
(N'Bia Hà Nội', 15000, 6, 'Bia_HaNoi.png'),
(N'Bia 333', 14000, 6, 'Bia_333.png'),
(N'Bia Sapporo', 30000, 6, 'Bia_Sapporo.png'),
(N'Bia Strongbow', 32000, 6, 'Bia_Strongbow.png'),
(N'Bia Tiger Bạc', 22000, 6, 'Bia_Tiger_Bac.png'),

-- RƯỢU (MaMonAn = 7)
(N'Rượu Vodka Hà Nội', 95000, 7, 'Ruou_Vodka_HaNoi.png'),
(N'Rượu Tây Ballantine’s', 850000, 7, 'Ruou_Ballantines.png'),
(N'Rượu Chivas Regal 12', 1050000, 7, 'Ruou_Chivas12.png'),
(N'Rượu Sâm Ngọc Linh', 1200000, 7, 'Ruou_Sam_Ngoc_Linh.png'),
(N'Rượu Johnnie Walker Red Label', 900000, 7, 'Ruou_Johnnie_Red.png'),
(N'Rượu Soju Vị Nho', 125000, 7, 'Ruou_Soju_Nho.png'),
(N'Rượu Soju Đào', 125000, 7, 'Ruou_Soju_Dao.png');

---- BẢNG BÀN ĂN
CREATE TABLE BANAN (
 MaBan int PRIMARY KEY,
 TenBan NVARCHAR(50) NOT NULL,
 TrangThai NVARCHAR(20) NOT NULL -- Ví dụ: 'Trống', 'Đang dùng'
);

-- BẢNG NHÂN 
CREATE TABLE NHANVIEN (
    MaNV INT IDENTITY(1,1) PRIMARY KEY,
    TenNV NVARCHAR(100) NOT NULL,
    SDT VARCHAR(15),
    ChucVu NVARCHAR(50),
    Luong FLOAT,
    SoNgayLam INT,
    SoNgayNghi INT,
    TenDangNhap VARCHAR(50) UNIQUE NOT NULL,
    FOREIGN KEY (TenDangNhap) REFERENCES TAIKHOAN(TENDANGNHAP)
);
CREATE TABLE THONGKEDOANHTHU (
    MaDT INT IDENTITY(1,1) PRIMARY KEY,
    Ngay DATE NOT NULL UNIQUE,
    TongThu FLOAT DEFAULT 0,
    TongChi FLOAT DEFAULT 0,
    LoiNhuan AS (TongThu - TongChi), -- Tự động tính
    GhiChu NVARCHAR(255)
);
--  HÓA ĐƠN
-- =========================
CREATE TABLE HOADON (
    MaHD INT IDENTITY(1,1) PRIMARY KEY,
    MaBan INT NOT NULL,
    MaNV INT NOT NULL,              -- Người lập hóa đơn
    NgayLap DATETIME NOT NULL DEFAULT GETDATE(),
    TrangThai NVARCHAR(20),        -- 'Chưa thanh toán', 'Đã thanh toán'
    TongTien FLOAT,
	MaDT INT,
	FOREIGN KEY (MaDT) REFERENCES THONGKEDOANHTHU(MaDT),
    FOREIGN KEY (MaBan) REFERENCES BANAN(MaBan),
    FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV),
);

-- Bảng LOẠI NƯỚC UỐNG
CREATE TABLE LOAINUOCUONG (
    MaLoaiNuoc INT PRIMARY KEY IDENTITY(1,1),
    TenLoai NVARCHAR(100),
    HinhAnh NVARCHAR(255)
);
-- Bảng NƯỚC UỐNG
CREATE TABLE NUOCUONG (
    MaNuocUong INT PRIMARY KEY IDENTITY(1,1),
    MaLoaiNuoc INT,
    TenNuocUong NVARCHAR(100),
    GiaBan DECIMAL(18,2),
    HinhAnh NVARCHAR(255),
	DonViTinh nvarchar(50),
    FOREIGN KEY (MaLoaiNuoc) REFERENCES LOAINUOCUONG(MaLoaiNuoc)
);
CREATE TABLE CT_HOADON (
	MaCT INT IDENTITY(1,1) PRIMARY KEY,
    MaHD INT NOT NULL,
    MaChiTiet INT NOT NULL,
    TenMon NVARCHAR(100),          -- Lưu tên món thời điểm bán
    SoLuong INT NOT NULL,
    DonGia FLOAT NOT NULL,
	MaNuocUong INT NOT NULL, 
   
	FOREIGN KEY (MaNuocUong) REFERENCES NUOCUONG(MaNuocUong),
    FOREIGN KEY (MaHD) REFERENCES HOADON(MaHD),
    FOREIGN KEY (MaChiTiet) REFERENCES ChiTietMonAn(MaChiTiet)
);
CREATE TABLE PHIEUTRALUONG (
    MaPhieuLuong INT IDENTITY(1,1) PRIMARY KEY,  -- Use IDENTITY for auto-increment in SQL Server
    MaNV INT,
    NgayThanhToan DATE,
    TongLuong DECIMAL(10, 2),
    LuongTru DECIMAL(10, 2),
    GhiChu VARCHAR(255),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
);

CREATE TABLE CHITIEU (
    MaCT INT IDENTITY(1,1) PRIMARY KEY,
    Ngay DATE NOT NULL,
    SoTien FLOAT NOT NULL,
    MoTa NVARCHAR(255),
	MaDT INT,
	FOREIGN KEY (MaDT) REFERENCES THONGKEDOANHTHU(MaDT)
);
--- inset tay 
INSERT INTO CHITIEU (Ngay, SoTien, MoTa) VALUES 
('2025-07-01', 1000000, N'Mua nguyên liệu'),
('2025-07-02', 750000, N'Tiền điện nước'),
('2025-07-03', 500000, N'Thuê bảo trì máy lạnh');

INSERT INTO NHANVIEN (TenNV, SDT, ChucVu, Luong, SoNgayLam, SoNgayNghi, TenDangNhap)
VALUES 
(N'le hoang Sơn', '0898988818', N'Quản lý', 15000000, 26, 2, 'son'),
(N'Hoàng Thị An', '0911123456', N'Phục vụ', 8000000, 24, 1, 'an');


ALTER TABLE HOADON ADD MaDT INT;
ALTER TABLE HOADON
ADD CONSTRAINT FK_HOADON_THONGKEDOANHTHU FOREIGN KEY (MaDT) REFERENCES THONGKEDOANHTHU(MaDT);
ALTER TABLE CHITIEU ADD MaDT INT;
ALTER TABLE CHITIEU
ADD CONSTRAINT FK_CHITIEU_THONGKEDOANHTHU FOREIGN KEY (MaDT) REFERENCES THONGKEDOANHTHU(MaDT);
ALTER TABLE TAIKHOAN
ALTER COLUMN MaNV INT;

--CREATE VIEW v_THONGKEDOANHTHUFull AS
--SELECT 
  --  dt.MaDT,
    --dt.Ngay,
    --ISNULL(SUM(DISTINCT hd.TongTien), 0) AS TongThu,
    --ISNULL(SUM(ct.SoTien), 0) AS TongChi,
    --ISNULL(SUM(DISTINCT hd.TongTien), 0) - ISNULL(SUM(ct.SoTien), 0) AS LoiNhuan
--FROM THONGKEDOANHTHU dt
--LEFT JOIN HOADON hd ON dt.MaDT = hd.MaDT AND hd.TrangThai = N'Đã thanh toán'
--LEFT JOIN CHITIEU ct ON dt.MaDT = ct.MaDT
--GROUP BY dt.MaDT, dt.Ngay;
-- Tạo 40 bàn
DELETE FROM CT_HOADON;
DELETE FROM HOADON;
DELETE FROM BANAN;

DECLARE @i INT = 1;
WHILE @i <= 40
BEGIN
    INSERT INTO BANAN (MaBan, TenBan, TrangThai)
    VALUES (@i, N'Bàn số ' + CAST(@i AS NVARCHAR), N'Trống');
    SET @i = @i + 1;
END;

-- Khai báo biến tổng tiền
DECLARE @TongTien MONEY = 100000; -- hoặc lấy từ hóa đơn thật sự

-- Cập nhật doanh thu
IF EXISTS (SELECT * FROM THONGKEDOANHTHU WHERE Ngay = CAST(GETDATE() AS DATE))
    UPDATE THONGKEDOANHTHU
    SET TongThu = TongThu + @TongTien
    WHERE Ngay = CAST(GETDATE() AS DATE);
ELSE
    INSERT INTO THONGKEDOANHTHU(Ngay, TongThu)
    VALUES (CAST(GETDATE() AS DATE), @TongTien);

	-- 16/7
	-- thêm bảng chấm công cho nhân viên ---
	use QUANLYNHAHANGAMTHUCVIETNAM
	CREATE TABLE CHAMCONG (
    MaChamCong INT IDENTITY PRIMARY KEY,
    MaNV INT FOREIGN KEY REFERENCES NHANVIEN(MaNV),
    NgayCham DATE,
    CoMat BIT,
    GhiChu NVARCHAR(255)
);




-- Bảng KHO_NUOCUONG (nhập kho)
CREATE TABLE KHO_NUOCUONG (
    MaKho INT PRIMARY KEY IDENTITY(1,1),
    MaNuocUong INT,
    DonViTinh NVARCHAR(50),
    SoLuong INT,
    GiaNhap DECIMAL(18,2),
    NgayNhap DATE,
    FOREIGN KEY (MaNuocUong) REFERENCES NUOCUONG(MaNuocUong)
);

-- Bảng TỒN KHO
CREATE TABLE TONKHO (
    MaTonKho INT PRIMARY KEY IDENTITY(1,1),
    MaNuocUong INT,
    SoLuongTon INT,
    FOREIGN KEY (MaNuocUong) REFERENCES NUOCUONG(MaNuocUong)
);


-- Bảng XUẤT KHO NƯỚC UỐNG
CREATE TABLE XuatKhoNuocUong (
    MaXuatKho INT PRIMARY KEY IDENTITY(1,1),
    MaNuocUong INT,
    TenNuocUong NVARCHAR(100),
    SoLuong INT,
    NgayXuat DATE,
	MaHD INT,
	FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD),
    FOREIGN KEY (MaNuocUong) REFERENCES NUOCUONG(MaNuocUong)
);


-- Nếu đã có FK, phải drop trước
ALTER TABLE NHANVIEN DROP CONSTRAINT FK_NHANVIEN_TENDANGNHAP; -- Nếu có tên FK này (hoặc dùng SSMS để kiểm tra tên FK thật)

-- Xóa cột sai
ALTER TABLE NHANVIEN DROP COLUMN TenDangNhap;
SELECT fk.name AS ForeignKey,
       tp.name AS ParentTable,
       cp.name AS ParentColumn,
       tr.name AS ReferencedTable,
       cr.name AS ReferencedColumn
FROM sys.foreign_keys fk
JOIN sys.foreign_key_columns fkc ON fk.object_id = fkc.constraint_object_id
JOIN sys.tables tp ON fkc.parent_object_id = tp.object_id
JOIN sys.columns cp ON fkc.parent_object_id = cp.object_id AND fkc.parent_column_id = cp.column_id
JOIN sys.tables tr ON fkc.referenced_object_id = tr.object_id
JOIN sys.columns cr ON fkc.referenced_object_id = cr.object_id AND fkc.referenced_column_id = cr.column_id
WHERE tp.name = 'NHANVIEN';
ALTER TABLE NHANVIEN DROP CONSTRAINT FK__NHANVIEN__TenDan__47DBAE45;
ALTER TABLE NHANVIEN DROP CONSTRAINT UQ__NHANVIEN__55F68FC06B1BCF2E;
ALTER TABLE NHANVIEN DROP COLUMN TenDangNhap;
ALTER TABLE TAIKHOAN ADD MaNV INT;
ALTER TABLE TAIKHOAN
ADD CONSTRAINT FK_TAIKHOAN_NHANVIEN
FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV);

DELETE FROM TAIKHOAN;
INSERT INTO TAIKHOAN (TENDANGNHAP, MATKHAU, VAITRO, MaNV)
VALUES (N'hao', N'3', N'quan ly', 3);

 SELECT * FROM TAIKHOAN;

 ALTER TABLE TAIKHOAN

 SELECT MATKHAU FROM TAIKHOAN WHERE TENDANGNHAP = 'tên đăng nhập lỗi';
