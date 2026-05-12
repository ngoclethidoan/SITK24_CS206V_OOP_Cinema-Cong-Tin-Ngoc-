package GUI;

import database.*;
import model.*;
import service.FilmService;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {

    private final MainFrame mainFrame;
    private final FilmService filmService = new FilmService();

    // Film tab
    private JTable filmTable; private DefaultTableModel filmModel;
    private static final String[] FILM_COLS = {"Code","Title","Duration","Price","Room","State"};

    // Snack tab
    private JTable itemTable; private DefaultTableModel itemModel;
    private static final String[] ITEM_COLS = {"Code","Name","Price","Qty","Type"};

    // Users tab
    private JTable userTable; private DefaultTableModel userModel;
    private static final String[] USER_COLS = {"UserID","Name","Admin","VIP"};

    // Vouchers tab
    private JTable voucherTable; private DefaultTableModel voucherModel;
    private static final String[] VOUCHER_COLS = {"Code","Name","% Off","Min Order","Active"};

    // Bookings tab
    private JTable bookingTable; private DefaultTableModel bookingModel;
    private static final String[] BOOKING_COLS = {"Booking ID","User","Film","Seat","Total"};

    public AdminPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout()); setBackground(new Color(19, 19, 19));
        buildUI();
    }

    private void buildUI() {
        add(buildTop(), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(new Color(30, 30, 30)); tabs.setForeground(Color.WHITE);
        tabs.addTab("🎬 Films",    buildFilmTab());
        tabs.addTab("🍿 Snacks",   buildSnackTab());
        tabs.addTab("👥 Users",    buildUsersTab());
        tabs.addTab("🎟️ Vouchers", buildVouchersTab());
        tabs.addTab("📋 Bookings", buildBookingsTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildTop() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(new Color(25, 25, 25));
        JButton back = new JButton("← Back");
        back.addActionListener(e -> mainFrame.showHome()); styleBtn(back, new Color(60,60,60));
        JLabel title = new JLabel("🛠  Admin Panel");
        title.setForeground(Color.WHITE); title.setFont(new Font("Dialog", Font.BOLD, 16));
        p.add(back); p.add(Box.createHorizontalStrut(16)); p.add(title);
        return p;
    }

    // ══════════ FILM TAB ════════════════════════════════════════════
    private void editFilm() {
    int row = filmTable.getSelectedRow();
    if (row < 0) { JOptionPane.showMessageDialog(this, "Select a film first."); return; }
    String code = (String) filmModel.getValueAt(row, 0);
    Film ex = FilmDatabase.findByCode(code);
    if (ex == null) return;

    JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this),
        "Edit Film", Dialog.ModalityType.APPLICATION_MODAL);
    d.setSize(420, 480); d.setLocationRelativeTo(this);
    d.setLayout(new BorderLayout());
    d.getContentPane().setBackground(new Color(45, 45, 45));

    JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));
    form.setBackground(new Color(45, 45, 45));
    form.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

    JTextField tfTitle = field(); tfTitle.setText(ex.getTitle());
    JTextField tfDur   = field(); tfDur.setText(String.valueOf(ex.getDuration()));
    JTextField tfPrice = field(); tfPrice.setText(String.valueOf(ex.getPrice()));
    JTextField tfDir   = field(); tfDir.setText(ex.getDirector());
    JTextField tfCast  = field(); tfCast.setText(ex.getCast());
    JTextField tfRoom  = field(); tfRoom.setText(ex.getRoomId());
    JTextField tfImg   = field(); tfImg.setText(ex.getImagePath());
    JTextField tfSum   = field(); tfSum.setText(ex.getSummary());
    JComboBox<Film.State> cb = new JComboBox<>(Film.State.values());
    cb.setSelectedItem(ex.getState());
    cb.setBackground(new Color(65,65,65)); cb.setForeground(Color.WHITE);

    form.add(label("Code (read-only)")); form.add(label(code));
    form.add(label("Title"));    form.add(tfTitle);
    form.add(label("Duration")); form.add(tfDur);
    form.add(label("Price"));    form.add(tfPrice);
    form.add(label("Director")); form.add(tfDir);
    form.add(label("Cast"));     form.add(tfCast);
    form.add(label("Room ID"));  form.add(tfRoom);
    form.add(label("Image"));    form.add(tfImg);
    form.add(label("Summary"));  form.add(tfSum);
    form.add(label("State"));    form.add(cb);

    JPanel br = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    br.setBackground(new Color(45, 45, 45));
    JButton save = new JButton("Save"), cancel = new JButton("Cancel");
    styleBtn(save, new Color(46,204,113)); styleBtn(cancel, new Color(80,80,80));
    br.add(cancel); br.add(save);
    cancel.addActionListener(e -> d.dispose());
    save.addActionListener(e -> {
        String title = tfTitle.getText().trim();
        if (title.isEmpty()) { JOptionPane.showMessageDialog(d, "Title required."); return; }
        int dur; double price;
        try {
            dur   = Integer.parseInt(tfDur.getText().trim());
            price = Double.parseDouble(tfPrice.getText().trim());
        } catch (NumberFormatException ex2) {
            JOptionPane.showMessageDialog(d, "Duration and Price must be numbers."); return;
        }
        Film updated = new Film(code, title, dur, price,
            tfDir.getText().trim(), tfCast.getText().trim(),
            tfSum.getText().trim(), tfImg.getText().trim(),
            tfRoom.getText().trim(), (Film.State) cb.getSelectedItem());
        filmService.updateFilm(updated);
        filmService.saveFilms();
        refreshFilmTable(); mainFrame.refreshUI();
        d.dispose();
        JOptionPane.showMessageDialog(this, "Film updated!");
    });
    d.add(new JScrollPane(form), BorderLayout.CENTER);
    d.add(br, BorderLayout.SOUTH);
    d.setVisible(true);
}
    
    private JPanel buildFilmTab() {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(new Color(19,19,19));
        filmModel = new DefaultTableModel(FILM_COLS, 0) {@Override
 public boolean isCellEditable(int r,int c){return false;} };
        refreshFilmTable();
        filmTable = new JTable(filmModel); styleTable(filmTable);
        JScrollPane sp = new JScrollPane(filmTable); sp.getViewport().setBackground(new Color(30,30,30)); sp.setBorder(null);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER,14,10)); btns.setBackground(new Color(25,25,25));
        JButton add=new JButton("➕ Add Film"),edit=new JButton("🔄✏ Edit Film"),del=new JButton("🗑 Delete");
        styleBtn(add,new Color(46,204,113));styleBtn(edit,new Color(150, 100, 200));styleBtn(del,new Color(192,57,43));
        edit.addActionListener(e -> editFilm());
        add.addActionListener(e->showAddFilmDialog()); edit.addActionListener(e->editFilm()); del.addActionListener(e->deleteFilm());
        btns.add(add);btns.add(edit);btns.add(del);
        p.add(sp,BorderLayout.CENTER);p.add(btns,BorderLayout.SOUTH);
        return p;
    }

    private void refreshFilmTable() {
        if(filmModel==null)return; filmModel.setRowCount(0);
        for(Film f:FilmDatabase.getFilms())
            filmModel.addRow(new Object[]{f.getCodeFilm(),f.getTitle(),f.getDuration()+" min",
                String.format("%,.0f VND",f.getPrice()),f.getRoomId(),f.getState()});
    }

    private void showAddFilmDialog() {
        JDialog d=new JDialog(SwingUtilities.getWindowAncestor(this),"Add New Film",Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(420,480);d.setLocationRelativeTo(this);d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(new Color(45,45,45));
        JPanel form=new JPanel(new GridLayout(0,2,8,10));form.setBackground(new Color(45,45,45));
        form.setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        JTextField tfCode=field(),tfTitle=field(),tfDur=field(),tfPrice=field(),
            tfDir=field(),tfCast=field(),tfRoom=field(),tfImg=field(),tfSum=field();
        tfImg.setText("posters/");
        String[] lbls={"Code","Title","Duration (mins)","Price (VND)","Director","Cast (| separator)","Room ID","Image path","Summary"};
        JTextField[] flds={tfCode,tfTitle,tfDur,tfPrice,tfDir,tfCast,tfRoom,tfImg,tfSum};
        for(int i=0;i<lbls.length;i++){JLabel l=new JLabel(lbls[i]);l.setForeground(Color.LIGHT_GRAY);form.add(l);form.add(flds[i]);}
        JLabel sl=new JLabel("State");sl.setForeground(Color.LIGHT_GRAY);
        JComboBox<Film.State> cb=new JComboBox<>(Film.State.values());
        cb.setBackground(new Color(65,65,65));cb.setForeground(Color.WHITE);
        form.add(sl);form.add(cb);
        JPanel br=new JPanel(new FlowLayout(FlowLayout.RIGHT));br.setBackground(new Color(45,45,45));
        JButton save=new JButton("Save"),cancel=new JButton("Cancel");
        styleBtn(save,new Color(46,204,113));styleBtn(cancel,new Color(80,80,80));
        br.add(cancel);br.add(save);
        cancel.addActionListener(e->d.dispose());
        save.addActionListener(e->{
            String code=tfCode.getText().trim(),title=tfTitle.getText().trim();
            if(code.isEmpty()||title.isEmpty()){JOptionPane.showMessageDialog(d,"Code and Title required.");return;}
            if(FilmDatabase.findByCode(code)!=null){JOptionPane.showMessageDialog(d,"Code already exists.");return;}
            int dur;double price;
            try{dur=Integer.parseInt(tfDur.getText().trim());price=Double.parseDouble(tfPrice.getText().trim());}
            catch(NumberFormatException ex){JOptionPane.showMessageDialog(d,"Duration and Price must be numbers.");return;}
            Film film=new Film(code,title,dur,price,tfDir.getText().trim(),tfCast.getText().trim(),
                tfSum.getText().trim(),tfImg.getText().trim(),tfRoom.getText().trim(),(Film.State)cb.getSelectedItem());
            filmService.addFilm(film);filmService.saveFilms();refreshFilmTable();mainFrame.refreshUI();
            d.dispose();JOptionPane.showMessageDialog(this,"Film added successfully!");
        });
        d.add(new JScrollPane(form),BorderLayout.CENTER);d.add(br,BorderLayout.SOUTH);d.setVisible(true);
    }
    
    private void editItem() {
    int row = itemTable.getSelectedRow();
    if (row < 0) { JOptionPane.showMessageDialog(this, "Select an item first."); return; }

    String code = (String) itemModel.getValueAt(row, 0);
    Item ex = ItemDatabase.getById(code);
    if (ex == null) return;

    JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this),
        "Edit Item", Dialog.ModalityType.APPLICATION_MODAL);
    d.setSize(340, 300); d.setLocationRelativeTo(this);
    d.setLayout(new BorderLayout());
    d.getContentPane().setBackground(new Color(45, 45, 45));

    JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));
    form.setBackground(new Color(45, 45, 45));
    form.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

    // Pre-fill all fields with current values
    JTextField tfName  = field(); tfName.setText(ex.getName());
    JTextField tfPrice = field(); tfPrice.setText(String.valueOf(ex.getPrice()));
    JTextField tfQty   = field(); tfQty.setText(String.valueOf(ex.getQuantity()));

    String currentType = ex instanceof Corn ? "CORN" : "BEVERAGE";
    JComboBox<String> cb = new JComboBox<>(new String[]{"CORN", "BEVERAGE", "COMBO"});
    cb.setSelectedItem(currentType);
    cb.setBackground(new Color(65, 65, 65)); cb.setForeground(Color.WHITE);

