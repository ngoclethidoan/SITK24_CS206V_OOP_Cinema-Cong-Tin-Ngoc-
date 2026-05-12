package GUI;

import model.LanguageManager;
import database.UserDatabase;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import static model.LanguageManager.t;

public class SettingPanel extends JPanel {

    // ── Design tokens ─────────────────────────────────────────────────
    private static final Color BG = new Color(22, 22, 28);
    private static final Color SIDEBAR = new Color(16, 16, 22);
    private static final Color ACCENT = new Color(100, 149, 237);
    private static final Color ACCENT2 = new Color(46, 204, 113);
    private static final Color WARN = new Color(231, 76, 60);
    private static final Color CARD = new Color(32, 32, 42);
    private static final Color FIELD_BG = new Color(42, 42, 55);
    private static final Color FIELD_BDR = new Color(65, 65, 85);
    private static final Color TEXT = Color.WHITE;
    private static final Color SUBTEXT = new Color(160, 160, 175);
    private static final Color SIDEBAR_SEL = new Color(40, 50, 75);

    // Cross-platform font (SansSerif = fallback cho cả Win/macOS, hỗ trợ Unicode/tiếng Việt)
    private static Font uiFont(int style, int size) {
        Font f = new Font("SansSerif UI", style, size);
        if (!f.getFamily().equalsIgnoreCase("SansSerif UI")) {
            f = new Font("SansSerif", style, size);
        }
        return f;
    }
    private static final Font FONT_BODY = uiFont(Font.PLAIN, 14);
    private static final Font FONT_BOLD = uiFont(Font.BOLD, 14);
    private static final Font FONT_H2 = uiFont(Font.BOLD, 18);
    private static final Font FONT_LABEL = uiFont(Font.BOLD, 13);

    // ── References ────────────────────────────────────────────────────
    private final MainFrame mainFrame;
    private JButton navLang, navAccount, navSecurity;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentArea = new JPanel(cardLayout);

    private LanguageTabPanel langPanel;
    private AccountTabPanel accountPanel;
    private SecurityTabPanel securityPanel;

    // ── Constructor ───────────────────────────────────────────────────
    public SettingPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildTitleBar(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG);
        body.add(buildSidebar(), BorderLayout.WEST);

        contentArea.setBackground(BG);
        langPanel = new LanguageTabPanel();
        accountPanel = new AccountTabPanel(mainFrame.getCurrentUser());
        securityPanel = new SecurityTabPanel(mainFrame.getCurrentUser());
        contentArea.add(langPanel, "LANG");
        contentArea.add(accountPanel, "ACCOUNT");
        contentArea.add(securityPanel, "SECURITY");

        body.add(contentArea, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        selectTab(navLang, "LANG");

        // Listener được đăng ký khi panel hiển thị, huỷ khi bị remove khỏi UI
        LanguageManager.getInstance().addChangeListener(this::refreshLabels);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        LanguageManager.getInstance().addChangeListener(this::refreshLabels);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        LanguageManager.getInstance().removeChangeListener(this::refreshLabels);
    }

