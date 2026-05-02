package GUI;

import database.FilmDatabase;
import model.Film;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;

public class SearchBar extends JPanel {

    // ── Colours ──────────────────────────────────────────────────────
    private static final Color BG            = new Color(30, 30, 30);
    private static final Color BG_FOCUS      = new Color(38, 38, 38);
    private static final Color BORDER_IDLE   = new Color(60, 60, 60);
    private static final Color BORDER_FOCUS  = new Color(100, 149, 237);
    private static final Color TEXT_CLR      = Color.WHITE;
    private static final Color PLACEHOLDER_CLR = new Color(120, 120, 120);
    private static final Color DROP_BG       = new Color(28, 28, 28);
    private static final Color DROP_HOVER    = new Color(50, 50, 60);
    private static final Color DROP_SELECT   = new Color(40, 60, 100);
    private static final Color DROP_BORDER   = new Color(70, 70, 90);
    private static final Color SUBTITLE_CLR  = new Color(150, 150, 150);

    // ── Font an toàn cho cả Windows và macOS (hỗ trợ tiếng Việt) ─────
    private static Font safeFont(String name, int style, int size) {
        Font f = new Font(name, style, size);
        if (f.getFamily().equalsIgnoreCase(name)) return f;
        return new Font("SansSerif", style, size);
    }

    private static final Font FONT_UI    = safeFont("SansSerif",       Font.PLAIN, 15);
    private static final Font FONT_BOLD  = safeFont("SansSerif",       Font.BOLD,  14);
    private static final Font FONT_SUB   = safeFont("SansSerif",       Font.PLAIN, 12);
    private static final Font FONT_EMOJI = safeFont("SansSerif Emoji", Font.PLAIN, 16);

    // ── Components ───────────────────────────────────────────────────
    private final JTextField field;
    private final JButton    searchIconBtn;
    private final JButton    clearBtn;
    private final JWindow    dropdown;
    private final JPanel     dropPanel;
    private final JPanel     pill;

    // ── State ─────────────────────────────────────────────────────────
    private int selectedIndex = -1;
    private List<Film> currentSuggestions = List.of();
    private final java.util.function.Consumer<Film> onSelect;
    private final MainFrame owner;

    // ── Constructor ──────────────────────────────────────────────────
    public SearchBar(MainFrame ownerFrame, java.util.function.Consumer<Film> onSelect) {
        this.onSelect = onSelect;
        this.owner    = ownerFrame;

        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(440, 44));

