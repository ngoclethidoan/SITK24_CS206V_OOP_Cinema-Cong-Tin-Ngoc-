package GUI;

import model.User;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import static GUI.LanguageManager.t;

/**
 * SettingPanel — modal settings dialog with three tabs:
 *   1. 🌐 Language  – choose EN / VI / JP, applies instantly to all UI text
 *   2. 👤 Account   – change username and email
 *   3. 🔒 Security  – change password
 *
 * All label strings are driven by LanguageManager so they update live
 * when the language is changed.
 */
public class SettingPanel extends JDialog {

    // ── Design tokens ────────────────────────────────────────────────
    private static final Color BG        = new Color(22, 22, 28);
    private static final Color SIDEBAR   = new Color(16, 16, 22);
    private static final Color ACCENT    = new Color(100, 149, 237);
    private static final Color ACCENT2   = new Color(46, 204, 113);
    private static final Color WARN      = new Color(231, 76,  60);
    private static final Color CARD      = new Color(32, 32, 42);
    private static final Color FIELD_BG  = new Color(42, 42, 55);
    private static final Color FIELD_BDR = new Color(65, 65, 85);
    private static final Color TEXT      = Color.WHITE;
    private static final Color SUBTEXT   = new Color(160, 160, 175);
    private static final Color SIDEBAR_SEL  = new Color(40, 50, 75);
    private static final Font  FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font  FONT_BOLD = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font  FONT_H2   = new Font("Segoe UI", Font.BOLD,  18);

    // ── References ───────────────────────────────────────────────────
    private final MainFrame mainFrame;

    // ── Tab nav buttons (kept so we can re-label on lang change) ─────
    private JButton navLang, navAccount, navSecurity;

    // ── Active content panel ─────────────────────────────────────────
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     contentArea;

    // ── Panels ───────────────────────────────────────────────────────
    private LanguageTabPanel  langPanel;
    private AccountTabPanel   accountPanel;
    private SecurityTabPanel  securityPanel;

