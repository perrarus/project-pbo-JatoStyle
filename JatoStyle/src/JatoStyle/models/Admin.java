/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JatoStyle.models;


public class Admin {
    private int idAdmin;
    private String namaAdmin;
    private String email;
    private String password;
    
    public Admin() {}
    
    // setter getter
    public int getIdAdmin() { return idAdmin; }
    public void setIdAdmin(int idAdmin) { this.idAdmin = idAdmin; }
    public String getNamaAdmin() { return namaAdmin; }
    public void setNamaAdmin(String namaAdmin) { this.namaAdmin = namaAdmin; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
