package Test;

import model.*;
import service.*;
import exception.*;
import database.VoucherDatabase;
import java.util.ArrayList;
import java.util.List;

public class CinemaTest {

    static int passed = 0, failed = 0;

    public static void main(String[] args) {

        System.out.println("══════════════════════════════════════");
        System.out.println("   CNT CINEMA — TEST SUITE");
        System.out.println("══════════════════════════════════════\n");

        // ── 1. POLYMORPHISM: computePrice() ──────────────────────────
        System.out.println("── 1. Polymorphism (seat prices) ──");
        double base = 100_000;
        check("Standard = base price",       new StandardSeat("A1",0,0,base).computePrice() == base);
        check("Recliner  = base * 1.3",      new ReclineSeat("B1",1,0,base).computePrice()  == base * 1.3);
        check("VIP       = base * 1.5",      new VIPSeat("C1",2,0,base).computePrice()      == base * 1.5);
        check("Couple    = base * 1.8",      new CoupleSeat("D1",3,0,base).computePrice()   == base * 1.8);
        check("Premium   = base * 2.0",      new PremiumSeat("E1",4,0,base).computePrice()  == base * 2.0);
        // Negative: different types must have different prices
        check("VIP != Standard price",
            new VIPSeat("C1",2,0,base).computePrice() != new StandardSeat("A1",0,0,base).computePrice());

        // ── 2. INHERITANCE ────────────────────────────────────────────
        System.out.println("\n── 2. Inheritance ──");
        check("StandardSeat  is-a Seat",  new StandardSeat("A",0,0,0)       instanceof Seat);
        check("VIPSeat        is-a Seat",  new VIPSeat("B",0,0,0)            instanceof Seat);
        check("ReclineSeat    is-a Seat",  new ReclineSeat("C",0,0,0)        instanceof Seat);
        check("CoupleSeat     is-a Seat",  new CoupleSeat("D",0,0,0)         instanceof Seat);
        check("PremiumSeat    is-a Seat",  new PremiumSeat("E",0,0,0)        instanceof Seat);
        check("Corn            is-a Item",  new Corn("C01","Pop",30_000,1)   instanceof Item);
        check("Beverage        is-a Item",  new Beverage("B01","Coke",25_000,1) instanceof Item);

        // ── 3. STATE MANAGEMENT ───────────────────────────────────────
        System.out.println("\n── 3. Seat State Management ──");
        StandardSeat seat = new StandardSeat("A1",0,0,100_000);
        check("New seat is available",       seat.isAvailable());
        seat.setState(Seat.State.booked);
        check("After booking: not available",!seat.isAvailable());
        seat.setState(Seat.State.available);
        check("After release: available",    seat.isAvailable());
        // Negative
        seat.setState(Seat.State.booked);
        check("Booked seat isAvailable=false", !seat.isAvailable());

        // ── 4. CONCURRENCY / SEATSERVICE ─────────────────────────────
        System.out.println("\n── 4. SeatService (Concurrency) ──");
        SeatService svc = new SeatService();
        StandardSeat s2 = new StandardSeat("A2",0,1,100_000);

        // Happy case: select available seat
        try {
            svc.select(s2);
            check("select() books seat", !s2.isAvailable());
        } catch (SeatAlreadyBookedException e) {
            check("select() books seat", false);
        }

        // Negative case: select already booked seat
        boolean threw = false;
        try { svc.select(s2); }
        catch (SeatAlreadyBookedException e) { threw = true; }
        check("Cannot book already booked seat", threw);

        // Cancel and re-book
        svc.cancel(s2);
        check("cancel() releases seat", s2.isAvailable());
        try {
            svc.select(s2);
            check("Can re-book after cancel", !s2.isAvailable());
        } catch (SeatAlreadyBookedException e) {
            check("Can re-book after cancel", false);
        }

        // Two threads: only one can book
        StandardSeat shared = new StandardSeat("X1",0,0,100_000);
        boolean[] results = new boolean[2];
        Thread t1 = new Thread(() -> {
            try { svc.select(shared); results[0] = true; }
            catch (SeatAlreadyBookedException e) { results[0] = false; }
        });
        Thread t2 = new Thread(() -> {
            try { svc.select(shared); results[1] = true; }
            catch (SeatAlreadyBookedException e) { results[1] = false; }
        });
        try { t1.start(); t2.start(); t1.join(); t2.join(); }
        catch (InterruptedException ignored) {}
        check("Only 1 of 2 threads books same seat", results[0] ^ results[1]);

        // ── 5. PAYMENT CALCULATION ────────────────────────────────────
        System.out.println("\n── 5. PaymentService ──");
        PaymentService pay = new PaymentService();
        Film film = new Film("T01","Test",120,100_000,"D","C","S","img","R1",Film.State.NOW_SHOWING);
        Room room = new Room("R1",5,5);

        List<CartItem>      tickets = new ArrayList<>();
        List<SnackCartItem> snacks  = new ArrayList<>();

        // Happy: empty = 0
        check("calcTotal empty = 0", pay.calcTotal(tickets, snacks) == 0);

        // Happy: one standard seat
        tickets.add(new CartItem(film, room, new StandardSeat("X1",0,0,100_000)));
        check("calcTotal 1 standard = 100k", pay.calcTotal(tickets, snacks) == 100_000);

        // Happy: standard + VIP
        tickets.add(new CartItem(film, room, new VIPSeat("X2",1,0,100_000)));
        check("calcTotal Std+VIP = 250k",    pay.calcTotal(tickets, snacks) == 250_000);

        // Happy: add snack
        List<Item> items = new ArrayList<>();
        items.add(new Corn("C01","Corn",30_000,2)); // 2 x 30k = 60k
        snacks.add(new SnackCartItem(items));
        check("calcTotal with snack = 310k", pay.calcTotal(tickets, snacks) == 310_000);

        // Negative: negative quantity item (edge case)
        check("Corn price not negative", new Corn("C01","Corn",30_000,1).getPrice() >= 0);

        // ── 6. USER SERVICE ───────────────────────────────────────────
        System.out.println("\n── 6. UserService ──");
        User user = new User("Tin","u001","pass123", new ArrayList<>());
        UserService usvc = new UserService();

        // Happy: change name
        usvc.changeName(user, "Ngoc");
        check("changeName: name updated",     user.getName().equals("Ngoc"));
        // Negative: old name gone
        check("changeName: old name removed", !user.getName().equals("Tin"));

        // Happy: change password
        usvc.changePassword(user, "newpass456");
        check("changePassword: updated",      user.getPassword().equals("newpass456"));
        // Negative: old password gone
        check("changePassword: old removed",  !user.getPassword().equals("pass123"));

        // Happy: upgrade VIP
        check("New user is not VIP",          !user.isVIP());
        usvc.upgradeVIP(user);
        check("After upgrade: is VIP",         user.isVIP());

        // ── 7. FILM SERVICE ───────────────────────────────────────────
        System.out.println("\n── 7. FilmService ──");
        Film f = new Film("F01","Test",100,50_000,"D","C","S","img","R1",Film.State.COMING_SOON);
        FilmService fsvc = new FilmService();

        check("Initial state = COMING_SOON",  f.getState() == Film.State.COMING_SOON);
        fsvc.changeState(f, Film.State.NOW_SHOWING);
        check("changeState → NOW_SHOWING",    f.getState() == Film.State.NOW_SHOWING);
        fsvc.changeState(f, Film.State.ENDED);
        check("changeState → ENDED",          f.getState() == Film.State.ENDED);
        // Negative: state is not the old one
        check("State is no longer COMING_SOON", f.getState() != Film.State.COMING_SOON);

        // ── 8. ROOM — 2D ARRAY ───────────────────────────────────────
        System.out.println("\n── 8. Room (2D Array) ──");
        Room r = new Room("R1", 5, 6);
        check("Seats matrix not null",    r.getSeats() != null);
        check("Rows = 5",                 r.getSeats().length == 5);
        check("Cols = 6",                 r.getSeats()[0].length == 6);
        check("Total seats = 30",         r.getSeats().length * r.getSeats()[0].length == 30);
        // Negative: no null seats in matrix
        boolean anyNull = false;
        for (Seat[] row : r.getSeats())
            for (Seat s3 : row) if (s3 == null) anyNull = true;
        check("No null seats in matrix",  !anyNull);

        // ── 9. VOUCHER ────────────────────────────────────────────────
        System.out.println("\n── 9. Voucher ──");
        Voucher v = new Voucher("TEST10","Test 10%",10,0,true);
        check("Voucher is active",              v.isActive());
        check("10% off 100k = 90k",             v.apply(100_000) == 90_000);
        check("Discount on 100k = 10k",         v.discount(100_000) == 10_000);

        // Negative: inactive voucher
        Voucher inactive = new Voucher("OFF","Off",50,0,false);
        check("Inactive voucher: no discount",  inactive.apply(100_000) == 100_000);

        // Negative: min order not met
        Voucher minOrder = new Voucher("MIN","Min",20,500_000,true);
        check("Min order not met: no discount", minOrder.apply(100_000) == 100_000);
        check("Min order met: discount applies",minOrder.apply(500_000) == 400_000);

        // ── RESULT ────────────────────────────────────────────────────
        System.out.println("\n══════════════════════════════════════");
        System.out.printf("  PASSED: %d / %d%n", passed, passed + failed);
        System.out.printf("  FAILED: %d%n", failed);
        System.out.println("══════════════════════════════════════");
    }

    static void check(String name, boolean condition) {
        if (condition) { System.out.println("✅ PASS: " + name); passed++; }
        else           { System.out.println("❌ FAIL: " + name); failed++; }
    }
}