        // ── Rounded pill ─────────────────────────────────────────────
        pill = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(field.isFocusOwner() ? BG_FOCUS : BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                g2.setColor(field.isFocusOwner() ? BORDER_FOCUS : BORDER_IDLE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 28, 28);
                g2.dispose();
            }
        };
        pill.setOpaque(false);
        
        // ── Text field ────────────────────────────────────────────────
        field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(PLACEHOLDER_CLR);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));

                    FontMetrics fm = g2.getFontMetrics();
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

                    g2.drawString(
                            LanguageManager.t(LanguageManager.SEARCH_PLACEHOLDER),
                            6,
                            y
                    );

                    g2.dispose();
                }
            }
        };

        field.setOpaque(false);
        field.setBackground(BG);
        field.setForeground(TEXT_CLR);
        field.setCaretColor(TEXT_CLR);
        field.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        field.setFont(FONT_UI);


       // ── 🔍 Search icon button (clickable) ─────────────────────────
        searchIconBtn = new JButton("🔍");
        searchIconBtn.setFont(FONT_EMOJI);
        searchIconBtn.setForeground(PLACEHOLDER_CLR);
        searchIconBtn.setOpaque(false);
        searchIconBtn.setContentAreaFilled(false);
        searchIconBtn.setBorderPainted(false);
        searchIconBtn.setFocusPainted(false);
        searchIconBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchIconBtn.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        searchIconBtn.addActionListener(e -> doSearch());

        // ── ✕ Clear button ────────────────────────────────────────────
        clearBtn = new JButton("✕");
        clearBtn.setFont(safeFont("SansSerif", Font.PLAIN, 12));
        clearBtn.setForeground(PLACEHOLDER_CLR);
        clearBtn.setOpaque(false);
        clearBtn.setContentAreaFilled(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.setVisible(false);
        clearBtn.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        clearBtn.addActionListener(e -> {
            field.setText("");
            hideDropdown();
            field.requestFocus();
        });

        // ── RIGHT PANEL (FIX LAYOUT BUG) ─────────────────────────────
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(clearBtn);
        rightPanel.add(searchIconBtn);

        // ── PILL CONTAINER ───────────────────────────────────────────
        JPanel pill = new JPanel(new BorderLayout());
        pill.setBackground(BG);
        pill.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        pill.add(field, BorderLayout.CENTER);
        pill.add(rightPanel, BorderLayout.EAST);

        add(pill, BorderLayout.CENTER);

        // ── Dropdown window ───────────────────────────────────────────
        dropdown = new JWindow(ownerFrame);
        dropdown.setType(Window.Type.POPUP);
        dropPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DROP_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(DROP_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
            }
        };
        dropPanel.setLayout(new BoxLayout(dropPanel, BoxLayout.Y_AXIS));
        dropPanel.setOpaque(false);
        dropPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        dropdown.add(dropPanel);

        // ── Document listener → live suggestions ──────────────────────
        field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { onTextChanged(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { onTextChanged(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { onTextChanged(); }
        });

        // ── Focus ─────────────────────────────────────────────────────
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                Timer t = new Timer(200, ev -> {
                    if (!dropdown.isVisible()) return;
                    Point mp = MouseInfo.getPointerInfo().getLocation();
                    Point dl = dropdown.getLocationOnScreen();
                    Rectangle db = new Rectangle(dl.x, dl.y, dropdown.getWidth(), dropdown.getHeight());
                    if (!db.contains(mp)) hideDropdown();
                });
                t.setRepeats(false);
                t.start();
                pill.repaint();
            }
            @Override public void focusGained(FocusEvent e) { pill.repaint(); onTextChanged(); }
        });

        // ── Keyboard ──────────────────────────────────────────────────
        field.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN   -> moveSelection(1);
                    case KeyEvent.VK_UP     -> moveSelection(-1);
                    case KeyEvent.VK_ENTER  -> { doSearch(); }
                    case KeyEvent.VK_ESCAPE -> hideDropdown();
                }
            }
        });

        // Reposition khi window di chuyển
        ownerFrame.addComponentListener(new ComponentAdapter() {
            @Override public void componentMoved(ComponentEvent e)   { repositionDropdown(); }
            @Override public void componentResized(ComponentEvent e) { repositionDropdown(); }
        });

        LanguageManager.getInstance().addChangeListener(field::repaint);
    }

    // ── doSearch: Enter hoặc click 🔍 ────────────────────────────────
    private void doSearch() {
        // Nếu đang chọn item bằng ↑↓ thì mở film đó
        if (selectedIndex >= 0 && selectedIndex < currentSuggestions.size()) {
            Film f = currentSuggestions.get(selectedIndex);
            field.setText(f.getTitle());
            hideDropdown();
            onSelect.accept(f);
            return;
        }
        // Không chọn cụ thể → tìm tất cả kết quả phù hợp, hiển thị search result panel
        String query = field.getText().trim();
        if (query.isEmpty()) return;
        hideDropdown();
        List<Film> results = searchFilms(query);
        owner.showSearchResults(results, query);
    }

    // ── Text changed ─────────────────────────────────────────────────
    private void onTextChanged() {
        String query = field.getText().trim();
        clearBtn.setVisible(!query.isEmpty());
        selectedIndex = -1;
        if (query.isEmpty()) { hideDropdown(); return; }
        currentSuggestions = searchFilms(query).stream().limit(7).collect(Collectors.toList());
        buildDropdown();
    }

    /**
     * Tìm kiếm bỏ qua các ký tự đặc biệt – chỉ so sánh các "word" (chữ và số).
     * Ví dụ: "avengers: endgame" tìm bằng "avengers endgame" vẫn ra.
     */
    private List<Film> searchFilms(String rawQuery) {
        // Chuẩn hoá: xóa ký tự không phải chữ/số/khoảng trắng, lower
        String normalized = rawQuery.toLowerCase().replaceAll("[^\\p{L}\\p{N}\\s]", " ").trim();
        String[] words = normalized.split("\\s+");

        return FilmDatabase.getUniqueFilms().stream()
            .filter(f -> {
                String title    = normalize(f.getTitle());
                String director = normalize(f.getDirector());
                String cast     = normalize(f.getCast());
                // Tất cả từ khóa phải xuất hiện trong ít nhất 1 trường
                for (String w : words) {
                    if (w.isEmpty()) continue;
                    if (!title.contains(w) && !director.contains(w) && !cast.contains(w))
                        return false;
                }
                return true;
            })
            .collect(Collectors.toList());
    }

    private String normalize(String s) {
        return s.toLowerCase().replaceAll("[^\\p{L}\\p{N}\\s]", " ");
    }

    // ── Build dropdown rows ───────────────────────────────────────────
    private void buildDropdown() {
        dropPanel.removeAll();
        if (currentSuggestions.isEmpty()) {
            JLabel none = new JLabel("  " + LanguageManager.t(LanguageManager.SEARCH_NO_RESULTS));
            none.setForeground(PLACEHOLDER_CLR);
            none.setFont(safeFont("SansSerif", Font.ITALIC, 13));
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
        final int idx = index;
        JPanel row = new JPanel(new BorderLayout(10, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (idx == selectedIndex)                        g2.setColor(DROP_SELECT);
                else if (getClientProperty("hover") != null)    g2.setColor(DROP_HOVER);
                else                                             g2.setColor(new Color(0,0,0,0));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Thumbnail
        JLabel thumb = new JLabel();
        thumb.setPreferredSize(new Dimension(38, 54));
        thumb.setHorizontalAlignment(SwingConstants.CENTER);
        java.io.File imgFile = new java.io.File(film.getImagePath());
        if (imgFile.exists()) {
            Image img = new ImageIcon(film.getImagePath()).getImage()
                            .getScaledInstance(38, 54, Image.SCALE_SMOOTH);
            thumb.setIcon(new ImageIcon(img));
        } else {
            thumb.setText("🎬");
            thumb.setFont(safeFont("SansSerif", Font.PLAIN, 22));
            thumb.setForeground(PLACEHOLDER_CLR);
        }
        row.add(thumb, BorderLayout.WEST);

        // Info
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel titleLbl = new JLabel(film.getTitle());
        titleLbl.setForeground(TEXT_CLR);
        titleLbl.setFont(FONT_BOLD);

        JLabel subLbl = new JLabel(
            LanguageManager.t(LanguageManager.FILM_DIRECTOR) + ": " + film.getDirector()
            + "  •  " + film.getDuration() + " " + LanguageManager.t(LanguageManager.FILM_MINS));
        subLbl.setForeground(SUBTITLE_CLR);
        subLbl.setFont(FONT_SUB);

        info.add(titleLbl);
        info.add(Box.createRigidArea(new Dimension(0, 2)));
        info.add(subLbl);
        row.add(info, BorderLayout.CENTER);

        row.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { row.putClientProperty("hover", Boolean.TRUE); row.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { row.putClientProperty("hover", null); row.repaint(); }
            @Override public void mouseClicked(MouseEvent e) {
                field.setText(film.getTitle());
                hideDropdown();
                onSelect.accept(film);
            }
        });
        return row;
    }

    // ── Navigation ────────────────────────────────────────────────────
    private void moveSelection(int delta) {
        if (currentSuggestions.isEmpty()) return;
        selectedIndex = Math.max(-1, Math.min(currentSuggestions.size()-1, selectedIndex + delta));
        dropPanel.repaint();
    }

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
        int h = currentSuggestions.isEmpty() ? 44 : currentSuggestions.size() * 68 + 12;
        dropdown.setSize(getWidth(), Math.min(h, 480));
        dropPanel.revalidate();
    }

    public void refreshLanguage() { field.repaint(); }
}