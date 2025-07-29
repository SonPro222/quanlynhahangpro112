/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package ui.manager;

import dao.NhanVienDAO;
import dao.TaiKhoanDAO;
import dao.impl.NhanVienDAOImpl;
import dao.impl.TaiKhoanDAOImpl;
import entity.TaiKhoan;
import java.awt.Color;
import java.awt.Component;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author ACER
 */
public class QuanLyTaiKhoan extends javax.swing.JDialog {

    /**
     * Creates new form QuanLyTaiKhoan
     */
    public QuanLyTaiKhoan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        fillToTable();
        setTitle("Quản Lý Tài Khoản");
//        setSize(1000, 600);            // <-- kích thước mong muốn
        setResizable(false);          // <-- không cho kéo giãn
        setLocationRelativeTo(null);
        chkManager1.setOpaque(false);
        chkManager1.setContentAreaFilled(false);
        chkManager.setOpaque(false);
        chkManager.setContentAreaFilled(false);
//       jLabel7.setOpaque(false);
//       jLabel7.setContentAreaFilled(false);
        chkShowPassword.setOpaque(false);
        chkShowPassword.setContentAreaFilled(false);

        Color customColor = new Color(180, 180, 180);
        tblTaiKhoan.setBackground(customColor);
        tblTaiKhoan.getTableHeader().setBackground(customColor);

    }

    public void fillToTable() {
        DefaultTableModel model = (DefaultTableModel) tblTaiKhoan.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ
        TaiKhoanDAO dao = new TaiKhoanDAOImpl();

        List<TaiKhoan> list = dao.findAll(); // lấy danh sách user từ DB
        for (TaiKhoan user : list) {
            String maskedPassword = "*".repeat(user.getMatkhau().length()); // che mật khẩu
            model.addRow(new Object[]{
                user.getTendangnhap(),
                maskedPassword,
                user.getVaitro(),
                user.getMaNV()// Thêm cột quyền
            });
        }

        // Cột 2 (mật khẩu) luôn hiển thị dấu * (nếu bạn muốn bảo vệ hiển thị)
        tblTaiKhoan.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value != null) {
                    setText(value.toString()); // giá trị đã được che từ trước
                } else {
                    setText("");
                }
            }
        });
    }

    public void addAccount() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String confirm = new String(txtConfirmPassword.getPassword()).trim();
        String maNVStr = txtMaNV.getText().trim(); // Giả sử có ô nhập mã NV

        // Kiểm tra trống các trường nhập
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty() || maNVStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // Kiểm tra vai trò
        if (!chkManager.isSelected() && !chkManager1.isSelected()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn vai trò (quyền)!");
            return;
        }

        // Kiểm tra mật khẩu khớp
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu và xác nhận không khớp!");
            return;
        }

        // Kiểm tra username đã tồn tại
        TaiKhoanDAO userDAO = new TaiKhoanDAOImpl();
        if (userDAO.exists(username)) {
            JOptionPane.showMessageDialog(this, "Tài khoản đã tồn tại!");
            return;
        }

        // Parse mã nhân viên
        int maNV;
        try {
            maNV = Integer.parseInt(maNVStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mã nhân viên phải là số!");
            return;
        }

        // Kiểm tra mã nhân viên có tồn tại và đã có tài khoản
        NhanVienDAO nvDAO = new NhanVienDAOImpl();
        if (nvDAO.findById(maNV) == null) {
            JOptionPane.showMessageDialog(this, "Mã nhân viên không tồn tại!");
            return;
        }

        // Kiểm tra xem nhân viên đã có tài khoản chưa
        if (userDAO.existsByMaNV(maNV)) {
            JOptionPane.showMessageDialog(this, "Nhân viên này đã có tài khoản!");
            return;
        }

        // Băm mật khẩu trước khi lưu vào cơ sở dữ liệu
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Gán vai trò
        String role = chkManager.isSelected() ? "Quản lý" : "Nhân viên";

        // Tạo đối tượng User
        TaiKhoan user = TaiKhoan.builder()
                .tendangnhap(username)
                .matkhau(hashedPassword) // Lưu mật khẩu đã được băm
                .vaitro(role)
                .maNV(maNV) // Gán mã nhân viên
                .build();

        userDAO.create(user);
        JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!");
        fillToTable(); // Cập nhật lại bảng nếu có
    }

    public void XoaUser() {
        int row = tblTaiKhoan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần xóa!");
            return;
        }

        String username = tblTaiKhoan.getValueAt(row, 0).toString(); // Cột 0 là TENDANGNHAP

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa tài khoản: " + username + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            TaiKhoanDAO dao = new TaiKhoanDAOImpl();
            dao.deleteById(username);
            JOptionPane.showMessageDialog(this, "Đã xóa tài khoản thành công!");

            fillToTable(); // load lại bảng sau khi xóa
        }
    }

    private void datLaiMatKhau() {
        String username = txtUsername2.getText().trim();
        String maNVText = txtMaNV2.getText().trim();
        String passMoi = txtPassword2.getText().trim();
        String xacNhan = txtConfirmPassword2.getText().trim();

        if (username.isEmpty() || maNVText.isEmpty() || passMoi.isEmpty() || xacNhan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không được để trống");
            return;
        }

        int maNV;
        try {
            maNV = Integer.parseInt(maNVText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mã nhân viên không hợp lệ");
            return;
        }

        TaiKhoanDAOImpl dao = new TaiKhoanDAOImpl();
        boolean hopLe = dao.kiemTraUsernameVaMaNV(username, maNV);

        if (!hopLe) {
            JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mã nhân viên");
            return;
        }

        if (!passMoi.equals(xacNhan)) {
            JOptionPane.showMessageDialog(this, "Xác nhận mật khẩu không khớp");
            return;
        }

        // Nếu cần mã hóa mật khẩu bằng BCrypt
        String hash = BCrypt.hashpw(passMoi, BCrypt.gensalt());

        TaiKhoan user = dao.findById(username);
        if (user != null) {
            user.setMatkhau(hash);
            dao.update(user);
            JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thành công");
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        group = new javax.swing.ButtonGroup();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        jLabel6 = new javax.swing.JLabel();
        txtConfirmPassword = new javax.swing.JPasswordField();
        jLabel8 = new javax.swing.JLabel();
        chkManager = new javax.swing.JCheckBox();
        chkManager1 = new javax.swing.JCheckBox();
        chkShowPassword = new javax.swing.JCheckBox();
        jButton2 = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTaiKhoan = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txtMaNV = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtUsername2 = new javax.swing.JTextField();
        txtPassword2 = new javax.swing.JPasswordField();
        jLabel16 = new javax.swing.JLabel();
        txtConfirmPassword2 = new javax.swing.JPasswordField();
        jLabel17 = new javax.swing.JLabel();
        txtMaNV2 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        btnClear1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(204, 204, 204));
        jLabel3.setText("Quên Mật Khẩu");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, 130, 24));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(204, 204, 204));
        jLabel4.setText("Nhập Tên Tài Khoản");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 40, 164, 24));

        txtUsername.setBackground(new java.awt.Color(102, 102, 102));
        txtUsername.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtUsername.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(txtUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 70, 160, 25));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(204, 204, 204));
        jLabel5.setText("Nhập Mật Khẩu");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 100, 164, 24));

        txtPassword.setBackground(new java.awt.Color(102, 102, 102));
        txtPassword.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtPassword.setForeground(new java.awt.Color(255, 255, 255));
        txtPassword.setMinimumSize(new java.awt.Dimension(69, 29));
        txtPassword.setPreferredSize(new java.awt.Dimension(70, 25));
        getContentPane().add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 130, 160, 25));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(204, 204, 204));
        jLabel6.setText("Xác Nhận Mật Khẩu");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 160, 164, 24));

        txtConfirmPassword.setBackground(new java.awt.Color(102, 102, 102));
        txtConfirmPassword.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtConfirmPassword.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(txtConfirmPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 190, 160, 25));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(204, 204, 204));
        jLabel8.setText("Mã Nhân Viên");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 220, 164, 24));

        group.add(chkManager);
        chkManager.setForeground(new java.awt.Color(204, 204, 204));
        chkManager.setText("Quản Lý");
        chkManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkManagerActionPerformed(evt);
            }
        });
        getContentPane().add(chkManager, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 280, 70, -1));

        group.add(chkManager1);
        chkManager1.setForeground(new java.awt.Color(204, 204, 204));
        chkManager1.setText("Nhân Viên");
        getContentPane().add(chkManager1, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 300, 101, -1));

        chkShowPassword.setForeground(new java.awt.Color(204, 204, 204));
        chkShowPassword.setText("Hiển Thị Mật Khẩu");
        chkShowPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowPasswordActionPerformed(evt);
            }
        });
        getContentPane().add(chkShowPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 290, -1, -1));

        jButton2.setBackground(new java.awt.Color(102, 102, 102));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(204, 204, 204));
        jButton2.setText("Đăng Ký");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 330, 130, -1));

        btnClear.setBackground(new java.awt.Color(102, 102, 102));
        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnClear.setForeground(new java.awt.Color(204, 204, 204));
        btnClear.setText("Làm Mới");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        getContentPane().add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 370, 130, -1));

        tblTaiKhoan.setBackground(new java.awt.Color(102, 102, 102));
        tblTaiKhoan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tblTaiKhoan.setForeground(new java.awt.Color(0, 0, 0));
        tblTaiKhoan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Tên Tài Khoản", "Mật Khẩu", "Quyền", "Mã NV"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblTaiKhoan);
        if (tblTaiKhoan.getColumnModel().getColumnCount() > 0) {
            tblTaiKhoan.getColumnModel().getColumn(2).setMinWidth(80);
            tblTaiKhoan.getColumnModel().getColumn(2).setMaxWidth(80);
            tblTaiKhoan.getColumnModel().getColumn(3).setMinWidth(50);
            tblTaiKhoan.getColumnModel().getColumn(3).setMaxWidth(50);
        }

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 410, 360, 100));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(204, 204, 204));
        jLabel2.setText("Danh Sách Tài Khoản");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 380, 210, 24));

        txtMaNV.setBackground(new java.awt.Color(102, 102, 102));
        txtMaNV.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtMaNV.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(txtMaNV, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 250, 160, 25));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(204, 204, 204));
        jLabel9.setText("Đăng ký tài khoản");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 10, 180, 24));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(204, 204, 204));
        jLabel14.setText("Nhập Tên Tài Khoản");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 40, 164, 24));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(204, 204, 204));
        jLabel15.setText("Nhập Mật Khẩu Mới");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 160, 164, 24));

        txtUsername2.setBackground(new java.awt.Color(102, 102, 102));
        txtUsername2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtUsername2.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(txtUsername2, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 70, 160, 25));

        txtPassword2.setBackground(new java.awt.Color(102, 102, 102));
        txtPassword2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtPassword2.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(txtPassword2, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 190, 160, 25));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(204, 204, 204));
        jLabel16.setText("Xác Nhận Mật Khẩu");
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 220, 164, 24));

        txtConfirmPassword2.setBackground(new java.awt.Color(102, 102, 102));
        txtConfirmPassword2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtConfirmPassword2.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(txtConfirmPassword2, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 250, 160, 25));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(204, 204, 204));
        jLabel17.setText("Mã Nhân Viên");
        getContentPane().add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 100, 164, 24));

        txtMaNV2.setBackground(new java.awt.Color(102, 102, 102));
        txtMaNV2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtMaNV2.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(txtMaNV2, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 130, 160, 25));

        jButton5.setBackground(new java.awt.Color(102, 102, 102));
        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(204, 204, 204));
        jButton5.setText("Đặt Lại Mật Khẩu");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 330, 160, -1));

        btnClear1.setBackground(new java.awt.Color(102, 102, 102));
        btnClear1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnClear1.setForeground(new java.awt.Color(204, 204, 204));
        btnClear1.setText("Xóa Tài Khoản");
        btnClear1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClear1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnClear1, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 520, 170, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/quanlytaikhoan (2).png"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1040, 560));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkShowPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowPasswordActionPerformed
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
                txtConfirmPassword.setEchoChar((char) 0);
                txtPassword2.setEchoChar((char) 0);
                txtConfirmPassword2.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('*');
                txtConfirmPassword.setEchoChar('*');
                txtPassword2.setEchoChar('*');
                txtConfirmPassword2.setEchoChar('*');
            }
        });
    }//GEN-LAST:event_chkShowPasswordActionPerformed
    void clear() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        txtMaNV.setText("");
        txtUsername2.setText("");
        txtPassword2.setText("");
        txtConfirmPassword2.setText("");
        txtMaNV2.setText("");
    }
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        addAccount();    // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void chkManagerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkManagerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkManagerActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        datLaiMatKhau();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void btnClear1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClear1ActionPerformed
        XoaUser();  // TODO add your handling code here:
    }//GEN-LAST:event_btnClear1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnClear1;
    private javax.swing.JCheckBox chkManager;
    private javax.swing.JCheckBox chkManager1;
    private javax.swing.JCheckBox chkShowPassword;
    private javax.swing.ButtonGroup group;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblTaiKhoan;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JPasswordField txtConfirmPassword2;
    private javax.swing.JTextField txtMaNV;
    private javax.swing.JTextField txtMaNV2;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JPasswordField txtPassword2;
    private javax.swing.JTextField txtUsername;
    private javax.swing.JTextField txtUsername2;
    // End of variables declaration//GEN-END:variables
}
