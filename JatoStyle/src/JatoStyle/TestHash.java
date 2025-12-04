/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JatoStyle;
import JatoStyle.services.PasswordHasher;

/**
 *
 * @author Lenovo
 */
public class TestHash {
    public static void main(String[] args) {
        String password = "admin123"; 
        String hash = PasswordHasher.hashPassword(password);
        System.out.println("HASH = " + hash);
    }
}
