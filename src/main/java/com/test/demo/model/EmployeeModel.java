package com.test.demo.model;

public class EmployeeModel {
        private int maNhanVien;
        private String ho;
        private String ten;
        private String ngaySinh;
        private String chucVu;
        private double luong;

        public EmployeeModel(int maNhanVien, String ho, String ten, String ngaySinh, String chucVu, double luong) {
            this.maNhanVien = maNhanVien;
            this.ho = ho;
            this.ten = ten;
            this.ngaySinh = ngaySinh;
            this.chucVu = chucVu;
            this.luong = luong;
        }

        // Getters
        public int getMaNhanVien() { return maNhanVien; }
        public String getHo() { return ho; }
        public String getTen() { return ten; }
        public String getNgaySinh() { return ngaySinh; }
        public String getChucVu() { return chucVu; }
        public double getLuong() { return luong; }
    }
