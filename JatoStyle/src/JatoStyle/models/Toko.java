/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JatoStyle.models;

import java.sql.Time;

public class Toko {
    private int idToko;
    private int idAdmin;
    private String namaToko;
    private String email;
    private String password;
    private Time jamBuka;
    private Time jamTutup;
    
    public Toko() {}
    
    // setter getter
    public int getIdToko() { return idToko; }
    public void setIdToko(int idToko) { this.idToko = idToko; }
    public int getIdAdmin() { return idAdmin; }
    public void setIdAdmin(int idAdmin) { this.idAdmin = idAdmin; }
    public String getNamaToko() { return namaToko; }
    public void setNamaToko(String namaToko) { this.namaToko = namaToko; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Time getJamBuka() { return jamBuka; }
    public void setJamBuka(Time jamBuka) { this.jamBuka = jamBuka; }
    public Time getJamTutup() { return jamTutup; }
    public void setJamTutup(Time jamTutup) { this.jamTutup = jamTutup; }
}
