package GUI;

import database.FilmDatabase;
import database.ItemDatabase;
import model.*;
import service.FilmService;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class AdminPanel extends JPanel {

    private final MainFrame mainFrame;
    private final FilmService filmService = new FilmService();

    private JTable filmTable;
    private DefaultTableModel filmModel;
    private static final String[] FILM_COLS = {"Code","Title","Duration","Price","Room","State"};

    private JTable itemTable;
    private DefaultTableModel itemModel;
    private static final String[] ITEM_COLS = {"Code","Name","Price","Qty","Type"};

    public AdminPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(19, 19, 19));
        buildUI();
    }

    private void buildUI() {
        add(buildTop(), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(new Color(30, 30, 30));
        tabs.setForeground(Color.WHITE);
        tabs.addTab("🎬 Films",  buildFilmTab());
        tabs.addTab("🍿 Snacks", buildSnackTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildTop() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(new Color(25, 25, 25));
        JButton back = new JButton("← Back");
        back.addActionListener(e -> mainFrame.showHome());
        styleBtn(back, new Color(60, 60, 60));
        JLabel title = new JLabel("🛠  Admin Panel");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Dialog", Font.BOLD, 16));
        p.add(back); p.add(Box.createHorizontalStrut(16)); p.add(title);
        return p;
    }

    // ═══════════ FILM TAB ════════════════════════════════════════════
    private JPanel buildFilmTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(19, 19, 19));
        filmModel = new DefaultTableModel(FILM_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        refreshFilmTable();
        filmTable = new JTable(filmModel);
        styleTable(filmTable);
        JScrollPane sp = new JScrollPane(filmTable);
        sp.getViewport().setBackground(new Color(30, 30, 30)); sp.setBorder(null);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 10));
        btns.setBackground(new Color(25, 25, 25));
        JButton add = new JButton("➕ Add Film"), state = new JButton("🔄 Change State"),
                del = new JButton("🗑 Delete");
        styleBtn(add, new Color(46,204,113)); styleBtn(state, new Color(52,152,219));
        styleBtn(del, new Color(192,57,43));
        add.addActionListener(e -> showAddFilmDialog());
        state.addActionListener(e -> changeFilmState());
        del.addActionListener(e -> deleteFilm());
        btns.add(add); btns.add(state); btns.add(del);
        p.add(sp, BorderLayout.CENTER); p.add(btns, BorderLayout.SOUTH);
        return p;
    }

    private void refreshFilmTable() {
        if (filmModel == null) return;
        filmModel.setRowCount(0);
        for (Film f : FilmDatabase.getFilms())
            filmModel.addRow(new Object[]{f.getCodeFilm(), f.getTitle(),
                f.getDuration()+" min", String.format("%,.0f VND", f.getPrice()),
                f.getRoomId(), f.getState()});
    }

    private void showAddFilmDialog() {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this),
            "Add New Film", Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(420, 460); d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(new Color(45, 45, 45));
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));
        form.setBackground(new Color(45, 45, 45));
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        JTextField tfCode=field(),tfTitle=field(),tfDur=field(),tfPrice=field(),
                   tfDir=field(),tfCast=field(),tfRoom=field(),tfImg=field(),tfSum=field();
        tfImg.setText("posters/");
        String[] lbls = {"Code","Title","Duration (mins)","Price (VND)","Director",
                         "Cast (| separator)","Room ID","Image path","Summary"};
        JTextField[] flds = {tfCode,tfTitle,tfDur,tfPrice,tfDir,tfCast,tfRoom,tfImg,tfSum};
        for (int i=0;i<lbls.length;i++){JLabel l=new JLabel(lbls[i]);l.setForeground(Color.LIGHT_GRAY);form.add(l);form.add(flds[i]);}
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
            d.dispose();JOptionPane.showMessageDialog(this,"Film added!");
        });
        d.add(new JScrollPane(form),BorderLayout.CENTER);d.add(br,BorderLayout.SOUTH);d.setVisible(true);
    }

    private void changeFilmState() {
        int row=filmTable.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Select a film first.");return;}
        Film film=FilmDatabase.findByCode((String)filmModel.getValueAt(row,0));
        if(film==null)return;
        Film.State chosen=(Film.State)JOptionPane.showInputDialog(this,
            "New state for \""+film.getTitle()+"\":","Change State",
            JOptionPane.QUESTION_MESSAGE,null,Film.State.values(),film.getState());
        if(chosen==null)return;
        filmService.changeState(film,chosen);filmService.saveFilms();refreshFilmTable();mainFrame.refreshUI();
    }

    private void deleteFilm() {
        int row=filmTable.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Select a film first.");return;}
        String code=(String)filmModel.getValueAt(row,0),title=(String)filmModel.getValueAt(row,1);
        if(JOptionPane.showConfirmDialog(this,"Delete \""+title+"\"?","Confirm Delete",
            JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.YES_OPTION)return;
        filmService.removeFilm(code);filmService.saveFilms();refreshFilmTable();mainFrame.refreshUI();
    }

    // ═══════════ SNACK TAB ═══════════════════════════════════════════
    private JPanel buildSnackTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(19, 19, 19));
        itemModel = new DefaultTableModel(ITEM_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        refreshItemTable();
        itemTable = new JTable(itemModel);
        styleTable(itemTable);
        JScrollPane sp = new JScrollPane(itemTable);
        sp.getViewport().setBackground(new Color(30, 30, 30)); sp.setBorder(null);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 10));
        btns.setBackground(new Color(25, 25, 25));
        JButton add=new JButton("➕ Add Item"), edit=new JButton("✏ Edit Price"),
                del=new JButton("🗑 Delete");
        styleBtn(add,new Color(46,204,113));styleBtn(edit,new Color(52,152,219));styleBtn(del,new Color(192,57,43));
        add.addActionListener(e->showAddItemDialog());
        edit.addActionListener(e->editItemPrice());
        del.addActionListener(e->deleteItem());
        btns.add(add);btns.add(edit);btns.add(del);
        p.add(sp,BorderLayout.CENTER);p.add(btns,BorderLayout.SOUTH);
        return p;
    }

    private void refreshItemTable() {
        if(itemModel==null)return;
        itemModel.setRowCount(0);
        for(Item it:ItemDatabase.getAll()){
            String type=it instanceof model.Corn?"CORN":it instanceof model.Beverage?"BEVERAGE":"COMBO";
            itemModel.addRow(new Object[]{it.getCodeItem(),it.getName(),
                String.format("%,.0f VND",it.getPrice()),it.getQuantity(),type});
        }
    }

    private void showAddItemDialog() {
        JDialog d=new JDialog(SwingUtilities.getWindowAncestor(this),
            "Add Snack/Drink",Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(340,300);d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());d.getContentPane().setBackground(new Color(45,45,45));
        JPanel form=new JPanel(new GridLayout(0,2,8,10));
        form.setBackground(new Color(45,45,45));form.setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        JTextField tfCode=field(),tfName=field(),tfPrice=field(),tfQty=field();tfQty.setText("100");
        String[] lbls={"Code (e.g. BEV05)","Name","Price (VND)","Stock Qty"};
        JTextField[] flds={tfCode,tfName,tfPrice,tfQty};
        for(int i=0;i<lbls.length;i++){JLabel l=new JLabel(lbls[i]);l.setForeground(Color.LIGHT_GRAY);form.add(l);form.add(flds[i]);}
        JLabel tl=new JLabel("Type");tl.setForeground(Color.LIGHT_GRAY);
        JComboBox<String> cb=new JComboBox<>(new String[]{"CORN","BEVERAGE","COMBO"});
        cb.setBackground(new Color(65,65,65));cb.setForeground(Color.WHITE);
        form.add(tl);form.add(cb);
        JPanel br=new JPanel(new FlowLayout(FlowLayout.RIGHT));br.setBackground(new Color(45,45,45));
        JButton save=new JButton("Save"),cancel=new JButton("Cancel");
        styleBtn(save,new Color(46,204,113));styleBtn(cancel,new Color(80,80,80));
        br.add(cancel);br.add(save);
        cancel.addActionListener(e->d.dispose());
        save.addActionListener(e->{
            String code=tfCode.getText().trim(),name=tfName.getText().trim();
            if(code.isEmpty()||name.isEmpty()){JOptionPane.showMessageDialog(d,"Code and Name required.");return;}
            double price;int qty;
            try{price=Double.parseDouble(tfPrice.getText().trim());qty=Integer.parseInt(tfQty.getText().trim());}
            catch(NumberFormatException ex){JOptionPane.showMessageDialog(d,"Price and Qty must be numbers.");return;}
            String type=(String)cb.getSelectedItem();
            Item item="CORN".equals(type)?new model.Corn(code,name,price,qty):new model.Beverage(code,name,price,qty);
            ItemDatabase.addOrUpdate(item);ItemDatabase.saveToCSV();refreshItemTable();
            d.dispose();JOptionPane.showMessageDialog(this,"Item added!");
        });
        d.add(new JScrollPane(form),BorderLayout.CENTER);d.add(br,BorderLayout.SOUTH);d.setVisible(true);
    }

    private void editItemPrice() {
        int row=itemTable.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Select an item first.");return;}
        String code=(String)itemModel.getValueAt(row,0),name=(String)itemModel.getValueAt(row,1);
        String input=JOptionPane.showInputDialog(this,"New price for \""+name+"\" (VND):","Edit Price",JOptionPane.QUESTION_MESSAGE);
        if(input==null||input.trim().isEmpty())return;
        try{
            double np=Double.parseDouble(input.trim());
            Item ex=ItemDatabase.getById(code);if(ex==null)return;
            Item up=ex instanceof model.Corn?new model.Corn(code,ex.getName(),np,ex.getQuantity())
                                            :new model.Beverage(code,ex.getName(),np,ex.getQuantity());
            ItemDatabase.addOrUpdate(up);ItemDatabase.saveToCSV();refreshItemTable();
        }catch(NumberFormatException ex){JOptionPane.showMessageDialog(this,"Invalid price.");}
    }

    private void deleteItem() {
        int row=itemTable.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Select an item first.");return;}
        String code=(String)itemModel.getValueAt(row,0),name=(String)itemModel.getValueAt(row,1);
        if(JOptionPane.showConfirmDialog(this,"Delete \""+name+"\"?","Confirm Delete",
            JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.YES_OPTION)return;
        ItemDatabase.remove(code);ItemDatabase.saveToCSV();refreshItemTable();
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
        btn.setBackground(bg);btn.setForeground(Color.WHITE);btn.setFont(new Font("Dialog",Font.BOLD,13));
        btn.setFocusPainted(false);btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
