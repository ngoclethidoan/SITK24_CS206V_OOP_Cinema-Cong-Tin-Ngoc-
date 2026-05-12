package Test;

import model.*;
import service.*;
import exception.*;
import java.util.ArrayList;
import java.util.List;

public class CinemaTest {

    static int passed = 0, failed = 0;

    public static void main(String[] args) {
        System.out.println("══════════════════════════════════════════");
        System.out.println("        CNT CINEMA — TEST SUITE");
        System.out.println("══════════════════════════════════════════\n");

        testPolymorphism();
        testInheritance();
        testSeatState();
        testConcurrency();
        testPaymentService();
        testUserService();
        testFilmService();
        testRoom2DArray();
        testVoucher();
        testCartItem();
        testItemPricing();
        testExceptionMessages();

        System.out.println("\n══════════════════════════════════════════");
        System.out.printf("  PASSED: %d / %d%n", passed, passed + failed);
        System.out.printf("  FAILED: %d%n", failed);
        System.out.println("══════════════════════════════════════════");
    }

    // ── 1. POLYMORPHISM ──────────────────────────────────────────────
    static void testPolymorphism() {
        System.out.println("── 1. Polymorphism (computePrice) ──");
        double base = 100_000;
        check("Standard = base",              new StandardSeat("A1",0,0,base).computePrice() == base);
        check("Recliner  = base * 1.3",       new ReclineSeat("B1",1,0,base).computePrice()  == base * 1.3);
        check("VIP       = base * 1.5",       new VIPSeat("C1",2,0,base).computePrice()      == base * 1.5);
        check("Couple    = base * 1.8",       new CoupleSeat("D1",3,0,base).computePrice()   == base * 1.8);
        check("Premium   = base * 2.0",       new PremiumSeat("E1",4,0,base).computePrice()  == base * 2.0);
        check("VIP price > Standard",
            new VIPSeat("C1",2,0,base).computePrice() > new StandardSeat("A1",0,0,base).computePrice());
        check("Premium is most expensive",
            new PremiumSeat("E1",4,0,base).computePrice() > new VIPSeat("C1",2,0,base).computePrice());
        check("Zero base → zero price",       new VIPSeat("X1",0,0,0).computePrice() == 0);
    }

    // ── 2. INHERITANCE ───────────────────────────────────────────────
    static void testInheritance() {
        System.out.println("\n── 2. Inheritance ──");
        check("StandardSeat is-a Seat",  new StandardSeat("A",0,0,0)    instanceof Seat);
        check("VIPSeat       is-a Seat",  new VIPSeat("B",0,0,0)         instanceof Seat);
        check("ReclineSeat   is-a Seat",  new ReclineSeat("C",0,0,0)     instanceof Seat);
        check("CoupleSeat    is-a Seat",  new CoupleSeat("D",0,0,0)      instanceof Seat);
        check("PremiumSeat   is-a Seat",  new PremiumSeat("E",0,0,0)     instanceof Seat);
        check("Corn     is-a Item",       new Corn("C01","Pop",30_000,1) instanceof Item);
        check("Beverage is-a Item",       new Beverage("B01","Coke",25_000,1) instanceof Item);
        check("Corn name correct",     new Corn("C01","Pop",30_000,1).getName().equals("Pop"));
        check("Beverage name correct", new Beverage("B01","Coke",25_000,1).getName().equals("Coke"));
    }

    // ── 3. SEAT STATE ────────────────────────────────────────────────
    static void testSeatState() {
        System.out.println("\n── 3. Seat State Management ──");
        StandardSeat seat = new StandardSeat("A1",0,0,100_000);
        check("New seat is available",          seat.isAvailable());
        seat.setState(Seat.State.booked);
        check("After booking: not available",   !seat.isAvailable());
        seat.setState(Seat.State.available);
        check("After release: available again", seat.isAvailable());
        seat.setState(Seat.State.booked);
        check("Booked → isAvailable = false",   !seat.isAvailable());
        seat.setState(Seat.State.available);
        seat.setState(Seat.State.booked);
        check("Release + re-book works",        !seat.isAvailable());
    }

    // ── 4. CONCURRENCY ───────────────────────────────────────────────
    static void testConcurrency() {
        System.out.println("\n── 4. SeatService (Concurrency) ──");
        SeatService svc = new SeatService();
        StandardSeat s = new StandardSeat("A2",0,1,100_000);

        try { svc.select(s); check("select() books seat", !s.isAvailable()); }
        catch (SeatAlreadyBookedException e) { check("select() books seat", false); }

        boolean threw = false;
        try { svc.select(s); } catch (SeatAlreadyBookedException e) { threw = true; }
        check("Double-book throws exception", threw);

        svc.cancel(s);
        check("cancel() releases seat", s.isAvailable());

        try { svc.select(s); check("Re-book after cancel works", !s.isAvailable()); }
        catch (SeatAlreadyBookedException e) { check("Re-book after cancel works", false); }

        svc.cancel(s); svc.cancel(s);
        check("Double cancel does not crash", true);

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
        check("Only 1 of 2 threads books shared seat", results[0] ^ results[1]);
    }