//    String[] lbls = {"Name", "Price (VND)", "Stock Qty", "Type"};
    form.add(label("Code (read-only)")); form.add(label(code)); // code not editable
    form.add(label("Name"));  form.add(tfName);
    form.add(label("Price")); form.add(tfPrice);
    form.add(label("Qty"));   form.add(tfQty);
    form.add(label("Type"));  form.add(cb);

    JPanel br = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    br.setBackground(new Color(45, 45, 45));
    JButton save = new JButton("Save"), cancel = new JButton("Cancel");
    styleBtn(save, new Color(46, 204, 113)); styleBtn(cancel, new Color(80, 80, 80));
    br.add(cancel); br.add(save);

    cancel.addActionListener(e -> d.dispose());
    save.addActionListener(e -> {
        String name = tfName.getText().trim();
        if (name.isEmpty()) { JOptionPane.showMessageDialog(d, "Name required."); return; }
        double price; int qty;
        try {
            price = Double.parseDouble(tfPrice.getText().trim());
            qty   = Integer.parseInt(tfQty.getText().trim());
        } catch (NumberFormatException ex2) {
            JOptionPane.showMessageDialog(d, "Price and Qty must be numbers."); return;
        }
        String type = (String) cb.getSelectedItem();
        Item updated = "CORN".equals(type)
            ? new Corn(code, name, price, qty)
            : new Beverage(code, name, price, qty);
        ItemDatabase.addOrUpdate(updated);
        ItemDatabase.saveToCSV();
        refreshItemTable();
        d.dispose();
        JOptionPane.showMessageDialog(this, "Item updated!");
    });

    d.add(new JScrollPane(form), BorderLayout.CENTER);
    d.add(br, BorderLayout.SOUTH);
    d.setVisible(true);
}

