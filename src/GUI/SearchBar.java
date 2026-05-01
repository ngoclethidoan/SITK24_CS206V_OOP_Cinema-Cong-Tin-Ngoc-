package GUI;

import database.FilmDatabase;
import model.Film;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SearchBar — Google-style search with live dropdown suggestions.
 *
 * Features:
 *  - Animated placeholder that fades when typing
 *  - Dropdown list appears below the bar as user types (≥ 1 char)
 *  - Each suggestion row shows poster thumbnail + title + director
 *  - Keyboard navigation: ↑ / ↓ to move, Enter to open, Esc to close
 *  - Clicking a suggestion calls onSelect callback
 */
public class SearchBar extends JPanel {

    // ── Colours ──────────────────────────────────────────────────────
    private static final Color BG           = new Color(30, 30, 30);
    private static final Color BG_FOCUS     = new Color(38, 38, 38);
    private static final Color BORDER_IDLE  = new Color(60, 60, 60);
    private static final Color BORDER_FOCUS = new Color(100, 149, 237);
    private static final Color TEXT_CLR     = Color.WHITE;
    private static final Color PLACEHOLDER_CLR = new Color(120, 120, 120);
    private static final Color DROP_BG      = new Color(28, 28, 28);
    private static final Color DROP_HOVER   = new Color(50, 50, 60);
    private static final Color DROP_SELECT  = new Color(40, 60, 100);
    private static final Color DROP_BORDER  = new Color(70, 70, 90);
    private static final Color SUBTITLE_CLR = new Color(150, 150, 150);

    // ── Components ───────────────────────────────────────────────────
    private final JTextField field;
    private final JButton    clearBtn;
    private final JWindow    dropdown;
    private final JPanel     dropPanel;

    // ── State ─────────────────────────────────────────────────────────
    private int selectedIndex = -1;
    private List<Film> currentSuggestions = List.of();
    private final java.util.function.Consumer<Film> onSelect;

    // ── Constructor ──────────────────────────────────────────────────
    public SearchBar(JFrame owner, java.util.function.Consumer<Film> onSelect) {
        this.onSelect = onSelect;

        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        setPreferredSize(new Dimension(420, 44));

        // ── Outer rounded pill ───────────────────────────────────────
        JPanel pill = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(field.isFocusOwner() ? BG_FOCUS : BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                g2.setColor(field.isFocusOwner() ? BORDER_FOCUS : BORDER_IDLE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 28, 28);
                g2.dispose();
            }
        };
        pill.setOpaque(false);

        // ── Search icon ───────────────────────────────────────────────
        JLabel icon = new JLabel(" 🔍");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        icon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        pill.add(icon, BorderLayout.WEST);

