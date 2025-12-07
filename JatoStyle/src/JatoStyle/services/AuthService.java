/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JatoStyle.services;

import JatoStyle.Konektor;
import JatoStyle.models.User;
import JatoStyle.models.Admin;
import JatoStyle.models.Toko;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AuthService {
    private Konektor konektor;
    
    public AuthService() {
        konektor = new Konektor();
    }
    
    // validasi email 
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.contains("@") && email.contains(".") && email.length() >= 5;
    }
    
    // register user 
    public boolean registerUser(User user) {
        try {
            System.out.println("=== START REGISTER ===");
            System.out.println("Nama: " + user.getNama());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Password: " + user.getPassword());
            System.out.println("No HP: " + user.getNoHp());
            
            // validasi email sebelum register
            if (!isValidEmail(user.getEmail())) {
                System.out.println("Register Error: Format email tidak valid");
                return false;
            }
            
            // cek apakah email sudah terdaftar
            if (isEmailExists(user.getEmail(), "user")) {
                System.out.println("Register Error: Email sudah terdaftar");
                return false;
            }
            
            // hash password sebelum disimpan
            String hashedPassword = PasswordHasher.hashPassword(user.getPassword());
            System.out.println("Password (hashed): " + hashedPassword.substring(0, 20) + "...");
            
            // escape single quotes utk cegah SQL error
            String nama = user.getNama().replace("'", "''");
            String email = user.getEmail().replace("'", "''");
            String noHp = user.getNoHp() != null ? user.getNoHp().replace("'", "''") : "";
            
            String sql = String.format(
                "INSERT INTO user (nama, email, password, no_hp) VALUES ('%s', '%s', '%s', '%s')",
                nama, email, hashedPassword, noHp
            );
            
            System.out.println("Executing SQL: " + sql);
            
            konektor.query(sql);
            System.out.println("Register SUCCESS!");
            return true;
            
        } catch (Exception e) {
            System.out.println("Register ERROR: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // login user 
    public User loginUser(String email, String password) {
        try {
            System.out.println("=== START LOGIN ===");
            System.out.println("Email: " + email);
            System.out.println("Password (plain): " + password);
            
            // validasi email sebelum login
            if (!isValidEmail(email)) {
                System.out.println("Login Error: Format email tidak valid");
                return null;
            }
            
            // ambil data user berdasarkan email (tanpa filter password)
            String sql = String.format(
                "SELECT * FROM user WHERE email = '%s'",
                email.replace("'", "''")
            );
            
            System.out.println("Login SQL: " + sql);
            ResultSet rs = konektor.getData(sql);
            
            if (rs.next()) {
                // ambil password hash dari database
                String storedHash = rs.getString("password");
                System.out.println("Stored hash: " + storedHash.substring(0, 20) + "...");
                
                // verifikasi password
                if (PasswordHasher.verifyPassword(password, storedHash)) {
                    User user = new User();
                    user.setIdUser(rs.getInt("id_user"));
                    user.setNama(rs.getString("nama"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(storedHash); // Simpan hash, bukan plain text
                    user.setNoHp(rs.getString("no_hp"));
                    System.out.println("Login SUCCESS! User: " + user.getNama());
                    return user;
                } else {
                    System.out.println("Login FAILED: Password salah");
                }
            } else {
                System.out.println("Login FAILED: Email tidak ditemukan");
            }
        } catch (Exception e) {
            System.out.println("Login ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // login admin 
    public Admin loginAdmin(String email, String password) {
        try {
            // validasi email sebelum login
            if (!isValidEmail(email)) {
                System.out.println("Admin Login Error: Format email tidak valid");
                return null;
            }
            
            String sql = String.format(
                "SELECT * FROM admin WHERE email = '%s'",
                email.replace("'", "''")
            );
            ResultSet rs = konektor.getData(sql);
            
            if (rs.next()) {
                String storedHash = rs.getString("password");
                System.out.println("Admin stored hash: " + storedHash.substring(0, 20) + "...");
                
                // verifikasi password
                if (PasswordHasher.verifyPassword(password, storedHash)) {
                    Admin admin = new Admin();
                    admin.setIdAdmin(rs.getInt("id_admin"));
                    admin.setNamaAdmin(rs.getString("nama_admin"));
                    admin.setEmail(rs.getString("email"));
                    admin.setPassword(storedHash);
                    System.out.println("Admin Login SUCCESS!");
                    return admin;
                } else {
                    System.out.println("Admin Login FAILED: Password salah");
                }
            } else {
                System.out.println("Admin Login FAILED: Email tidak ditemukan");
            }
        } catch (Exception e) {
            System.out.println("Admin Login Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // login toko
    public Toko loginToko(String email, String password) {
        try {
            // validasi email sebelum login
            if (!isValidEmail(email)) {
                System.out.println("Toko Login Error: Format email tidak valid");
                return null;
            }
            
            String sql = String.format(
                "SELECT * FROM toko WHERE email = '%s'",
                email.replace("'", "''")
            );
            ResultSet rs = konektor.getData(sql);
            
            if (rs.next()) {
                String storedHash = rs.getString("password");
                System.out.println("Toko stored hash: " + storedHash.substring(0, 20) + "...");
                
                // verifikasi password
                if (PasswordHasher.verifyPassword(password, storedHash)) {
                    Toko toko = new Toko();
                    toko.setIdToko(rs.getInt("id_toko"));
                    toko.setIdAdmin(rs.getInt("id_admin"));
                    toko.setNamaToko(rs.getString("nama_toko"));
                    toko.setEmail(rs.getString("email"));
                    toko.setPassword(storedHash);
                    toko.setJamBuka(rs.getTime("jam_buka"));
                    toko.setJamTutup(rs.getTime("jam_tutup"));
                    System.out.println("Toko Login SUCCESS!");
                    return toko;
                } else {
                    System.out.println("Toko Login FAILED: Password salah");
                }
            } else {
                System.out.println("Toko Login FAILED: Email tidak ditemukan");
            }
        } catch (Exception e) {
            System.out.println("Toko Login Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // cek kalau email sudah terdaftar
    public boolean isEmailExists(String email, String table) {
        try {
            // validasi email sebelum cek
            if (!isValidEmail(email)) {
                System.out.println("Email Check Error: Format email tidak valid");
                return false;
            }
            
            String sql = String.format("SELECT * FROM %s WHERE email = '%s'", table, email.replace("'", "''"));
            ResultSet rs = konektor.getData(sql);
            boolean exists = rs.next();
            System.out.println("Email check: " + email + " exists: " + exists);
            return exists;
        } catch (Exception e) {
            System.out.println("Email Check Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // get semua toko
    public List<Toko> getAllToko() {
        List<Toko> tokoList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM toko ORDER BY nama_toko";
            ResultSet rs = konektor.getData(sql);
            
            while (rs.next()) {
                Toko toko = new Toko();
                toko.setIdToko(rs.getInt("id_toko"));
                toko.setNamaToko(rs.getString("nama_toko"));
                toko.setEmail(rs.getString("email"));
                toko.setJamBuka(rs.getTime("jam_buka"));
                toko.setJamTutup(rs.getTime("jam_tutup"));
                tokoList.add(toko);
            }
            
            System.out.println("Loaded " + tokoList.size() + " toko from database");
            
        } catch (Exception e) {
            System.out.println("Error getting toko: " + e.getMessage());
            e.printStackTrace();
        }
        return tokoList;
    }
    
    //Update Profile
    public boolean updateProfileUser(User user) {
        try {
            System.out.println("=== START UPDATE PROFILE ===");
            if (user.getIdUser() <= 0) {
                System.out.println("Update Profile Error: ID User tidak valid.");
                return false;
            }
            
            // Validasi format email yang baru
            if (!isValidEmail(user.getEmail())) {
                System.out.println("Update Profile Error: Format email baru tidak valid.");
                return false;
            }
            
            // Cek apakah email baru sudah terdaftar oleh user lain (jika email berubah)
            User existingUser = getUserByEmail(user.getEmail());
            if (existingUser != null && existingUser.getIdUser() != user.getIdUser()) {
                System.out.println("Update Profile Error: Email baru sudah digunakan oleh pengguna lain.");
                return false;
            }
            
            // Escape single quotes
            String nama = user.getNama().replace("'", "''");
            String email = user.getEmail().replace("'", "''");
            String noHp = user.getNoHp() != null ? user.getNoHp().replace("'", "''") : "";
            
            String sql = String.format(
                "UPDATE user SET nama = '%s', email = '%s', no_hp = '%s' WHERE id_user = %d",
                nama, email, noHp, user.getIdUser()
            );
            
            System.out.println("Executing Update SQL: " + sql);
            
            konektor.query(sql);
            System.out.println("Update Profile SUCCESS for user ID: " + user.getIdUser());
            return true;
            
        } catch (Exception e) {
            System.out.println("Update Profile ERROR: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public User getUserByEmail(String email) {
        try {
            String sql = String.format("SELECT * FROM user WHERE email = '%s'", email.replace("'", "''"));
            ResultSet rs = konektor.getData(sql);
            
            if (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("id_user"));
                user.setNama(rs.getString("nama"));
                user.setEmail(rs.getString("email"));
                user.setNoHp(rs.getString("no_hp"));
                user.setPassword(rs.getString("password")); // Ambil password juga
                return user;
            }
        } catch (Exception e) {
            System.out.println("Get User By Email Error: " + e.getMessage());
        }
        return null;
    }
    
    
     public Konektor getKonektor() {
        return konektor;
    }
}