    // ── Constructor ──────────────────────────────────────────────────
    public SettingPanel(MainFrame mainFrame) {
        super(mainFrame, t(LanguageManager.SETTINGS_TITLE), true);
        this.mainFrame = mainFrame;

        setSize(720, 500);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(60, 60, 80), 1));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        // ── Title bar ────────────────────────────────────────────────
        root.add(buildTitleBar(), BorderLayout.NORTH);

        // ── Body ─────────────────────────────────────────────────────
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG);
        body.add(buildSidebar(), BorderLayout.WEST);

        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(BG);

        langPanel     = new LanguageTabPanel();
        accountPanel  = new AccountTabPanel(mainFrame.getCurrentUser());
        securityPanel = new SecurityTabPanel(mainFrame.getCurrentUser());

        contentArea.add(langPanel,     "LANG");
        contentArea.add(accountPanel,  "ACCOUNT");
        contentArea.add(securityPanel, "SECURITY");

        body.add(contentArea, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);

        setContentPane(root);

        // Select first tab
        selectTab(navLang, "LANG");

        // Re-render labels when language changes; remove listener on close to avoid memory leak
        Runnable langListener = this::refreshLabels;
        LanguageManager.getInstance().addChangeListener(langListener);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                LanguageManager.getInstance().removeChangeListener(langListener);
            }
        });

        setVisible(true);
    }

    // ── Title bar (draggable) ────────────────────────────────────────
    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(SIDEBAR);
        bar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 12));

        JLabel title = new JLabel("⚙  " + t(LanguageManager.SETTINGS_TITLE));
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(TEXT);
        bar.add(title, BorderLayout.WEST);

        JButton close = new JButton("✕");
        close.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        close.setForeground(SUBTEXT);
        close.setOpaque(false);
        close.setContentAreaFilled(false);
        close.setBorderPainted(false);
        close.setFocusPainted(false);
        close.setCursor(new Cursor(Cursor.HAND_CURSOR));
        close.addActionListener(e -> dispose());
        close.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { close.setForeground(WARN); }
            public void mouseExited(MouseEvent e)  { close.setForeground(SUBTEXT); }
        });
        bar.add(close, BorderLayout.EAST);

        // Make draggable
        final Point[] dragStart = {null};
        bar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { dragStart[0] = e.getPoint(); }
        });
        bar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (dragStart[0] == null) return;
                Point cur = e.getLocationOnScreen();
                setLocation(cur.x - dragStart[0].x, cur.y - dragStart[0].y);
            }
        });

        return bar;
    }

    // ── Sidebar ──────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(SIDEBAR);
        side.setPreferredSize(new Dimension(190, 0));
        side.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        navLang     = makeNavBtn(t(LanguageManager.SETTINGS_LANGUAGE), "LANG");
        navAccount  = makeNavBtn(t(LanguageManager.SETTINGS_ACCOUNT),  "ACCOUNT");
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
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (Boolean.TRUE.equals(getClientProperty("selected"))) {
                    g2.setColor(SIDEBAR_SEL);
                    g2.fillRoundRect(6, 2, getWidth() - 12, getHeight() - 4, 8, 8);
                    // Left accent bar
                    g2.setColor(ACCENT);
                    g2.fillRoundRect(6, 2, 3, getHeight() - 4, 3, 3);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        btn.setForeground(SUBTEXT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 8));

        btn.addActionListener(e -> selectTab(btn, card));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!Boolean.TRUE.equals(btn.getClientProperty("selected")))
                    btn.setForeground(TEXT);
            }
            public void mouseExited(MouseEvent e) {
                if (!Boolean.TRUE.equals(btn.getClientProperty("selected")))
                    btn.setForeground(SUBTEXT);
            }
        });

        return btn;
    }

    private void selectTab(JButton active, String card) {
        for (JButton b : new JButton[]{navLang, navAccount, navSecurity}) {
            if (b == null) continue;
            b.putClientProperty("selected", b == active);
            b.setForeground(b == active ? TEXT : SUBTEXT);
            b.repaint();
        }
        cardLayout.show(contentArea, card);
    }

    // ── Refresh all labels on lang change ────────────────────────────
    private void refreshLabels() {
        setTitle(t(LanguageManager.SETTINGS_TITLE));
        if (navLang != null)     navLang.setText(t(LanguageManager.SETTINGS_LANGUAGE));
        if (navAccount != null)  navAccount.setText(t(LanguageManager.SETTINGS_ACCOUNT));
        if (navSecurity != null) navSecurity.setText(t(LanguageManager.SETTINGS_SECURITY));
        if (langPanel != null)   langPanel.refresh();
        if (accountPanel != null) accountPanel.refresh();
        if (securityPanel != null) securityPanel.refresh();
        revalidate();
        repaint();
    }

    // ════════════════════════════════════════════════════════════════
    //  TAB 1 – Language
    // ════════════════════════════════════════════════════════════════
    private class LanguageTabPanel extends JPanel {
        private JLabel  hdr;
        private JLabel  selectLbl;
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

            JLabel desc = makeSubLabel(t(LanguageManager.SETTINGS_SELECT_LANG));
            inner.add(desc);
            inner.add(Box.createRigidArea(new Dimension(0, 28)));

            // Language cards
            flagGrid.setBackground(BG);
            flagGrid.setMaximumSize(new Dimension(520, 100));
            flagGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

            flagGrid.removeAll();
            for (LanguageManager.Language lang : LanguageManager.Language.values()) {
                flagGrid.add(makeLangCard(lang));
            }

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

        private JPanel makeLangCard(LanguageManager.Language lang) {
            boolean isChosen  = lang == chosen;

            String flag = switch (lang) {
                case ENGLISH    -> "🇬🇧";
                case VIETNAMESE -> "🇻🇳";
                case JAPANESE   -> "🇯🇵";
            };

            JPanel card = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color bg = isChosen ? new Color(30, 50, 95) : CARD;
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                    Color bdr = isChosen ? ACCENT : FIELD_BDR;
                    g2.setStroke(new BasicStroke(isChosen ? 2f : 1f));
                    g2.setColor(bdr);
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            card.setOpaque(false);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createEmptyBorder(14, 10, 14, 10));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel fl = new JLabel(flag);
            fl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
            fl.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel name = new JLabel(lang.displayName);
            name.setFont(isChosen ? FONT_BOLD : FONT_BODY);
            name.setForeground(isChosen ? ACCENT : TEXT);
            name.setAlignmentX(Component.CENTER_ALIGNMENT);

            card.add(fl);
            card.add(Box.createRigidArea(new Dimension(0, 6)));
            card.add(name);

            card.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    chosen = lang;
                    flagGrid.removeAll();
                    for (LanguageManager.Language l : LanguageManager.Language.values())
                        flagGrid.add(makeLangCard(l));
                    flagGrid.revalidate();
                    flagGrid.repaint();
                }
            });

            return card;
        }

        void refresh() {
            if (hdr != null) hdr.setText(t(LanguageManager.SETTINGS_LANGUAGE));
            if (applyBtn != null) applyBtn.setText(t(LanguageManager.SETTINGS_APPLY_LANG));
            // Rebuild cards with fresh labels
            flagGrid.removeAll();
            for (LanguageManager.Language lang : LanguageManager.Language.values())
                flagGrid.add(makeLangCard(lang));
            flagGrid.revalidate();
            flagGrid.repaint();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  TAB 2 – Account (username / email)
    // ════════════════════════════════════════════════════════════════
    private class AccountTabPanel extends JPanel {
        private JLabel   hdr;
        private JLabel   userLbl, emailLbl;
        private JTextField userField, emailField;
        private JButton  saveBtn;
        private JLabel   statusLbl;
        private User     user;

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
            inner.add(Box.createRigidArea(new Dimension(0, 24)));

            // Username row
            userLbl = makeFieldLabel(t(LanguageManager.SETTINGS_USERNAME));
            userField = makeTextField(user != null ? user.getName() : "");
            inner.add(userLbl);
            inner.add(Box.createRigidArea(new Dimension(0, 6)));
            inner.add(userField);
            inner.add(Box.createRigidArea(new Dimension(0, 18)));

            // Email row
            emailLbl = makeFieldLabel(t(LanguageManager.SETTINGS_EMAIL));
            String currentEmail = (user != null && user instanceof ExtendedUser)
                    ? ((ExtendedUser) user).getEmail() : "";
            emailField = makeTextField(currentEmail);
            inner.add(emailLbl);
            inner.add(Box.createRigidArea(new Dimension(0, 6)));
            inner.add(emailField);
            inner.add(Box.createRigidArea(new Dimension(0, 26)));

            // Save button
            saveBtn = makePrimaryBtn(t(LanguageManager.SETTINGS_CHANGE_INFO), ACCENT2);
            inner.add(saveBtn);

            // Status
            inner.add(Box.createRigidArea(new Dimension(0, 12)));
            statusLbl = new JLabel(" ");
            statusLbl.setFont(FONT_BODY);
            statusLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            inner.add(statusLbl);

            saveBtn.addActionListener(e -> {
                if (user == null) {
                    showStatus(statusLbl, t(LanguageManager.MSG_MUST_LOGIN_FIRST), WARN);
                    return;
                }
                String newName  = userField.getText().trim();
                if (newName.isEmpty()) {
                    showStatus(statusLbl, t(LanguageManager.SETTINGS_USERNAME) + " cannot be empty.", WARN);
                    return;
                }
                user.setName(newName);
                // email field is UI-only for now (User model has no email field)
                mainFrame.refreshUI();
                showStatus(statusLbl, t(LanguageManager.MSG_UPDATED), ACCENT2);
            });

            add(inner, BorderLayout.NORTH);
        }

        void refresh() {
            if (hdr != null)      hdr.setText(t(LanguageManager.SETTINGS_ACCOUNT));
            if (userLbl != null)  userLbl.setText(t(LanguageManager.SETTINGS_USERNAME));
            if (emailLbl != null) emailLbl.setText(t(LanguageManager.SETTINGS_EMAIL));
            if (saveBtn != null)  saveBtn.setText(t(LanguageManager.SETTINGS_CHANGE_INFO));
            // Update user reference and sync field text
            user = mainFrame.getCurrentUser();
            if (userField != null && user != null) userField.setText(user.getName());
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  TAB 3 – Security (change password)
    // ════════════════════════════════════════════════════════════════
    private class SecurityTabPanel extends JPanel {
        private JLabel   hdr;
        private JLabel   oldLbl, newLbl, confirmLbl;
        private JPasswordField oldPF, newPF, confirmPF;
        private JButton  changeBtn;
        private JLabel   statusLbl;
        private User     user;

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

            oldLbl   = makeFieldLabel(t(LanguageManager.SETTINGS_OLD_PASS));
            oldPF    = makePassField();
            newLbl   = makeFieldLabel(t(LanguageManager.SETTINGS_NEW_PASS));
            newPF    = makePassField();
            confirmLbl = makeFieldLabel(t(LanguageManager.SETTINGS_CONFIRM_PASS));
            confirmPF  = makePassField();

            inner.add(oldLbl);    inner.add(Box.createRigidArea(new Dimension(0, 6)));
            inner.add(oldPF);     inner.add(Box.createRigidArea(new Dimension(0, 16)));
            inner.add(newLbl);    inner.add(Box.createRigidArea(new Dimension(0, 6)));
            inner.add(newPF);     inner.add(Box.createRigidArea(new Dimension(0, 16)));
            inner.add(confirmLbl);inner.add(Box.createRigidArea(new Dimension(0, 6)));
            inner.add(confirmPF); inner.add(Box.createRigidArea(new Dimension(0, 26)));

            changeBtn = makePrimaryBtn(t(LanguageManager.SETTINGS_CHANGE_PASS), ACCENT);
            inner.add(changeBtn);
            inner.add(Box.createRigidArea(new Dimension(0, 12)));

            statusLbl = new JLabel(" ");
            statusLbl.setFont(FONT_BODY);
            statusLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            inner.add(statusLbl);

            changeBtn.addActionListener(e -> {
                user = mainFrame.getCurrentUser();
                if (user == null) {
                    showStatus(statusLbl, t(LanguageManager.MSG_MUST_LOGIN_FIRST), WARN);
                    return;
                }
                String oldP  = new String(oldPF.getPassword());
                String newP  = new String(newPF.getPassword());
                String confP = new String(confirmPF.getPassword());

                if (!user.getPassword().equals(oldP)) {
                    showStatus(statusLbl, t(LanguageManager.MSG_WRONG_OLD_PASS), WARN);
                    shake(oldPF); return;
                }
                if (newP.isEmpty()) {
                    showStatus(statusLbl, t(LanguageManager.SETTINGS_NEW_PASS) + " cannot be empty.", WARN);
                    shake(newPF); return;
                }
                if (!newP.equals(confP)) {
                    showStatus(statusLbl, t(LanguageManager.MSG_PASS_MISMATCH), WARN);
                    shake(confirmPF); return;
                }
                user.setPassword(newP);
                oldPF.setText(""); newPF.setText(""); confirmPF.setText("");
                showStatus(statusLbl, t(LanguageManager.MSG_UPDATED), ACCENT2);
            });

            add(inner, BorderLayout.NORTH);
        }

        void refresh() {
            if (hdr != null)        hdr.setText(t(LanguageManager.SETTINGS_SECURITY));
            if (oldLbl != null)     oldLbl.setText(t(LanguageManager.SETTINGS_OLD_PASS));
            if (newLbl != null)     newLbl.setText(t(LanguageManager.SETTINGS_NEW_PASS));
            if (confirmLbl != null) confirmLbl.setText(t(LanguageManager.SETTINGS_CONFIRM_PASS));
            if (changeBtn != null)  changeBtn.setText(t(LanguageManager.SETTINGS_CHANGE_PASS));
            user = mainFrame.getCurrentUser();
        }

        private void shake(JComponent comp) {
            Point orig = comp.getLocation();
            Timer t = new Timer(18, null);
            int[] n = {0};
            t.addActionListener(e2 -> {
                comp.setLocation(orig.x + (n[0] % 2 == 0 ? 8 : -8), orig.y);
                if (++n[0] > 10) { t.stop(); comp.setLocation(orig); }
            });
            t.start();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  Shared UI factories
    // ════════════════════════════════════════════════════════════════
    private JLabel makeH2(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_H2);
        l.setForeground(TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel makeSubLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(SUBTEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel makeFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(SUBTEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField makeTextField(String initial) {
        JTextField f = new JTextField(initial);
        f.setFont(FONT_BODY);
        f.setBackground(FIELD_BG);
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BDR, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Focus glow
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT, 1),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FIELD_BDR, 1),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
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
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT, 1),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FIELD_BDR, 1),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            }
        });
        return f;
    }

    private JButton makePrimaryBtn(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()
                        ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(240, 40));
        b.setPreferredSize(new Dimension(200, 40));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        return b;
    }

    private void showStatus(JLabel lbl, String msg, Color color) {
        lbl.setText(msg);
        lbl.setForeground(color);
        // Auto-clear after 3s
        Timer t = new Timer(3000, e -> lbl.setText(" "));
        t.setRepeats(false);
        t.start();
    }

    // ── Placeholder inner class to allow email field without breaking User model ──
    /** Marker interface for extended user with email support. */
    public interface ExtendedUser { String getEmail(); }
}