// helper for read-only label in form
private JLabel label(String text) {
    JLabel l = new JLabel(text); l.setForeground(Color.LIGHT_GRAY); return l;
}

//    private void changeFilmState() {
//        int row=filmTable.getSelectedRow();
//        if(row<0){JOptionPane.showMessageDialog(this,"Select a film first.");return;}
//        Film film=FilmDatabase.findByCode((String)filmModel.getValueAt(row,0));if(film==null)return;
//        Film.State chosen=(Film.State)JOptionPane.showInputDialog(this,"New state for \""+film.getTitle()+"\":","Change State",
//            JOptionPane.QUESTION_MESSAGE,null,Film.State.values(),film.getState());
//        if(chosen==null)return;
//        filmService.changeState(film,chosen);filmService.saveFilms();refreshFilmTable();mainFrame.refreshUI();
//    }

    private void deleteFilm() {
        int row=filmTable.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Select a film first.");return;}
        String code=(String)filmModel.getValueAt(row,0),title=(String)filmModel.getValueAt(row,1);
        if(JOptionPane.showConfirmDialog(this,"Delete \""+title+"\"?","Confirm Delete",
            JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.YES_OPTION)return;
        filmService.removeFilm(code);filmService.saveFilms();refreshFilmTable();mainFrame.refreshUI();
    }

    // ══════════ SNACK TAB ════════════════════════════════════════════
    private JPanel buildSnackTab() {
        JPanel p=new JPanel(new BorderLayout());p.setBackground(new Color(19,19,19));
        itemModel=new DefaultTableModel(ITEM_COLS,0){@Override
        public boolean isCellEditable(int r,int c){return false;}};
        refreshItemTable();
        itemTable=new JTable(itemModel);styleTable(itemTable);
        JScrollPane sp=new JScrollPane(itemTable);sp.getViewport().setBackground(new Color(30,30,30));sp.setBorder(null);
        JPanel btns=new JPanel(new FlowLayout(FlowLayout.CENTER,14,10));btns.setBackground(new Color(25,25,25));
        JButton add=new JButton("➕ Add"),edit=new JButton("✏ Edit"),del=new JButton("🗑 Delete");
        styleBtn(add,new Color(46,204,113));styleBtn(edit,new Color(52,152,219));styleBtn(del,new Color(192,57,43));
        add.addActionListener(e->showAddItemDialog());edit.addActionListener(e->editItem());del.addActionListener(e->deleteItem());
        btns.add(add);btns.add(edit);btns.add(del);
        p.add(sp,BorderLayout.CENTER);p.add(btns,BorderLayout.SOUTH);
        return p;
    }

    private void refreshItemTable() {
        if(itemModel==null)return;itemModel.setRowCount(0);
        for(Item it:ItemDatabase.getAll()){
            String type=it instanceof Corn?"CORN":it instanceof Beverage?"BEVERAGE":"COMBO";
            itemModel.addRow(new Object[]{it.getCodeItem(),it.getName(),String.format("%,.0f VND",it.getPrice()),it.getQuantity(),type});
        }
    }

    private void showAddItemDialog() {
        JDialog d=new JDialog(SwingUtilities.getWindowAncestor(this),"Add Snack/Drink",Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(340,300);d.setLocationRelativeTo(this);d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(new Color(45,45,45));
        JPanel form=new JPanel(new GridLayout(0,2,8,10));form.setBackground(new Color(45,45,45));
        form.setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        JTextField tfCode=field(),tfName=field(),tfPrice=field(),tfQty=field();tfQty.setText("100");
        String[] lbls={"Code (e.g. BEV05)","Name","Price (VND)","Stock Qty"};
        JTextField[] flds={tfCode,tfName,tfPrice,tfQty};
        for(int i=0;i<lbls.length;i++){JLabel l=new JLabel(lbls[i]);l.setForeground(Color.LIGHT_GRAY);form.add(l);form.add(flds[i]);}
        JLabel tl=new JLabel("Type");tl.setForeground(Color.LIGHT_GRAY);
        JComboBox<String> cb=new JComboBox<>(new String[]{"CORN","BEVERAGE","COMBO"});
        cb.setBackground(new Color(65,65,65));cb.setForeground(Color.WHITE);form.add(tl);form.add(cb);
        JPanel br=new JPanel(new FlowLayout(FlowLayout.RIGHT));br.setBackground(new Color(45,45,45));
        JButton save=new JButton("Save"),cancel=new JButton("Cancel");
        styleBtn(save,new Color(46,204,113));styleBtn(cancel,new Color(80,80,80));br.add(cancel);br.add(save);
        cancel.addActionListener(e->d.dispose());
        save.addActionListener(e->{
            String code=tfCode.getText().trim(),name=tfName.getText().trim();
            if(code.isEmpty()||name.isEmpty()){JOptionPane.showMessageDialog(d,"Code and Name required.");return;}
            double price;int qty;
            try{price=Double.parseDouble(tfPrice.getText().trim());qty=Integer.parseInt(tfQty.getText().trim());}
            catch(NumberFormatException ex){JOptionPane.showMessageDialog(d,"Price and Qty must be numbers.");return;}
            String type=(String)cb.getSelectedItem();
            Item item="CORN".equals(type)?new Corn(code,name,price,qty):new Beverage(code,name,price,qty);
            ItemDatabase.addOrUpdate(item);ItemDatabase.saveToCSV();refreshItemTable();
            d.dispose();JOptionPane.showMessageDialog(this,"Item added!");
        });
        d.add(new JScrollPane(form),BorderLayout.CENTER);d.add(br,BorderLayout.SOUTH);d.setVisible(true);
    }



    private void deleteItem() {
        int row=itemTable.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Select an item first.");return;}
        String code=(String)itemModel.getValueAt(row,0),name=(String)itemModel.getValueAt(row,1);
        if(JOptionPane.showConfirmDialog(this,"Delete \""+name+"\"?","Confirm Delete",
            JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.YES_OPTION)return;
        ItemDatabase.remove(code);ItemDatabase.saveToCSV();refreshItemTable();
    }

    // ══════════ USERS TAB ════════════════════════════════════════════
    private JPanel buildUsersTab() {
        JPanel p=new JPanel(new BorderLayout());p.setBackground(new Color(19,19,19));
        userModel=new DefaultTableModel(USER_COLS,0){public boolean isCellEditable(int r,int c){return false;}};
        refreshUserTable();
        userTable=new JTable(userModel);styleTable(userTable);
        JScrollPane sp=new JScrollPane(userTable);sp.getViewport().setBackground(new Color(30,30,30));sp.setBorder(null);

        JPanel btns=new JPanel(new FlowLayout(FlowLayout.CENTER,14,10));btns.setBackground(new Color(25,25,25));
        JButton toggleAdmin=new JButton("🔑 Toggle Admin"),toggleVip=new JButton("⭐ Toggle VIP"),
                deleteUser=new JButton("🗑 Delete User");
        styleBtn(toggleAdmin,new Color(52,152,219));styleBtn(toggleVip,new Color(200,150,0));styleBtn(deleteUser,new Color(192,57,43));

        toggleAdmin.addActionListener(e->toggleUserAdmin());
        toggleVip.addActionListener(e->toggleUserVip());
        deleteUser.addActionListener(e->deleteUser());

        btns.add(toggleAdmin);btns.add(toggleVip);btns.add(deleteUser);
        p.add(sp,BorderLayout.CENTER);p.add(btns,BorderLayout.SOUTH);
        return p;
    }

    private void refreshUserTable() {
        if(userModel==null)return;userModel.setRowCount(0);
        for(User u:UserDatabase.getAll())
            userModel.addRow(new Object[]{u.getUserId(),u.getName(),u.isAdmin()?"✅":"❌",u.isVIP()?"⭐":"—"});
    }

    private void toggleUserAdmin() {
        int row=userTable.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Select a user first.");return;}
        String id=(String)userModel.getValueAt(row,0);
        User u=findUser(id);if(u==null)return;
        if(u.getUserId().equalsIgnoreCase("admin")){JOptionPane.showMessageDialog(this,"Cannot change the main admin.");return;}
        u.setAdmin(!u.isAdmin());
        UserDatabase.save();refreshUserTable();
        JOptionPane.showMessageDialog(this,"Admin status updated for "+u.getName());
    }

    private void toggleUserVip() {
        int row=userTable.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Select a user first.");return;}
        User u=findUser((String)userModel.getValueAt(row,0));if(u==null)return;
        if(u.isVIP())u.setVIP(false); else u.setVIP(true);
        UserDatabase.save();refreshUserTable();
        JOptionPane.showMessageDialog(this,"VIP status updated for "+u.getName());
    }

    private void deleteUser() {
        int row=userTable.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Select a user first.");return;}
        String id=(String)userModel.getValueAt(row,0),name=(String)userModel.getValueAt(row,1);
        if(id.equalsIgnoreCase("admin")){JOptionPane.showMessageDialog(this,"Cannot delete the main admin.");return;}
        if(JOptionPane.showConfirmDialog(this,"Delete user \""+name+"\"? This cannot be undone.","Confirm",
            JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.YES_OPTION)return;
        UserDatabase.removeUser(id);UserDatabase.save();refreshUserTable();
    }

    private User findUser(String id) {
        for(User u:UserDatabase.getAll()) if(u.getUserId().equalsIgnoreCase(id)) return u;
        return null;
    }

    // ══════════ VOUCHERS TAB ══════════════════════════════════════════
    private void editVoucher() {
    int row = voucherTable.getSelectedRow();
    if (row < 0) { JOptionPane.showMessageDialog(this, "Select a voucher first."); return; }
    String id = (String) voucherModel.getValueAt(row, 0);
    Voucher old = VoucherDatabase.findById(id);
    if (old == null) return;

    JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this),
        "Edit Voucher", Dialog.ModalityType.APPLICATION_MODAL);
    d.setSize(360, 280); d.setLocationRelativeTo(this);
    d.setLayout(new BorderLayout());
    d.getContentPane().setBackground(new Color(45, 45, 45));

    JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));
    form.setBackground(new Color(45, 45, 45));
    form.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

    JTextField tfName = field(); tfName.setText(old.getName());
    JTextField tfPct  = field(); tfPct.setText(String.valueOf(old.getPercentOff()));
    JTextField tfMin  = field(); tfMin.setText(String.valueOf(old.getMinOrderAmount()));

    form.add(label("Code (read-only)")); form.add(label(id));
    form.add(label("Display Name"));    form.add(tfName);
    form.add(label("Discount % (0-100)")); form.add(tfPct);
    form.add(label("Min Order (VND)")); form.add(tfMin);

    JPanel br = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    br.setBackground(new Color(45, 45, 45));
    JButton save = new JButton("Save"), cancel = new JButton("Cancel");
    styleBtn(save, new Color(46,204,113)); styleBtn(cancel, new Color(80,80,80));
    br.add(cancel); br.add(save);
    cancel.addActionListener(e -> d.dispose());
    save.addActionListener(e -> {
        String name = tfName.getText().trim();
        if (name.isEmpty()) { JOptionPane.showMessageDialog(d, "Name required."); return; }
        double pct, min;
        try {
            pct = Double.parseDouble(tfPct.getText().trim());
            min = Double.parseDouble(tfMin.getText().trim());
        } catch (NumberFormatException ex2) {
            JOptionPane.showMessageDialog(d, "% and Min must be numbers."); return;
        }
        if (pct < 0 || pct > 100) { JOptionPane.showMessageDialog(d, "Discount must be 0-100."); return; }
        VoucherDatabase.add(new Voucher(id, name, pct, min, old.isActive()));
        VoucherDatabase.saveToCSV();
        refreshVoucherTable();
        d.dispose();
        JOptionPane.showMessageDialog(this, "Voucher updated!");
    });
    d.add(new JScrollPane(form), BorderLayout.CENTER);
    d.add(br, BorderLayout.SOUTH);
    d.setVisible(true);
}
    
    private JPanel buildVouchersTab() {
        JPanel p=new JPanel(new BorderLayout());p.setBackground(new Color(19,19,19));
        voucherModel=new DefaultTableModel(VOUCHER_COLS,0){public boolean isCellEditable(int r,int c){return false;}};
        refreshVoucherTable();
        voucherTable=new JTable(voucherModel);styleTable(voucherTable);
        JScrollPane sp=new JScrollPane(voucherTable);sp.getViewport().setBackground(new Color(30,30,30));sp.setBorder(null);

        JPanel btns=new JPanel(new FlowLayout(FlowLayout.CENTER,14,10));btns.setBackground(new Color(25,25,25));
        JButton add=new JButton("➕ Add Voucher"),edit=new JButton("🔄✏ Edit"),del=new JButton("🗑 Delete");
        styleBtn(add,new Color(46,204,113));styleBtn(edit,new Color(52,152,219));styleBtn(del,new Color(192,57,43));
        add.addActionListener(e->showAddVoucherDialog());
        edit.addActionListener(e->editVoucher());
        del.addActionListener(e->deleteVoucher());
        btns.add(add);btns.add(edit);btns.add(del);
        p.add(sp,BorderLayout.CENTER);p.add(btns,BorderLayout.SOUTH);
        return p;
    }

    private void refreshVoucherTable() {
        if(voucherModel==null)return;voucherModel.setRowCount(0);
        for(Voucher v:VoucherDatabase.getAll())
            voucherModel.addRow(new Object[]{v.getVoucherId(),v.getName(),
                (int)v.getPercentOff()+"%",String.format("%,.0f VND",v.getMinOrderAmount()),
                v.isActive()?"✅ Active":"❌ Inactive"});
    }

    private void showAddVoucherDialog() {
        JDialog d=new JDialog(SwingUtilities.getWindowAncestor(this),"Add Voucher",Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(360,300);d.setLocationRelativeTo(this);d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(new Color(45,45,45));
        JPanel form=new JPanel(new GridLayout(0,2,8,10));form.setBackground(new Color(45,45,45));
        form.setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        JTextField tfId=field(),tfName=field(),tfPct=field(),tfMin=field();tfMin.setText("0");
        String[] lbls={"Voucher Code","Display Name","Discount % (0-100)","Min Order (VND)"};
        JTextField[] flds={tfId,tfName,tfPct,tfMin};
        for(int i=0;i<lbls.length;i++){JLabel l=new JLabel(lbls[i]);l.setForeground(Color.LIGHT_GRAY);form.add(l);form.add(flds[i]);}
        JPanel br=new JPanel(new FlowLayout(FlowLayout.RIGHT));br.setBackground(new Color(45,45,45));
        JButton save=new JButton("Save"),cancel=new JButton("Cancel");
        styleBtn(save,new Color(46,204,113));styleBtn(cancel,new Color(80,80,80));br.add(cancel);br.add(save);
        cancel.addActionListener(e->d.dispose());
        save.addActionListener(e->{
            String id=tfId.getText().trim().toUpperCase(),name=tfName.getText().trim();
            if(id.isEmpty()||name.isEmpty()){JOptionPane.showMessageDialog(d,"Code and Name required.");return;}
            double pct,min;
            try{pct=Double.parseDouble(tfPct.getText().trim());min=Double.parseDouble(tfMin.getText().trim());}
            catch(NumberFormatException ex){JOptionPane.showMessageDialog(d,"% and Min must be numbers.");return;}
            if(pct<0||pct>100){JOptionPane.showMessageDialog(d,"Discount must be 0-100.");return;}
            VoucherDatabase.add(new Voucher(id,name,pct,min,true));
            VoucherDatabase.saveToCSV();refreshVoucherTable();
            d.dispose();JOptionPane.showMessageDialog(this,"Voucher added!");
        });
        d.add(new JScrollPane(form),BorderLayout.CENTER);d.add(br,BorderLayout.SOUTH);d.setVisible(true);
    }

    private void toggleVoucher() {
        int row=voucherTable.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Select a voucher first.");return;}
        String id=(String)voucherModel.getValueAt(row,0);
        Voucher old=VoucherDatabase.findById(id);if(old==null)return;
        VoucherDatabase.add(new Voucher(old.getVoucherId(),old.getName(),
            old.getPercentOff(),old.getMinOrderAmount(),!old.isActive()));
        VoucherDatabase.saveToCSV();refreshVoucherTable();
    }

    private void deleteVoucher() {
        int row=voucherTable.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Select a voucher first.");return;}
        String id=(String)voucherModel.getValueAt(row,0);
        if(JOptionPane.showConfirmDialog(this,"Delete voucher "+id+"?","Confirm",
            JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        VoucherDatabase.remove(id);VoucherDatabase.saveToCSV();refreshVoucherTable();
    }

    // ══════════ BOOKINGS TAB ═════════════════════════════════════════
    private JPanel buildBookingsTab() {
        JPanel p=new JPanel(new BorderLayout());p.setBackground(new Color(19,19,19));
        bookingModel=new DefaultTableModel(BOOKING_COLS,0){public boolean isCellEditable(int r,int c){return false;}};
        refreshBookingTable();
        bookingTable=new JTable(bookingModel);styleTable(bookingTable);
        JScrollPane sp=new JScrollPane(bookingTable);sp.getViewport().setBackground(new Color(30,30,30));sp.setBorder(null);

        // Revenue stats bar
        JPanel stats=new JPanel(new FlowLayout(FlowLayout.LEFT,20,8));stats.setBackground(new Color(25,25,25));
        List<String[]> all=BookingDatabase.readAll();
        double revenue=0;int count=0;
        for(String[] row:all){
            String status=row.length>BookingDatabase.COL_STATUS?row[BookingDatabase.COL_STATUS]:"";
            if(BookingDatabase.STATUS_PAID.equals(status)){
                revenue+=BookingDatabase.totalPrice(row);count++;
            }
        }
        JLabel lCount=new JLabel("📋 Total Bookings: "+count);lCount.setForeground(Color.WHITE);
        JLabel lRev=new JLabel("💰 Total Revenue: "+String.format("%,.0f VND",revenue));
        lRev.setForeground(new Color(255,215,0));lRev.setFont(new Font("Dialog",Font.BOLD,13));
        JButton refresh=new JButton("🔄 Refresh");styleBtn(refresh,new Color(52,152,219));
        refresh.addActionListener(e->refreshBookingTable());
        stats.add(lCount);stats.add(lRev);stats.add(refresh);

        p.add(sp,BorderLayout.CENTER);p.add(stats,BorderLayout.SOUTH);
        return p;
    }

    private void refreshBookingTable() {
        if(bookingModel==null)return;bookingModel.setRowCount(0);
        for(String[] row:BookingDatabase.readAll()){
            String status=row.length>BookingDatabase.COL_STATUS?row[BookingDatabase.COL_STATUS]:"";
            if(!BookingDatabase.STATUS_PAID.equals(status))continue;
            String bookingId=BookingDatabase.getBookingId(row);
            Film film=BookingDatabase.resolveFilm(row);
            Seat seat=BookingDatabase.resolveSeat(row);
            double total=BookingDatabase.totalPrice(row);
            bookingModel.addRow(new Object[]{
                bookingId, row[BookingDatabase.COL_USER],
                film!=null?film.getTitle():"—",
                seat!=null?seat.getCodeSeat():"—",
                String.format("%,.0f VND",total)
            });
        }
    }

    // ── HELPERS ──────────────────────────────────────────────────────
    private void styleTable(JTable t) {
        t.setBackground(new Color(30,30,30));t.setForeground(Color.WHITE);
        t.setGridColor(new Color(55,55,55));t.setSelectionBackground(new Color(52,152,219));
        t.setRowHeight(28);t.getTableHeader().setBackground(new Color(40,40,40));
        t.getTableHeader().setForeground(Color.WHITE);t.getTableHeader().setFont(new Font("Dialog",Font.BOLD,13));
    }
    private JTextField field(){
        JTextField f=new JTextField();f.setBackground(new Color(65,65,65));f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);f.setBorder(BorderFactory.createLineBorder(new Color(90,90,90)));return f;
    }
    private void styleBtn(JButton btn,Color bg){
        btn.setBackground(bg);btn.setForeground(Color.WHITE);btn.setFont(new Font("Dialog",Font.BOLD,12));
        btn.setFocusPainted(false);btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
