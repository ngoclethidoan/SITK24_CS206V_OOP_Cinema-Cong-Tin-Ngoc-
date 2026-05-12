package GUI;

import model.LanguageManager;
import model.*;
import service.PaymentService;
import database.VoucherDatabase;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import static model.LanguageManager.t;

public class PayPanel extends JPanel {

    private final MainFrame           mainFrame;
    private final List<CartItem>      ticketItems;
    private final List<SnackCartItem> snackItems;
    private final boolean             fromCart;
    private final PaymentService      paymentService = new PaymentService();

    private String  selectedPayMethod = "CASH";
    private Voucher appliedVoucher    = null;
    private JLabel  totalLabel;
    private JLabel  discountLabel;
    private JLabel  grandLabel;

    public PayPanel(MainFrame mainFrame, List<CartItem> ticketItems,
                    List<SnackCartItem> snackItems, boolean fromCart) {
        this.mainFrame   = mainFrame;
        this.ticketItems = ticketItems;
        this.snackItems  = snackItems;
        this.fromCart    = fromCart;
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 20));
        buildUI();
        LanguageManager.getInstance().addChangeListener(this::reload);
    }

    public PayPanel(MainFrame mainFrame, List<CartItem> ticketItems, boolean fromCart) {
        this(mainFrame, ticketItems, new ArrayList<>(), fromCart);
    }

    private void reload() { removeAll(); buildUI(); revalidate(); repaint(); }

    private void buildUI() {
        add(buildTop(),    BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);
    }

    // ── TOP ──────────────────────────────────────────────────────────
    private JPanel buildTop() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(new Color(20, 20, 20));
        JButton back = new JButton(t(LanguageManager.BTN_BACK));
        back.addActionListener(e -> {
            if (!fromCart) {
                // Release reserved seats
                for (CartItem c : ticketItems)
                    synchronized (c.getSeat()) { c.getSeat().setState(Seat.State.available); }
            }
            if (fromCart) mainFrame.showCart();
            else mainFrame.showHome();
        });
        p.add(back);
        JLabel title = new JLabel("  📋 Order Summary");
        title.setForeground(Color.WHITE); title.setFont(new Font("Dialog", Font.BOLD, 15));
        p.add(title);
        return p;
    }

    // ── CENTER ───────────────────────────────────────────────────────
    private JScrollPane buildCenter() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(20, 20, 20));
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Invoice header
        panel.add(buildInvoiceHeader()); panel.add(Box.createVerticalStrut(12));

        // Tickets
        if (!ticketItems.isEmpty()) {
            panel.add(sectionLabel("🎬  Tickets")); panel.add(Box.createVerticalStrut(6));
            double tt = 0;
            for (CartItem item : ticketItems) { panel.add(buildTicketRow(item)); panel.add(Box.createVerticalStrut(4)); tt += item.getSeat().computePrice(); }
            panel.add(subtotalRow("Ticket subtotal", tt)); panel.add(Box.createVerticalStrut(10));
        }

        // Snacks
        if (!snackItems.isEmpty()) {
            panel.add(divider()); panel.add(Box.createVerticalStrut(8));
            panel.add(sectionLabel("🍿  Snacks & Drinks")); panel.add(Box.createVerticalStrut(6));
            double st = 0;
            for (SnackCartItem si : snackItems)
                for (Item it : si.getItems()) { panel.add(buildSnackRow(it)); st += it.getPrice() * it.getQuantity(); }
            panel.add(subtotalRow("Snack subtotal", st)); panel.add(Box.createVerticalStrut(10));
        }

        panel.add(divider()); panel.add(Box.createVerticalStrut(10));

        // Totals
        double base = paymentService.calcTotal(ticketItems, snackItems);
        totalLabel   = totalRow("Subtotal", base, new Color(200, 200, 200), 13, false);
        discountLabel= totalRow("Discount", 0, new Color(100, 220, 100), 13, false);
        grandLabel   = totalRow("💰 TOTAL", base, new Color(255, 215, 0), 16, true);
        panel.add(totalLabel); panel.add(discountLabel); panel.add(grandLabel);
        discountLabel.setVisible(false);
        panel.add(Box.createVerticalStrut(16));

        // Voucher
        panel.add(divider()); panel.add(Box.createVerticalStrut(10));
        panel.add(sectionLabel("🎟️  Voucher")); panel.add(Box.createVerticalStrut(8));
        panel.add(buildVoucherRow(base)); panel.add(Box.createVerticalStrut(16));

        // Payment method
        panel.add(divider()); panel.add(Box.createVerticalStrut(10));
        panel.add(sectionLabel("💳  Payment Method")); panel.add(Box.createVerticalStrut(8));
        panel.add(buildPaymentMethods());

        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null); sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private JPanel buildInvoiceHeader() {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(28, 28, 28));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60,60,60)), new EmptyBorder(12,16,12,16)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        User user = mainFrame.getCurrentUser();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        hLine(p, "🏪 CNT CINEMA", new Color(255,215,0), Font.BOLD, 14);
        hLine(p, "👤 " + (user!=null ? user.getName()+" ("+user.getUserId()+")" : "Guest"), Color.LIGHT_GRAY, Font.PLAIN, 12);
        hLine(p, "📅 " + date, new Color(150,150,150), Font.PLAIN, 11);
        return p;
    }

    private void hLine(JPanel p, String text, Color c, int style, int size) {
        JLabel l = new JLabel(text); l.setForeground(c); l.setFont(new Font("Dialog",style,size));
        l.setAlignmentX(Component.LEFT_ALIGNMENT); p.add(l);
    }

    private JPanel buildTicketRow(CartItem item) {
        JPanel row = new JPanel(new BorderLayout(8,0));
        row.setBackground(new Color(30,30,30)); row.setBorder(new EmptyBorder(6,10,6,10));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JPanel left = new JPanel(); left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS)); left.setOpaque(false);
        JLabel fl = new JLabel("🎬 " + item.getFilm().getTitle()); fl.setForeground(Color.WHITE); fl.setFont(new Font("Dialog",Font.BOLD,13));
        JLabel sl = new JLabel("  💺 Seat "+item.getSeat().getCodeSeat()+" ("+typeName(item.getSeat())+")  |  🏢 "+item.getRoom().getRoomId());
        sl.setForeground(Color.LIGHT_GRAY); sl.setFont(new Font("Dialog",Font.PLAIN,11));
        left.add(fl); left.add(sl);
        JLabel price = new JLabel(String.format("%,.0f VND", item.getSeat().computePrice()));
        price.setForeground(new Color(100,220,100)); price.setFont(new Font("Dialog",Font.BOLD,13));
        row.add(left, BorderLayout.CENTER); row.add(price, BorderLayout.EAST);
        return row;
    }

    private JPanel buildSnackRow(Item it) {
        JPanel row = new JPanel(new BorderLayout()); row.setBackground(new Color(38,30,20));
        row.setBorder(new EmptyBorder(4,10,4,10)); row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel name = new JLabel("  • " + it.getName() + "  x" + it.getQuantity()); name.setForeground(new Color(220,190,120));
        JLabel price = new JLabel(String.format("%,.0f VND", it.getPrice()*it.getQuantity())); price.setForeground(new Color(220,190,120));
        row.add(name, BorderLayout.WEST); row.add(price, BorderLayout.EAST);
        return row;
    }

    private JPanel subtotalRow(String label, double amount) {
        JPanel p = new JPanel(new BorderLayout()); p.setOpaque(false);
        p.setBorder(new EmptyBorder(2,10,2,10)); p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        JLabel l = new JLabel(label); l.setForeground(new Color(150,150,150)); l.setFont(new Font("Dialog",Font.ITALIC,11));
        JLabel a = new JLabel(String.format("%,.0f VND", amount)); a.setForeground(new Color(150,150,150)); a.setFont(new Font("Dialog",Font.ITALIC,11));
        p.add(l, BorderLayout.WEST); p.add(a, BorderLayout.EAST);
        return p;
    }

    private JLabel totalRow(String label, double amount, Color c, int size, boolean bold) {
        JPanel p = new JPanel(new BorderLayout()); p.setOpaque(false);
        p.setBorder(new EmptyBorder(4,10,4,10)); p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        // Wrap in a label — we'll update text later
        JLabel combined = new JLabel(label + "   " + String.format("%,.0f VND", amount));
        combined.setForeground(c); combined.setFont(new Font("Dialog", bold?Font.BOLD:Font.PLAIN, size));
        combined.setAlignmentX(Component.LEFT_ALIGNMENT);
        return combined;
    }

    // ── VOUCHER ──────────────────────────────────────────────────────
    private JPanel buildVoucherRow(double base) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false); p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JTextField codeField = new JTextField(12);
        codeField.setBackground(new Color(45,45,45)); codeField.setForeground(Color.WHITE);
        codeField.setCaretColor(Color.WHITE);
        codeField.setBorder(BorderFactory.createLineBorder(new Color(80,80,80)));

        JLabel statusLbl = new JLabel("");
        statusLbl.setFont(new Font("Dialog", Font.PLAIN, 11));

        JButton applyBtn = new JButton("Apply");
        applyBtn.setBackground(new Color(52,152,219)); applyBtn.setForeground(Color.WHITE);
        applyBtn.setFocusPainted(false);

        applyBtn.addActionListener(e -> {
            String code = codeField.getText().trim();
            Voucher v = VoucherDatabase.findById(code);
            if (v == null || !v.isActive()) {
                statusLbl.setForeground(new Color(220, 60, 60));
                statusLbl.setText("❌ Invalid voucher");
                appliedVoucher = null;
            } else if (base < v.getMinOrderAmount()) {
                statusLbl.setForeground(new Color(220, 150, 0));
                statusLbl.setText("⚠️ Min order: " + String.format("%,.0f VND", v.getMinOrderAmount()));
                appliedVoucher = null;
            } else {
                appliedVoucher = v;
                double discount = v.discount(base);
                double grand    = v.apply(base);
                statusLbl.setForeground(new Color(46, 204, 113));
                statusLbl.setText("✅ " + v.getName() + " — saved " + String.format("%,.0f VND", discount));
                discountLabel.setText("Discount (" + (int)v.getPercentOff() + "%)   -" + String.format("%,.0f VND", discount));
                discountLabel.setVisible(true);
                grandLabel.setText("💰 TOTAL   " + String.format("%,.0f VND", grand));
            }
        });

        // Clear button
        JButton clearBtn = new JButton("✕");
        clearBtn.setBackground(new Color(80,40,40)); clearBtn.setForeground(Color.WHITE);
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(e -> {
            appliedVoucher = null;
            codeField.setText("");
            statusLbl.setText("");
            discountLabel.setVisible(false);
            grandLabel.setText("💰 TOTAL   " + String.format("%,.0f VND", base));
        });

        p.add(new JLabel("Code:")); p.add(codeField); p.add(applyBtn); p.add(clearBtn); p.add(statusLbl);
        return p;
    }

    // ── PAYMENT METHODS ──────────────────────────────────────────────
    private JPanel buildPaymentMethods() {
        JPanel p = new JPanel(new GridLayout(1, 4, 10, 0));
        p.setBackground(new Color(20, 20, 20));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        List<String[]> methods = paymentService.getPaymentMethods();
        JToggleButton[] btns = new JToggleButton[methods.size()];
        ButtonGroup bg = new ButtonGroup();

        for (int i = 0; i < methods.size(); i++) {
            String code = methods.get(i)[0];
            JToggleButton btn = new JToggleButton(
                "<html><center>" + methods.get(i)[1] + "<br><small>" + methods.get(i)[2] + "</small></center></html>");
            btn.setForeground(Color.WHITE); btn.setFocusPainted(false);
            btn.setSelected(code.equals(selectedPayMethod));
            btn.setBackground(code.equals(selectedPayMethod) ? new Color(46,204,113) : new Color(50,50,50));
            btns[i] = btn; bg.add(btn); p.add(btn);
        }

        // Add listeners AFTER array is complete
        for (int i = 0; i < methods.size(); i++) {
            final int idx = i;
            final String code = methods.get(i)[0];
            btns[i].addActionListener(e -> {
                selectedPayMethod = code;
                for (JToggleButton b : btns) b.setBackground(new Color(50, 50, 50)); // reset all
                btns[idx].setBackground(new Color(46, 204, 113));                    // green selected
            });
        }
        return p;
    }

    // ── BOTTOM ───────────────────────────────────────────────────────
    private JPanel buildBottom() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(25,25,25)); p.setBorder(new EmptyBorder(10,20,10,20));
        JLabel payInfo = new JLabel("Paying with: " + payMethodLabel());
        payInfo.setForeground(Color.GRAY);
        JButton confirm = new JButton("✅ Confirm Payment");
        confirm.setBackground(new Color(46,204,113)); confirm.setForeground(Color.WHITE);
        confirm.setFont(new Font("Dialog",Font.BOLD,14)); confirm.setFocusPainted(false);
        confirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirm.addActionListener(e -> confirmAndBook());
        p.add(payInfo, BorderLayout.WEST); p.add(confirm, BorderLayout.EAST);
        return p;
    }

    private String payMethodLabel() {
        return switch (selectedPayMethod) {
            case "CARD"  -> "💳 Credit Card";
            case "DEBIT" -> "🏦 Debit Card";
            case "QR"    -> "📱 QR Pay";
            default      -> "💵 Cash";
        };
    }

    // ── BOOK LOGIC ───────────────────────────────────────────────────
    private void confirmAndBook() {
        User u = mainFrame.getCurrentUser();
        double base  = paymentService.calcTotal(ticketItems, snackItems);
        double grand = appliedVoucher != null ? appliedVoucher.apply(base) : base;

        int confirm = JOptionPane.showConfirmDialog(mainFrame,
            "Confirm payment of " + String.format("%,.0f VND", grand)
            + "\nvia " + payMethodLabel() + "?",
            "Confirm Payment", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        paymentService.processPayment(u, ticketItems, snackItems, fromCart);
        mainFrame.refreshCartBadge();
        showSuccess(grand);
    }

    private void showSuccess(double grand) {
        String bookingId = "BK-" + mainFrame.getCurrentUser().getUserId().toUpperCase()
            + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        StringBuilder sb = new StringBuilder();
        sb.append("✅  Booking Confirmed!\n\n");
        sb.append("🔖 Booking ID: ").append(bookingId).append("\n");
        sb.append("💳 Payment: ").append(payMethodLabel()).append("\n");
        if (appliedVoucher != null)
            sb.append("🎟️ Voucher: ").append(appliedVoucher.getName()).append(" (-").append((int)appliedVoucher.getPercentOff()).append("%)\n");
        sb.append("\n");
        if (!ticketItems.isEmpty()) {
            sb.append("🎬 Tickets:\n");
            for (CartItem c : ticketItems)
                sb.append("  • ").append(c.getFilm().getTitle()).append(" | Seat ").append(c.getSeat().getCodeSeat())
                  .append(" | ").append(String.format("%,.0f VND\n", c.getSeat().computePrice()));
        }
        if (!snackItems.isEmpty()) {
            sb.append("🍿 Snacks:\n");
            for (SnackCartItem si : snackItems)
                for (Item it : si.getItems())
                    sb.append("  • ").append(it.getName()).append(" x").append(it.getQuantity()).append("\n");
        }
        sb.append("\n💰 Total Paid: ").append(String.format("%,.0f VND", grand));

        JOptionPane.showMessageDialog(mainFrame, sb.toString(), "Payment Successful",
            JOptionPane.INFORMATION_MESSAGE);
        mainFrame.showHome();
    }

    // ── HELPERS ──────────────────────────────────────────────────────
    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text); l.setForeground(new Color(200,200,200));
        l.setFont(new Font("Dialog",Font.BOLD,13)); l.setAlignmentX(Component.LEFT_ALIGNMENT); return l;
    }

    private JSeparator divider() {
        JSeparator sep = new JSeparator(); sep.setForeground(new Color(55,55,55));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); return sep;
    }

    private String typeName(Seat seat) {
        if (seat instanceof VIPSeat)     return "VIP";  if (seat instanceof PremiumSeat) return "Premium";
        if (seat instanceof ReclineSeat) return "Recliner"; if (seat instanceof CoupleSeat) return "Couple";
        return "Standard";
    }
}
