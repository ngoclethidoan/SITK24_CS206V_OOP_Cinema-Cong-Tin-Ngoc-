package cinema;

import GUI.MainFrame;
import database.BookingDatabase;
import database.FilmDatabase;
import database.ItemDatabase;
import database.RoomDatabase;
import database.VoucherDatabase;
import javax.swing.SwingUtilities;

class CinemaRun {
    public static void main(String[] args) {
        // Cross-platform font: uu tien Segoe UI (Windows), fallback Dialog (macOS/Linux)
        // Dam bao hien thi tieng Viet dung tren ca hai OS
        String fontName = "Dialog";
        java.awt.Font[] allFonts = java.awt.GraphicsEnvironment
            .getLocalGraphicsEnvironment().getAllFonts();
        for (java.awt.Font ff : allFonts) {
            if (ff.getName().equalsIgnoreCase("Segoe UI")) { fontName = "Segoe UI"; break; }
        }
        final java.awt.Font globalFont = new java.awt.Font(fontName, java.awt.Font.PLAIN, 13);
        javax.swing.UIManager.put("Button.font",        globalFont);
        javax.swing.UIManager.put("Label.font",         globalFont);
        javax.swing.UIManager.put("TextField.font",     globalFont);
        javax.swing.UIManager.put("TextArea.font",      globalFont);
        javax.swing.UIManager.put("PasswordField.font", globalFont);
        javax.swing.UIManager.put("ComboBox.font",      globalFont);
        javax.swing.UIManager.put("CheckBox.font",      globalFont);
        javax.swing.UIManager.put("RadioButton.font",   globalFont);
        javax.swing.UIManager.put("List.font",          globalFont);
        javax.swing.UIManager.put("Menu.font",          globalFont);
        javax.swing.UIManager.put("MenuItem.font",      globalFont);

        FilmDatabase.initDatabase();
        RoomDatabase.init();
        ItemDatabase.initDatabase();
        VoucherDatabase.init(); 
        BookingDatabase.restoreBookedSeats();
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
