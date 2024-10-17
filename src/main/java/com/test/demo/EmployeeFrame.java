package com.test.demo;

import javax.swing.*;
import java.awt.*;
import java.nio.ByteBuffer;
import java.sql.*;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.table.DefaultTableModel;

public class EmployeeFrame extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField keyField;
    private JComboBox<String> columnBox;

    public EmployeeFrame() {
        setTitle("Danh Sách Nhân Viên Với Mã Hóa");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"MaNhanVien", "Ho", "Ten", "NgaySinh", "ChucVu", "Luong", "Giải mã"}, 0);
        table = new JTable(tableModel);

        keyField = new JTextField(20);
        JLabel keyLabel = new JLabel("Khóa Mã Hóa:");

        String[] columns = {"Luong", "Ho", "Ten", "ChucVu"};
        columnBox = new JComboBox<>(columns);
        JLabel columnLabel = new JLabel("Chọn Cột Mã Hóa:");

        JButton loadButton = new JButton("Tải Dữ Liệu");
        loadButton.addActionListener(e -> loadEmployeeData());

        JButton encryptButton = new JButton("Mã Hóa");
        JButton decryptButton = new JButton("Giải Mã");

        encryptButton.addActionListener(e -> encryptColumn());
        decryptButton.addActionListener(e -> decryptColumn());

        JPanel panel = new JPanel();
        panel.add(keyLabel);
        panel.add(keyField);
        panel.add(columnLabel);
        panel.add(columnBox);
        panel.add(encryptButton);
        panel.add(decryptButton);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(loadButton, BorderLayout.SOUTH);
        add(panel, BorderLayout.NORTH);
    }

    private void loadEmployeeData() {
        String connectionUrl = "jdbc:mysql://127.0.0.1:3306/NHANVIEN";

        try (Connection conn = DriverManager.getConnection(connectionUrl, "root", "admin");
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT MaNhanVien, Ho, Ten, NgaySinh, ChucVu, Luong FROM NhanVien";
            ResultSet rs = stmt.executeQuery(sql);
            tableModel.setRowCount(0);

            while (rs.next()) {
                int maNhanVien = rs.getInt("MaNhanVien");
                String ho = rs.getString("Ho");
                String ten = rs.getString("Ten");
                String ngaySinh = rs.getString("NgaySinh");
                String chucVu = rs.getString("ChucVu");
                byte[] luongBytes = rs.getBytes("Luong");
                double luong = decryptSalary(luongBytes);

                tableModel.addRow(new Object[]{maNhanVien, ho, ten, ngaySinh, chucVu, luong, ""});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + ex.getMessage());
        }
    }

    private double decryptSalary(byte[] encryptedValue) {
        try {
            String key = keyField.getText();
            String initVector = "1234567890123456";

            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initVector.getBytes("UTF-8"));
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedValue);
            ByteBuffer wrapped = ByteBuffer.wrap(decryptedBytes);
            return wrapped.getDouble();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi giải mã lương: " + e.getMessage());
            return 0.0;
        }
    }

    private void encryptColumn() {
        String selectedColumn = columnBox.getSelectedItem().toString();
        String encryptionKey = keyField.getText();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object value = tableModel.getValueAt(i, getColumnIndex(selectedColumn));
            byte[] encryptedValue = encrypt(value.toString(), encryptionKey);
            tableModel.setValueAt(encryptedValue, i, getColumnIndex(selectedColumn));
            updateDatabase(i, selectedColumn, encryptedValue);
        }
    }

    private void decryptColumn() {
        String selectedColumn = columnBox.getSelectedItem().toString();
        String decryptionKey = keyField.getText();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object value = tableModel.getValueAt(i, getColumnIndex(selectedColumn));
            if (value instanceof byte[]) {
                byte[] decryptedValue = decrypt((byte[]) value, decryptionKey);
                if (decryptedValue != null) {
                    double decryptedSalary = ByteBuffer.wrap(decryptedValue).getDouble();
                    tableModel.setValueAt(decryptedSalary, i, getColumnIndex(selectedColumn));
                }
                updateDatabase(i, selectedColumn, decryptedValue);
            }
        }
    }

    private byte[] encrypt(String value, String key) {
        try {
            String initVector = "1234567890123456";
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initVector.getBytes("UTF-8"));
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

            byte[] inputBytes = ByteBuffer.allocate(8).putDouble(Double.parseDouble(value)).array();
            return cipher.doFinal(inputBytes);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi mã hóa: " + e.getMessage());
            return null;
        }
    }

    private byte[] decrypt(byte[] value, String key) {
        try {
            String initVector = "1234567890123456";
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initVector.getBytes("UTF-8"));
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return cipher.doFinal(value);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi giải mã: " + e.getMessage());
            return null;
        }
    }

    private void updateDatabase(int rowIndex, String columnName, byte[] value) {
        String connectionUrl = "jdbc:mysql://127.0.0.1:3306/NHANVIEN";

        try (Connection conn = DriverManager.getConnection(connectionUrl, "root", "admin");
             PreparedStatement pstmt = conn.prepareStatement("UPDATE NhanVien SET " + columnName + " = ? WHERE MaNhanVien = ?")) {
            pstmt.setBytes(1, value);
            pstmt.setInt(2, (int) tableModel.getValueAt(rowIndex, 0));
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật cơ sở dữ liệu: " + ex.getMessage());
        }
    }

    private int getColumnIndex(String columnName) {
        switch (columnName) {
            case "Luong": return 5;
            case "Ho": return 1;
            case "Ten": return 2;
            case "ChucVu": return 4;
            default: return -1;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmployeeFrame frame = new EmployeeFrame();
            frame.setVisible(true);
        });
    }
}
