package com.campusorg.app;

import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

import com.campusorg.gui.MainFrame;

public class MainApp {
    private static final Logger logger = Logger.getLogger(MainApp.class.getName());
    
    public static void main(String[] args) {
        logger.info("Aplikasi Campus Organization Manager sedang berjalan...");
        
        // Menjalankan GUI di Event Dispatch Thread (Standar Java Swing)
        SwingUtilities.invokeLater(() -> {
            try {
                // Mengatur tampilan agar mirip sistem operasi (Windows/Mac/Linux)
                javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName()
                );
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException _) {
                // UI look and feel could not be set, using default
            }

            MainFrame frame = new MainFrame();
            frame.setVisible(true); // Menampilkan jendela aplikasi
        });
    }
}