    // ── 5. PAYMENT SERVICE ───────────────────────────────────────────
    static void testPaymentService() {
        System.out.println("\n── 5. PaymentService ──");
        PaymentService pay = new PaymentService();
        Film film = new Film("T01","Test",120,100_000,"D","C","S","img","R1",Film.State.NOW_SHOWING);
        Room room = new Room("R1",5,5);
        List<CartItem>      tickets = new ArrayList<>();
        List<SnackCartItem> snacks  = new ArrayList<>();

        check("Empty = 0", pay.calcTotal(tickets, snacks) == 0);

        tickets.add(new CartItem(film, room, new StandardSeat("X1",0,0,100_000)));
        check("1 Standard = 100k", pay.calcTotal(tickets, snacks) == 100_000);

        tickets.add(new CartItem(film, room, new VIPSeat("X2",1,0,100_000)));
        check("Std + VIP = 250k",  pay.calcTotal(tickets, snacks) == 250_000);

        tickets.add(new CartItem(film, room, new PremiumSeat("X3",2,0,100_000)));
        check("+ Premium = 450k",  pay.calcTotal(tickets, snacks) == 450_000);

        List<Item> items = new ArrayList<>();
        items.add(new Corn("C01","Corn",30_000,2));
        items.add(new Beverage("B01","Coke",25_000,1));
        snacks.add(new SnackCartItem(items));
        check("+ Snacks = 535k",   pay.calcTotal(tickets, snacks) == 535_000);

        check("getPaymentMethods not empty",  !pay.getPaymentMethods().isEmpty());
        check("CASH is valid method",          pay.isValidPaymentMethod("CASH"));
        check("CARD is valid method",          pay.isValidPaymentMethod("CARD"));
        check("INVALID is not valid method",  !pay.isValidPaymentMethod("BITCOIN"));
    }

    // ── 6. USER SERVICE ──────────────────────────────────────────────
    static void testUserService() {
        System.out.println("\n── 6. UserService ──");
        User user = new User("Tin","u001","pass123", new ArrayList<>());
        UserService svc = new UserService();

        svc.changeName(user, "Ngoc");
        check("changeName: updated",          user.getName().equals("Ngoc"));
        check("changeName: old removed",      !user.getName().equals("Tin"));

        svc.changePassword(user, "newpass456");
        check("changePassword: updated",      user.getPassword().equals("newpass456"));
        check("changePassword: old removed",  !user.getPassword().equals("pass123"));

        check("New user is not VIP",          !user.isVIP());
        svc.upgradeVIP(user);
        check("After upgradeVIP: isVIP",       user.isVIP());

        try { svc.changeName(user, ""); check("changeName empty: no crash", true); }
        catch (Exception e) { check("changeName empty: no crash", false); }
    }

    // ── 7. FILM SERVICE ──────────────────────────────────────────────
    static void testFilmService() {
        System.out.println("\n── 7. FilmService ──");
        Film f = new Film("F01","Test",100,50_000,"D","C","S","img","R1",Film.State.COMING_SOON);
        FilmService svc = new FilmService();

        check("Initial = COMING_SOON",        f.getState() == Film.State.COMING_SOON);
        svc.changeState(f, Film.State.NOW_SHOWING);
        check("→ NOW_SHOWING",                f.getState() == Film.State.NOW_SHOWING);
        svc.changeState(f, Film.State.ENDED);
        check("→ ENDED",                      f.getState() == Film.State.ENDED);
        check("State != COMING_SOON",         f.getState() != Film.State.COMING_SOON);
        check("Title correct",                f.getTitle().equals("Test"));
        check("Duration correct",             f.getDuration() == 100);
        check("Price correct",                f.getPrice() == 50_000);
        check("RoomId correct",               f.getRoomId().equals("R1"));
    }

    // ── 8. ROOM 2D ARRAY ─────────────────────────────────────────────
    static void testRoom2DArray() {
        System.out.println("\n── 8. Room (2D Array) ──");
        Room r = new Room("R1", 5, 6);
        check("Matrix not null",              r.getSeats() != null);
        check("Rows = 5",                     r.getSeats().length == 5);
        check("Cols = 6",                     r.getSeats()[0].length == 6);
        check("Total = 30",                   r.getSeats().length * r.getSeats()[0].length == 30);
        boolean anyNull = false;
        for (Seat[] row : r.getSeats())
            for (Seat s : row) if (s == null) anyNull = true;
        check("No null seats",                !anyNull);
        boolean allAvailable = true;
        for (Seat[] row : r.getSeats())
            for (Seat s : row) if (!s.isAvailable()) allAvailable = false;
        check("All seats initially available", allAvailable);
        check("Room ID correct",              r.getRoomId().equals("R1"));
    }