        // ── Text field ────────────────────────────────────────────────
        field = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(PLACEHOLDER_CLR);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    FontMetrics fm = g2.getFontMetrics();
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(LanguageManager.t(LanguageManager.SEARCH_PLACEHOLDER), 4, y);
                    g2.dispose();
                }
            }
        };
        field.setOpaque(false);
        field.setBackground(BG);
        field.setForeground(TEXT_CLR);
        field.setCaretColor(TEXT_CLR);
        field.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 4));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        pill.add(field, BorderLayout.CENTER);

        // ── Clear (×) button ─────────────────────────────────────────
        clearBtn = new JButton("✕");
        clearBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clearBtn.setForeground(PLACEHOLDER_CLR);
        clearBtn.setOpaque(false);
        clearBtn.setContentAreaFilled(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.setVisible(false);
        clearBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        clearBtn.addActionListener(e -> { field.setText(""); hideDropdown(); field.requestFocus(); });
        pill.add(clearBtn, BorderLayout.EAST);

        add(pill, BorderLayout.CENTER);

        // ── Dropdown window ───────────────────────────────────────────
        dropdown = new JWindow(owner);
        dropdown.setType(Window.Type.POPUP);
        dropPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DROP_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(DROP_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        dropPanel.setLayout(new BoxLayout(dropPanel, BoxLayout.Y_AXIS));
        dropPanel.setOpaque(false);
        dropPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        dropdown.add(dropPanel);

        // ── Listeners ─────────────────────────────────────────────────
        field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { onTextChanged(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { onTextChanged(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { onTextChanged(); }
        });

        field.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                // Delay so click on dropdown row registers first
                Timer t = new Timer(200, ev -> {
                    // Check if mouse is currently over the dropdown window
                    Point mousePos = MouseInfo.getPointerInfo().getLocation();
                    Rectangle dropBounds = new Rectangle(
                        dropdown.getLocationOnScreen().x,
                        dropdown.getLocationOnScreen().y,
                        dropdown.getWidth(),
                        dropdown.getHeight()
                    );
                    if (!dropBounds.contains(mousePos)) hideDropdown();
                });
                t.setRepeats(false);
                t.start();
                pill.repaint();
            }
            @Override public void focusGained(FocusEvent e) { pill.repaint(); onTextChanged(); }
        });

        field.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN  -> moveSelection(1);
                    case KeyEvent.VK_UP    -> moveSelection(-1);
                    case KeyEvent.VK_ENTER -> confirmSelection();
                    case KeyEvent.VK_ESCAPE-> hideDropdown();
                }
            }
        });

        // Hide dropdown when owner moves / resizes
        owner.addComponentListener(new ComponentAdapter() {
            @Override public void componentMoved(ComponentEvent e)   { repositionDropdown(); }
            @Override public void componentResized(ComponentEvent e) { repositionDropdown(); }
        });

        // Language change: repaint placeholder
        LanguageManager.getInstance().addChangeListener(field::repaint);
    }

    // ── Text changed ─────────────────────────────────────────────────
    private void onTextChanged() {
        String query = field.getText().trim();
        clearBtn.setVisible(!query.isEmpty());
        selectedIndex = -1;

        if (query.isEmpty()) { hideDropdown(); return; }

        String q = query.toLowerCase();
        currentSuggestions = FilmDatabase.getUniqueFilms().stream()
                .filter(f -> f.getTitle().toLowerCase().contains(q)
                          || f.getDirector().toLowerCase().contains(q)
                          || f.getCast().toLowerCase().contains(q))
                .limit(7)
                .collect(Collectors.toList());

        buildDropdown();
    }

    // ── Build dropdown rows ───────────────────────────────────────────
    private void buildDropdown() {
        dropPanel.removeAll();

        if (currentSuggestions.isEmpty()) {
            JLabel none = new JLabel("  " + LanguageManager.t(LanguageManager.SEARCH_NO_RESULTS));
            none.setForeground(PLACEHOLDER_CLR);
            none.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            none.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            dropPanel.add(none);
        } else {
            for (int i = 0; i < currentSuggestions.size(); i++) {
                dropPanel.add(createRow(currentSuggestions.get(i), i));
            }
        }

        dropPanel.revalidate();
        dropPanel.repaint();
        showDropdown();
    }

    // ── Single suggestion row ─────────────────────────────────────────
    private JPanel createRow(Film film, int index) {
        final int idx = index;   // capture stable copy for lambdas
        JPanel row = new JPanel(new BorderLayout(10, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (idx == selectedIndex)            g2.setColor(DROP_SELECT);
                else if (getClientProperty("hover") != null) g2.setColor(DROP_HOVER);
                else                                 g2.setColor(new Color(0, 0, 0, 0));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));
        row.putClientProperty("index", index);

        // Tiny poster thumbnail
        JLabel thumb = new JLabel();
        thumb.setPreferredSize(new Dimension(38, 54));
        thumb.setHorizontalAlignment(SwingConstants.CENTER);
        java.io.File imgFile = new java.io.File(film.getImagePath());
        if (imgFile.exists()) {
            ImageIcon ic = new ImageIcon(film.getImagePath());
            Image img = ic.getImage().getScaledInstance(38, 54, Image.SCALE_SMOOTH);
            thumb.setIcon(new ImageIcon(img));
        } else {
            thumb.setText("🎬");
            thumb.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
            thumb.setForeground(PLACEHOLDER_CLR);
        }
        row.add(thumb, BorderLayout.WEST);

        // Text info
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(film.getTitle());
        title.setForeground(TEXT_CLR);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel sub = new JLabel(LanguageManager.t(LanguageManager.FILM_DIRECTOR)
                + ": " + film.getDirector()
                + "  •  " + film.getDuration() + " " + LanguageManager.t(LanguageManager.FILM_MINS));
        sub.setForeground(SUBTITLE_CLR);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        info.add(title);
        info.add(Box.createRigidArea(new Dimension(0, 2)));
        info.add(sub);
        row.add(info, BorderLayout.CENTER);

        // Hover
        row.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                row.putClientProperty("hover", Boolean.TRUE);
                row.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                row.putClientProperty("hover", null);
                row.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) {
                onSelect.accept(film);
                field.setText(film.getTitle());
                hideDropdown();
            }
        });

        return row;
    }

    // ── Keyboard navigation ───────────────────────────────────────────
    private void moveSelection(int delta) {
        if (currentSuggestions.isEmpty()) return;
        selectedIndex = Math.max(-1, Math.min(currentSuggestions.size() - 1, selectedIndex + delta));
        dropPanel.repaint();
    }

    private void confirmSelection() {
        if (selectedIndex >= 0 && selectedIndex < currentSuggestions.size()) {
            Film f = currentSuggestions.get(selectedIndex);
            onSelect.accept(f);
            field.setText(f.getTitle());
            hideDropdown();
        }
    }

    // ── Show / hide dropdown ──────────────────────────────────────────
    private void showDropdown() {
        repositionDropdown();
        if (!dropdown.isVisible()) dropdown.setVisible(true);
    }

    public void hideDropdown() {
        dropdown.setVisible(false);
        selectedIndex = -1;
    }

    private void repositionDropdown() {
        if (!isShowing()) return;
        Point loc = getLocationOnScreen();
        dropdown.setLocation(loc.x, loc.y + getHeight() + 2);
        dropdown.setSize(getWidth(), Math.min(currentSuggestions.isEmpty() ? 44 : currentSuggestions.size() * 68 + 12, 480));
        dropPanel.revalidate();
    }

    /** Called when language changes – repaint placeholder text */
    public void refreshLanguage() { field.repaint(); }
}
