/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JatoStyle;

import java.sql.ResultSet;

/**
 *
 * @author Lenovo
 */
public class TestFoodOrder {
    public static void main(String[] args) {
        Konektor conn = new Konektor();  // buat objek koneksi

        conn.query("INSERT INTO admin (nama_admin, email, password) " +
                   "VALUES ('Admin Utama', 'admin@mail.com', '1234')");

        conn.query("INSERT INTO user (nama, email, password, no_hp) " +
                   "VALUES ('Fitri', 'fitri@mail.com', '12345', '0812345678')");

        ResultSet rs = conn.getData("SELECT * FROM user");
        try {
            while (rs.next()) {
                System.out.println("ID User: " + rs.getInt("id_user"));
                System.out.println("Nama: " + rs.getString("nama"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("--------------------------");
            }
        } catch (Exception e) {
            System.out.println("Error membaca data: " + e.getMessage());
        }
    }
}