    // ── 9. VOUCHER ───────────────────────────────────────────────────
    static void testVoucher() {
        System.out.println("\n── 9. Voucher ──");
        Voucher v = new Voucher("TEST10","Test 10%",10,0,true);
        check("Active voucher",               v.isActive());
        check("10% off 100k = 90k",           v.apply(100_000) == 90_000);
        check("Discount = 10k",               v.discount(100_000) == 10_000);
        check("50% off 200k = 100k",          new Voucher("X","X",50,0,true).apply(200_000) == 100_000);

        Voucher off = new Voucher("OFF","Off",50,0,false);
        check("Inactive: no discount",        off.apply(100_000) == 100_000);
        check("Inactive: discount = 0",       off.discount(100_000) == 0);

        Voucher min = new Voucher("MIN","Min",20,500_000,true);
        check("Min not met: no discount",     min.apply(100_000) == 100_000);
        check("Min exactly met: applies",     min.apply(500_000) == 400_000);
        check("Min exceeded: applies",        min.apply(600_000) == 480_000);
    }

    // ── 10. CART ITEM ────────────────────────────────────────────────
    static void testCartItem() {
        System.out.println("\n── 10. CartItem ──");
        Film film = new Film("F01","Avengers",150,100_000,"D","C","S","img","R1",Film.State.NOW_SHOWING);
        Room room = new Room("R1",5,5);
        Seat seat = new VIPSeat("D5",3,4,80_000);
        CartItem item = new CartItem(film, room, seat);
        check("CartItem film not null",       item.getFilm() != null);
        check("CartItem room not null",       item.getRoom() != null);
        check("CartItem seat not null",       item.getSeat() != null);
        check("CartItem film title",          item.getFilm().getTitle().equals("Avengers"));
        check("CartItem seat code = D5",      item.getSeat().getCodeSeat().equals("D5"));
        check("CartItem VIP price = 120k",    item.getSeat().computePrice() == 80_000 * 1.5);
    }

    // ── 11. ITEM PRICING ─────────────────────────────────────────────
    static void testItemPricing() {
        System.out.println("\n── 11. Item Pricing ──");
        Corn corn = new Corn("C01","Popcorn",30_000,3);
        check("Corn price = 30k",             corn.getPrice() == 30_000);
        check("Corn qty = 3",                 corn.getQuantity() == 3);
        check("Corn total = 90k",             corn.getPrice() * corn.getQuantity() == 90_000);

        Beverage bev = new Beverage("B01","Coke",25_000,2);
        check("Beverage price = 25k",         bev.getPrice() == 25_000);
        check("Beverage qty = 2",             bev.getQuantity() == 2);
        check("Beverage total = 50k",         bev.getPrice() * bev.getQuantity() == 50_000);

        List<Item> items = new ArrayList<>();
        items.add(corn); items.add(bev);
        SnackCartItem sc = new SnackCartItem(items);
        check("SnackCartItem size = 2",       sc.getItems().size() == 2);
        check("SnackCartItem total = 140k",   sc.getTotalPrice() == 140_000);
    }

    // ── 12. EXCEPTION MESSAGES ───────────────────────────────────────
    static void testExceptionMessages() {
        System.out.println("\n── 12. Exception Messages ──");
        SeatAlreadyBookedException e1 = new SeatAlreadyBookedException("D5");
        check("SeatAlreadyBooked has message",    e1.getMessage() != null && !e1.getMessage().isEmpty());
        check("SeatAlreadyBooked mentions seat",  e1.getMessage().contains("D5"));

        InvalidSeat e2 = new InvalidSeat("Z9");
        check("InvalidSeat has message",          e2.getMessage() != null && !e2.getMessage().isEmpty());
        check("InvalidSeat mentions seat",        e2.getMessage().contains("Z9"));

        NoExistFilm e3 = new NoExistFilm("F99");
        check("NoExistFilm has message",          e3.getMessage() != null && !e3.getMessage().isEmpty());
        check("NoExistFilm mentions code",        e3.getMessage().contains("F99"));

        InvalidVoucherException e4 = new InvalidVoucherException("FAKE");
        check("InvalidVoucher has message",       e4.getMessage() != null && !e4.getMessage().isEmpty());
        check("InvalidVoucher mentions code",     e4.getMessage().contains("FAKE"));

        check("SeatAlreadyBooked extends Exception", e1 instanceof Exception);
        check("InvalidSeat extends Exception",       e2 instanceof Exception);
        check("NoExistFilm extends Exception",       e3 instanceof Exception);
        check("InvalidVoucher extends Exception",    e4 instanceof Exception);
    }

    // ── HELPER ───────────────────────────────────────────────────────
    static void check(String name, boolean ok) {
        if (ok) { System.out.println("  ✅ PASS: " + name); passed++; }
        else     { System.out.println("  ❌ FAIL: " + name); failed++; }
    }
}
