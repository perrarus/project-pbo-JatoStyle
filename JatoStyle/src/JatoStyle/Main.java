/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JatoStyle;

/**
 *
 * @author Aisha Sahwa Bagas
 */
import JatoStyle.gui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Food Order Application...");
        
        // start gui
        java.awt.EventQueue.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
