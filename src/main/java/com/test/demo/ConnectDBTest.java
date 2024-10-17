package com.test.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Statement;
import java.sql.ResultSet;


public class ConnectDBTest {

    public static void main(String[] args) {
        String connectionUrl = "jdbc:mysql://127.0.0.1:3306/NHANVIEN";

        try (Connection conn = DriverManager.getConnection(connectionUrl,"root", "admin");
             Statement stmt = conn.createStatement()) {

            System.out.println("Ket noi thanh cong den SQL Server!");
            System.out.println("Ten co so du lieu: " + conn.getCatalog());

            // Kiểm tra nếu bảng NhanVien tồn tại trước khi truy vấn
            String sql = "SELECT * FROM NhanVien";
            System.out.println("Dang thuc hien truy van: " + sql);

            // Thực hiện truy vấn SQL để lấy dữ liệu từ bảng NhanVien
            ResultSet rs = stmt.executeQuery(sql);

            // Hiển thị kết quả
            while (rs.next()) {
                int maNhanVien = rs.getInt("MaNhanVien");
                String ho = rs.getString("Ho");
                String ten = rs.getString("Ten");
                String ngaySinh = rs.getString("NgaySinh");
                String chucVu = rs.getString("ChucVu");
                double luong = rs.getDouble("Luong");

                System.out.println("MaNhanVien: " + maNhanVien + ", Ho: " + ho + ", Ten: " + ten +
                        ", NgaySinh: " + ngaySinh + ", ChucVu: " + chucVu + ", Luong: " + luong);
            }

        } catch (SQLException ex) {
            ex.printStackTrace(); // In lỗi chi tiết
        }
    }
}