    // ── Title bar ─────────────────────────────────────────────────────
    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(SIDEBAR);
        bar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 12));

        JLabel title = new JLabel("⚙  " + t(LanguageManager.SETTINGS_TITLE));
        title.setFont(uiFont(Font.BOLD, 17));
        title.setForeground(TEXT);
        bar.add(title, BorderLayout.WEST);

        JButton backBtn = new JButton("← " + t(LanguageManager.BTN_BACK));
        backBtn.setFont(FONT_BODY);
        backBtn.setForeground(SUBTEXT);
        backBtn.setOpaque(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> mainFrame.showHome());
        backBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                backBtn.setForeground(TEXT);
            }

            public void mouseExited(MouseEvent e) {
                backBtn.setForeground(SUBTEXT);
            }
        });
        bar.add(backBtn, BorderLayout.EAST);
        return bar;
    }

    // ── Sidebar ───────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(SIDEBAR);
        side.setPreferredSize(new Dimension(200, 0));
        side.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        navLang = makeNavBtn(t(LanguageManager.SETTINGS_LANGUAGE), "LANG");
        navAccount = makeNavBtn(t(LanguageManager.SETTINGS_ACCOUNT), "ACCOUNT");
        navSecurity = makeNavBtn(t(LanguageManager.SETTINGS_SECURITY), "SECURITY");

        side.add(navLang);
        side.add(Box.createRigidArea(new Dimension(0, 4)));
        side.add(navAccount);
        side.add(Box.createRigidArea(new Dimension(0, 4)));
        side.add(navSecurity);
        side.add(Box.createVerticalGlue());
        return side;
    }

    private JButton makeNavBtn(String label, String card) {
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (Boolean.TRUE.equals(getClientProperty("selected"))) {
                    g2.setColor(SIDEBAR_SEL);
                    g2.fillRoundRect(6, 2, getWidth() - 12, getHeight() - 4, 8, 8);
                    g2.setColor(ACCENT);
                    g2.fillRoundRect(6, 2, 3, getHeight() - 4, 3, 3);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BODY);
        btn.setForeground(SUBTEXT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 8));
        btn.addActionListener(e -> selectTab(btn, card));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!Boolean.TRUE.equals(btn.getClientProperty("selected"))) {
                    btn.setForeground(TEXT);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (!Boolean.TRUE.equals(btn.getClientProperty("selected"))) {
                    btn.setForeground(SUBTEXT);
                }
            }
        });
        return btn;
    }

    private void selectTab(JButton active, String card) {
        for (JButton b : new JButton[]{navLang, navAccount, navSecurity}) {
            if (b == null) {
                continue;
            }
            b.putClientProperty("selected", b == active);
            b.setForeground(b == active ? TEXT : SUBTEXT);
            b.repaint();
        }
        cardLayout.show(contentArea, card);
    }

    private void refreshLabels() {
        if (navLang != null) {
            navLang.setText(t(LanguageManager.SETTINGS_LANGUAGE));
        }
        if (navAccount != null) {
            navAccount.setText(t(LanguageManager.SETTINGS_ACCOUNT));
        }
        if (navSecurity != null) {
            navSecurity.setText(t(LanguageManager.SETTINGS_SECURITY));
        }
        if (langPanel != null) {
            langPanel.refresh();
        }
        if (accountPanel != null) {
            accountPanel.refresh();
        }
        if (securityPanel != null) {
            securityPanel.refresh();
        }
        revalidate();
        repaint();
    }

    // ════════════════════════════════════════════════════════════════
    //  TAB 1 – Language
    // ════════════════════════════════════════════════════════════════
    private class LanguageTabPanel extends JPanel {

        private JLabel hdr, descLbl;
        private JButton applyBtn;
        private final JPanel flagGrid = new JPanel(new GridLayout(1, 3, 16, 0));
        private LanguageManager.Language chosen = LanguageManager.getInstance().getCurrent();

        LanguageTabPanel() {
            setBackground(BG);
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));
            build();
        }

        private void build() {
            removeAll();
            JPanel inner = new JPanel();
            inner.setBackground(BG);
            inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

            hdr = makeH2(t(LanguageManager.SETTINGS_LANGUAGE));
            inner.add(hdr);
            inner.add(Box.createRigidArea(new Dimension(0, 8)));
            descLbl = makeSubLabel(t(LanguageManager.SETTINGS_SELECT_LANG));
            inner.add(descLbl);
            inner.add(Box.createRigidArea(new Dimension(0, 28)));

            flagGrid.setBackground(BG);
            flagGrid.setMaximumSize(new Dimension(520, 110));
            flagGrid.setAlignmentX(LEFT_ALIGNMENT);
            rebuildCards();
            inner.add(flagGrid);
            inner.add(Box.createRigidArea(new Dimension(0, 32)));

            applyBtn = makePrimaryBtn(t(LanguageManager.SETTINGS_APPLY_LANG), ACCENT);
            applyBtn.addActionListener(e -> {
                LanguageManager.getInstance().setLanguage(chosen);
                mainFrame.refreshUI();
            });
            inner.add(applyBtn);
            add(inner, BorderLayout.NORTH);
        }

        private void rebuildCards() {
            flagGrid.removeAll();
            for (LanguageManager.Language lang : LanguageManager.Language.values()) {
                flagGrid.add(makeLangCard(lang));
            }
            flagGrid.revalidate();
            flagGrid.repaint();
        }

        private JPanel makeLangCard(LanguageManager.Language lang) {
            boolean isChosen = lang == chosen;
            String flag = switch (lang) {
                case ENGLISH ->
                    "EN";
                case VIETNAMESE ->
                    "VI";
                case JAPANESE ->
                    "JP";
            };
            JPanel card = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(isChosen ? new Color(30, 50, 95) : CARD);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                    g2.setColor(isChosen ? ACCENT : FIELD_BDR);
                    g2.setStroke(new BasicStroke(isChosen ? 2f : 1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            card.setOpaque(false);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createEmptyBorder(14, 10, 14, 10));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel fl = new JLabel(flag);
            fl.setFont(uiFont(Font.BOLD, 28));
            fl.setForeground(isChosen ? ACCENT : SUBTEXT);
            fl.setAlignmentX(CENTER_ALIGNMENT);

            JLabel name = new JLabel(lang.displayName);
            name.setFont(isChosen ? FONT_BOLD : FONT_BODY);
            name.setForeground(isChosen ? ACCENT : TEXT);
            name.setAlignmentX(CENTER_ALIGNMENT);

            card.add(fl);
            card.add(Box.createRigidArea(new Dimension(0, 6)));
            card.add(name);
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    chosen = lang;
                    rebuildCards();
                }
            });
            return card;
        }

        void refresh() {
            if (hdr != null) {
                hdr.setText(t(LanguageManager.SETTINGS_LANGUAGE));
            }
            if (descLbl != null) {
                descLbl.setText(t(LanguageManager.SETTINGS_SELECT_LANG));
            }
            if (applyBtn != null) {
                applyBtn.setText(t(LanguageManager.SETTINGS_APPLY_LANG));
            }
            rebuildCards();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  TAB 2 – Account
    // ════════════════════════════════════════════════════════════════
    private class AccountTabPanel extends JPanel {

        private JLabel hdr, userLbl, emailLbl, currentInfoLbl;
        private JTextField userField, emailField;
        private JButton saveBtn;
        private JLabel statusLbl;
        private User user;

        AccountTabPanel(User user) {
            this.user = user;
            setBackground(BG);
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));
            build();
        }

        private void build() {
            removeAll();
            JPanel inner = new JPanel();
            inner.setBackground(BG);
            inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

            hdr = makeH2(t(LanguageManager.SETTINGS_ACCOUNT));
            inner.add(hdr);
            inner.add(Box.createRigidArea(new Dimension(0, 8)));

            // Hiển thị thông tin hiện tại
            String info = user != null
                    ? "Current: " + user.getName() + " (login: " + user.getUserId() + ")"
                    : "Not logged in";
            currentInfoLbl = makeSubLabel(info);
            inner.add(currentInfoLbl);
            inner.add(Box.createRigidArea(new Dimension(0, 20)));

            // Display Name
            userLbl = makeFieldLabel(t(LanguageManager.SETTINGS_USERNAME) + " (display name)");
            userField = makeTextField(user != null ? user.getName() : "");
            inner.add(userLbl);
            inner.add(Box.createRigidArea(new Dimension(0, 6)));
            inner.add(userField);
            inner.add(Box.createRigidArea(new Dimension(0, 16)));

            // Email (UI only)
            emailLbl = makeFieldLabel(t(LanguageManager.SETTINGS_EMAIL));
            emailField = makeTextField("");
            inner.add(emailLbl);
            inner.add(Box.createRigidArea(new Dimension(0, 6)));
            inner.add(emailField);
            inner.add(Box.createRigidArea(new Dimension(0, 24)));

            saveBtn = makePrimaryBtn(t(LanguageManager.SETTINGS_CHANGE_INFO), ACCENT2);
            inner.add(saveBtn);
            inner.add(Box.createRigidArea(new Dimension(0, 10)));

            statusLbl = new JLabel(" ");
            statusLbl.setFont(FONT_BODY);
            statusLbl.setAlignmentX(LEFT_ALIGNMENT);
            inner.add(statusLbl);

            saveBtn.addActionListener(e -> {
                user = mainFrame.getCurrentUser();
                if (user == null) {
                    showStatus(statusLbl, t(LanguageManager.MSG_MUST_LOGIN_FIRST), WARN);
                    return;
                }
                String newName = userField.getText().trim();
                if (newName.isEmpty()) {
                    showStatus(statusLbl, "Name cannot be empty.", WARN);
                    return;
                }
                // Đổi cả name lẫn userId để login bằng username mới được
                user.setName(newName);
                user.setUserId(newName);
                UserDatabase.save();   // ← lưu xuống file
                mainFrame.refreshUI();
                currentInfoLbl.setText("Current: " + user.getName() + " (login: " + user.getUserId() + ")");
                showStatus(statusLbl, t(LanguageManager.MSG_UPDATED), ACCENT2);
            });

            add(inner, BorderLayout.NORTH);
        }

        void refresh() {
            user = mainFrame.getCurrentUser();
            if (hdr != null) {
                hdr.setText(t(LanguageManager.SETTINGS_ACCOUNT));
            }
            if (userLbl != null) {
                userLbl.setText(t(LanguageManager.SETTINGS_USERNAME) + " (display name)");
            }
            if (emailLbl != null) {
                emailLbl.setText(t(LanguageManager.SETTINGS_EMAIL));
            }
            if (saveBtn != null) {
                saveBtn.setText(t(LanguageManager.SETTINGS_CHANGE_INFO));
            }
            if (userField != null && user != null) {
                userField.setText(user.getName());
            }
            if (currentInfoLbl != null) {
                currentInfoLbl.setText(user != null
                        ? "Current: " + user.getName() + " (login: " + user.getUserId() + ")"
                        : "Not logged in");
            }
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  TAB 3 – Security
    // ════════════════════════════════════════════════════════════════
    private class SecurityTabPanel extends JPanel {

        private JLabel hdr, oldLbl, newLbl, confirmLbl;
        private JPasswordField oldPF, newPF, confirmPF;
        private JButton changeBtn;
        private JLabel statusLbl;
        private User user;

        SecurityTabPanel(User user) {
            this.user = user;
            setBackground(BG);
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));
            build();
        }

        private void build() {
            removeAll();
            JPanel inner = new JPanel();
            inner.setBackground(BG);
            inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

            hdr = makeH2(t(LanguageManager.SETTINGS_SECURITY));
            inner.add(hdr);
            inner.add(Box.createRigidArea(new Dimension(0, 24)));

            oldLbl = makeFieldLabel(t(LanguageManager.SETTINGS_OLD_PASS));
            oldPF = makePassField();
            newLbl = makeFieldLabel(t(LanguageManager.SETTINGS_NEW_PASS));
            newPF = makePassField();
            confirmLbl = makeFieldLabel(t(LanguageManager.SETTINGS_CONFIRM_PASS));
            confirmPF = makePassField();

            inner.add(oldLbl);
            inner.add(Box.createRigidArea(new Dimension(0, 6)));
            inner.add(oldPF);
            inner.add(Box.createRigidArea(new Dimension(0, 14)));
            inner.add(newLbl);
            inner.add(Box.createRigidArea(new Dimension(0, 6)));
            inner.add(newPF);
            inner.add(Box.createRigidArea(new Dimension(0, 14)));
            inner.add(confirmLbl);
            inner.add(Box.createRigidArea(new Dimension(0, 6)));
            inner.add(confirmPF);
            inner.add(Box.createRigidArea(new Dimension(0, 24)));

            changeBtn = makePrimaryBtn(t(LanguageManager.SETTINGS_CHANGE_PASS), ACCENT);
            inner.add(changeBtn);
            inner.add(Box.createRigidArea(new Dimension(0, 10)));

            statusLbl = new JLabel(" ");
            statusLbl.setFont(FONT_BODY);
            statusLbl.setAlignmentX(LEFT_ALIGNMENT);
            inner.add(statusLbl);

            changeBtn.addActionListener(e -> {
                user = mainFrame.getCurrentUser();
                if (user == null) {
                    showStatus(statusLbl, t(LanguageManager.MSG_MUST_LOGIN_FIRST), WARN);
                    return;
                }
                String oldP = new String(oldPF.getPassword());
                String newP = new String(newPF.getPassword());
                String conP = new String(confirmPF.getPassword());
                if (!user.getPassword().equals(oldP)) {
                    showStatus(statusLbl, t(LanguageManager.MSG_WRONG_OLD_PASS), WARN);
                    shake(oldPF);
                    return;
                }
                if (newP.isEmpty()) {
                    showStatus(statusLbl, "New password cannot be empty.", WARN);
                    shake(newPF);
                    return;
                }
                if (!newP.equals(conP)) {
                    showStatus(statusLbl, t(LanguageManager.MSG_PASS_MISMATCH), WARN);
                    shake(confirmPF);
                    return;
                }
                user.setPassword(newP);
                UserDatabase.save();    // ← lưu xuống file
                oldPF.setText("");
                newPF.setText("");
                confirmPF.setText("");
                showStatus(statusLbl, t(LanguageManager.MSG_UPDATED), ACCENT2);
            });

            add(inner, BorderLayout.NORTH);
        }

        void refresh() {
            user = mainFrame.getCurrentUser();
            if (hdr != null) {
                hdr.setText(t(LanguageManager.SETTINGS_SECURITY));
            }
            if (oldLbl != null) {
                oldLbl.setText(t(LanguageManager.SETTINGS_OLD_PASS));
            }
            if (newLbl != null) {
                newLbl.setText(t(LanguageManager.SETTINGS_NEW_PASS));
            }
            if (confirmLbl != null) {
                confirmLbl.setText(t(LanguageManager.SETTINGS_CONFIRM_PASS));
            }
            if (changeBtn != null) {
                changeBtn.setText(t(LanguageManager.SETTINGS_CHANGE_PASS));
            }
        }

        private void shake(JComponent c) {
            Point orig = c.getLocation();
            Timer t = new Timer(18, null);
            int[] n = {0};
            t.addActionListener(ev -> {
                c.setLocation(orig.x + (n[0] % 2 == 0 ? 7 : -7), orig.y);
                if (++n[0] > 10) {
                    t.stop();
                    c.setLocation(orig);
                }
            });
            t.start();
        }
    }

    // ── Shared factories ──────────────────────────────────────────────
    private JLabel makeH2(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_H2);
        l.setForeground(TEXT);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JLabel makeSubLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(SUBTEXT);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JLabel makeFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(SUBTEXT);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JTextField makeTextField(String init) {
        JTextField f = new JTextField(init);
        f.setFont(FONT_BODY);
        f.setBackground(FIELD_BG);
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BDR, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setAlignmentX(LEFT_ALIGNMENT);
        f.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ACCENT, 1), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(FIELD_BDR, 1), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            }
        });
        return f;
    }

    private JPasswordField makePassField() {
        JPasswordField f = new JPasswordField();
        f.setFont(FONT_BODY);
        f.setBackground(FIELD_BG);
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BDR, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setAlignmentX(LEFT_ALIGNMENT);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ACCENT, 1), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            }

            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(FIELD_BDR, 1), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            }
        });
        return f;
    }

    private JButton makePrimaryBtn(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(FONT_BOLD);
        b.setForeground(Color.WHITE);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(240, 40));
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        return b;
    }

    private void showStatus(JLabel lbl, String msg, Color color) {
        lbl.setText(msg);
        lbl.setForeground(color);
        Timer t = new Timer(3000, e -> lbl.setText(" "));
        t.setRepeats(false);
        t.start();
    }
}