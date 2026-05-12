import javax.swing.*;

import java.awt.*;

import model.Item;

import java.util.ArrayList;

import java.util.List;



public class ItemPanel extends JPanel {

    private List<Item> availableItems;



    public ItemPanel() {

        setLayout(new GridLayout(0, 1)); 

        availableItems = new ArrayList<>();



        availableItems.add(new Item("COMBO1", "Combo Bắp Rang + Pepsi", 75000, 0));

        availableItems.add(new Item("WA1", "Nước suối", 20000, 0));



        for (Item item : availableItems) {

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JLabel lblName = new JLabel(item.getName() + " - " + item.getPrice() + " VNĐ");

            JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));

            

 

            spinner.addChangeListener(e -> item.setQuantity((int) spinner.getValue()));



            row.add(lblName);

            row.add(spinner);

            add(row);

        }

    }



    public List<Item> getSelectedItems() {

        List<Item> selected = new ArrayList<>();

        for (Item item : availableItems) {

            if (item.getQuantity() > 0) selected.add(item);

        }

        return selected;

    }

}