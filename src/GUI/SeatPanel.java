    package GUI;

import model.*;
import database.RoomDatabase;

import javax.swing.*;
import java.awt.*;

public class SeatPanel extends JPanel {

    private Film film;
    private MainFrame mainFrame;

    private Seat selectedSeat;
    private JButton[][] buttons;

    private Seat[][] seats;
    private Room room;

    private JLabel lblSelected;
    private JLabel lblPrice;

    private boolean editMode;

    public SeatPanel(Film film, MainFrame mainFrame) {

        this.film = film;
        this.mainFrame = mainFrame;

        // 🔥 ALWAYS GET LATEST ROOM DATA
        this.room = RoomDatabase.getRoom(film.getRoomId());
        this.seats = room.getSeats();

        this.editMode = mainFrame.getCurrentUser() != null
                && "admin".equals(mainFrame.getCurrentUser().getName());

        setLayout(new BorderLayout());
        setBackground(new Color(19, 19, 19));

        add(createTop(), BorderLayout.NORTH);
        add(createGrid(), BorderLayout.CENTER);
        add(createBottom(), BorderLayout.SOUTH);
    }

    // ================= TOP =================
    private JPanel createTop() {

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(new Color(19, 19, 19));

        JButton back = new JButton("⬅ BACK");
        back.addActionListener(e -> mainFrame.showHome());

        JLabel title = new JLabel(" " + film.getTitle() + " - " + room.getRoomId());
        title.setForeground(Color.WHITE);

        p.add(back);
        p.add(title);

        return p;
    }

    // ================= GRID =================
    private JPanel createGrid() {

        int row = seats.length;
        int col = seats[0].length;

        buttons = new JButton[row][col];

        JPanel grid = new JPanel(new GridLayout(row, col, 6, 6));
        grid.setBackground(new Color(19, 19, 19));

        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {

                Seat seat = seats[r][c];

                JButton btn = new JButton(seat.getCodeSeat());
                btn.setFocusPainted(false);

                updateButton(btn, seat);

                int fr = r;
                int fc = c;

                btn.addActionListener(e -> clickSeat(fr, fc));

                buttons[r][c] = btn;
                grid.add(btn);
            }
        }

        return grid;
    }

    // ================= CLICK =================
    private void clickSeat(int r, int c) {

        Seat seat = seats[r][c];

        // ===== ADMIN MODE =====
        if (editMode) {

            seat.setState(
                    seat.getState() == Seat.State.booked
                            ? Seat.State.available
                            : Seat.State.booked
            );

            updateButton(buttons[r][c], seat);
            return;
        }

        // ===== USER MODE =====
        if (!seat.isAvailable()) return;

        selectedSeat = seat;

        lblSelected.setText("Seat: " + seat.getCodeSeat());
        lblPrice.setText("Price: " + seat.computePrice());
    }

    // ================= UI UPDATE =================
    private void updateButton(JButton btn, Seat seat) {

        if (!seat.isAvailable()) {
            btn.setBackground(Color.DARK_GRAY);
            btn.setEnabled(false);
            btn.setForeground(Color.GRAY);
        } else {
            btn.setEnabled(true);
            btn.setBackground(new Color(30, 130, 80));
            btn.setForeground(Color.WHITE);
        }
    }

    // ================= BOTTOM =================
    private JPanel createBottom() {

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 30, 30));

        JPanel info = new JPanel(new GridLayout(2,1));
        info.setOpaque(false);

        lblSelected = new JLabel("No seat selected");
        lblSelected.setForeground(Color.WHITE);

        lblPrice = new JLabel("");
        lblPrice.setForeground(Color.YELLOW);

        info.add(lblSelected);
        info.add(lblPrice);

        JButton add = new JButton("Next");
        
        add.addActionListener(e -> {

            if (selectedSeat == null) return;

            mainFrame.getCurrentUser().getCart().add(
                    new CartItem(film, room, selectedSeat)
            );

            mainFrame.showCart();
        });

        p.add(info, BorderLayout.WEST);
        p.add(add, BorderLayout.EAST);

        return p;
    }

    // 🔥 IMPORTANT: refresh UI when returning
    @Override
    public void addNotify() {
        super.addNotify();

        // reload latest seat state
        this.room = RoomDatabase.getRoom(film.getRoomId());
        this.seats = room.getSeats();
    }
}