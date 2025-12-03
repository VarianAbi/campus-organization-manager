package com.campusorg.app;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

import com.campusorg.gui.MainFrame;
import com.campusorg.patterns.singleton.OrgManager;

public class MainApp {
    private static final Logger logger = Logger.getLogger(MainApp.class.getName());

    public static void main(String[] args) {
        logger.info("Aplikasi Campus Organization Manager sedang berjalan...");

        // Menjalankan GUI di Event Dispatch Thread (Standar Java Swing)
        SwingUtilities.invokeLater(() -> {
            try {
                // Mengatur tampilan agar mirip sistem operasi (Windows/Mac/Linux)
                javax.swing.UIManager.setLookAndFeel(
                        javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException
                    | UnsupportedLookAndFeelException _) {
                // UI look and feel could not be set, using default
            }

            MainFrame frame = new MainFrame();

            // ========== AUTO-SAVE SAAT CLOSE WINDOW ==========
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    logger.info("Menyimpan data ke JSON...");
                    OrgManager.getInstance().saveData();
                    logger.info("Data berhasil disimpan. Program ditutup.");
                }
            });

            frame.setVisible(true); // Menampilkan jendela aplikasi
        });
    }
}