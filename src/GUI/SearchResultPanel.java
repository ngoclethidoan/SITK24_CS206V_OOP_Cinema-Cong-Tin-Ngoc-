/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import model.Film;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * SearchResultPanel – hiển thị lưới phim kết quả tìm kiếm,
 * tương tự HOME nhưng chỉ show các film khớp query.
 */
public class SearchResultPanel extends JPanel {

    public SearchResultPanel(List<Film> results, String query, MainFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(new Color(19, 19, 19));

        // ── Top bar ───────────────────────────────────────────────────
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topBar.setBackground(new Color(19, 19, 19));

        JButton backBtn = new JButton("⬅ " + LanguageManager.t(LanguageManager.BTN_BACK));
        backBtn.setBackground(new Color(50, 50, 50));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> mainFrame.showHome());

        JLabel title = new JLabel("🔍  \"" + query + "\"  —  " + results.size() + " result(s)");
        title.setForeground(new Color(180, 180, 180));
        title.setFont(new Font("Dialog", Font.PLAIN, 14));

        topBar.add(backBtn);
        topBar.add(title);
        add(topBar, BorderLayout.NORTH);

        // ── Grid ──────────────────────────────────────────────────────
        if (results.isEmpty()) {
            JLabel none = new JLabel(LanguageManager.t(LanguageManager.SEARCH_NO_RESULTS), SwingConstants.CENTER);
            none.setForeground(new Color(120, 120, 120));
            none.setFont(new Font("Dialog", Font.ITALIC, 18));
            add(none, BorderLayout.CENTER);
        } else {
            JPanel grid = new JPanel(new GridLayout(0, 3, 25, 25));
            grid.setBackground(new Color(19, 19, 19));
            grid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            for (Film film : results) {
                grid.add(createCard(film, mainFrame));
            }

            JScrollPane sp = new JScrollPane(grid);
            sp.setBorder(null);
            sp.getViewport().setBackground(new Color(19, 19, 19));
            add(sp, BorderLayout.CENTER);
        }
    }

    private JPanel createCard(Film film, MainFrame mainFrame) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(30, 30, 30));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel pic = new JLabel();
        pic.setAlignmentX(Component.CENTER_ALIGNMENT);
        File f = new File(film.getImagePath());
        if (f.exists()) {
            Image img = new ImageIcon(film.getImagePath()).getImage()
                            .getScaledInstance(220, 310, Image.SCALE_SMOOTH);
            pic.setIcon(new ImageIcon(img));
        } else {
            pic.setText("No Image");
            pic.setForeground(Color.GRAY);
        }

        JLabel name = new JLabel(film.getTitle());
        name.setForeground(Color.WHITE);
        name.setFont(new Font("Dialog", Font.PLAIN, 13));
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(pic);
        card.add(name);
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                mainFrame.showFilmDetail(film);
            }
        });
        return card;
    }
}