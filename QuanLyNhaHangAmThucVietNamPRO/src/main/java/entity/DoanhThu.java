package entity;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoanhThu {
    private int maDT;
    private Date ngay;
    private double tongThu;
    private double tongChi;
    private String ghiChu;
    private String TENDANGNHAP;

    public double getLoiNhuan() { return tongThu - tongChi; }
    
public class Auth {
    public static NhanVien user = null;
}



   
}
