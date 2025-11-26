package com.campusorg.app;

import com.campusorg.gui.MainFrame;
import javax.swing.SwingUtilities;

public class MainApp {
    public static void main(String[] args) {
        System.out.println("Aplikasi Campus Organization Manager sedang berjalan...");
        
        // Menjalankan GUI di Event Dispatch Thread (Standar Java Swing)
        SwingUtilities.invokeLater(() -> {
            try {
                // Mengatur tampilan agar mirip sistem operasi (Windows/Mac/Linux)
                javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

            MainFrame frame = new MainFrame();
            frame.setVisible(true); // Menampilkan jendela aplikasi
        });
    }
}