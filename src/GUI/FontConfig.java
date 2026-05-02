package GUI;

import javax.swing.*;
import java.awt.*;

public class FontConfig {

    public static void init() {

        Font font = new Font("SansSerif", Font.PLAIN, 13);

        UIManager.put("defaultFont", font);

        UIManager.put("Button.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("PasswordField.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("CheckBox.font", font);
        UIManager.put("RadioButton.font", font);
        UIManager.put("List.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("MenuItem.font", font);
    }
}