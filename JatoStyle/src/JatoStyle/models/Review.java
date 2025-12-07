/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package JatoStyle.models;

import java.util.Date;

public class Review {
    private int idReview;
    private int idUser;
    private int idToko;
    private int rating;
    private String komentar;
    private Date tanggalReview;
    private Date lastEdited;
    private String namaUser; //display

    public Review() {}

    // Constructor, getters, setters
    public int getIdReview() { return idReview; }
    public void setIdReview(int idReview) { this.idReview = idReview; }
    
    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
    
    public int getIdToko() { return idToko; }
    public void setIdToko(int idToko) { this.idToko = idToko; }
    
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    
    public String getKomentar() { return komentar; }
    public void setKomentar(String komentar) { this.komentar = komentar; }
    
    public Date getTanggalReview() { return tanggalReview; }
    public void setTanggalReview(Date tanggalReview) { this.tanggalReview = tanggalReview; }
    
    public Date getLastEdited() { return lastEdited; }
    public void setLastEdited(Date lastEdited) { this.lastEdited = lastEdited; }
    
    public String getNamaUser() { return namaUser; }
    public void setNamaUser(String namaUser) { this.namaUser = namaUser; }
}