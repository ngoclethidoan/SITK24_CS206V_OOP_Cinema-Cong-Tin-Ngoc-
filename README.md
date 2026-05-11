# 🎬 CNT Cinema Booking System

A **Cinema Booking System** built with Java (Swing GUI) for the OOP course project.  
Demonstrates core OOP principles: Encapsulation, Inheritance, Polymorphism, Abstraction, and Interfaces.

---

## 👥 Authors
- Cong Nguyen
- Tin Nguyen  
- Ngoc Le

---

## 🚀 How to Run

### Option 1 — Maven (recommended)
```bash
# 1. Clone the repo
git clone <your-repo-url>
cd SITK24_CS206V_OOP_Cinema-Cong-Tin-Ngoc-

# 2. Build
mvn compile

# 3. Run
mvn exec:java -Dexec.mainClass="GUI.MainFrame"

# Or build a runnable JAR then run it
mvn package
java -jar dist/CNTCinema.jar
```

### Option 2 — NetBeans
Open the project folder in NetBeans and press **Run**.

> **Default admin account:** username `admin` / password `123`

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🎥 Browse films | View all films with poster, info, summary |
| 🔍 Search | Search films by title in real time |
| 💺 Seat selection | Visual 2D seat map with colour-coded types |
| 🛒 Cart | Add multiple seats to cart before paying |
| 💳 Payment | Checkout tickets + snacks in one flow |
| 🍿 Snack order | Order popcorn & drinks independently |
| 👤 User account | Register, login, view booking history |
| ⚙️ Settings | Change name, password, language (EN/VI/JP) |
| 🛠 Admin panel | Add, delete, change state of films (admin only) |

---

## 🧩 Package Structure

```
src/
├── cinema/        Entry point (Cinema.java)
├── GUI/           All Swing panels and frames
│   ├── MainFrame, FilmPanel, SeatPanel, CartPanel
│   ├── PayPanel, LoginFrame, RegisterFrame
│   ├── AdminPanel, SettingPanel, UserPanel ...
├── model/         Data classes
│   ├── Seat (abstract) → StandardSeat, VIPSeat,
│   │                      PremiumSeat, ReclineSeat, CoupleSeat
│   ├── Item → Beverage, Corn
│   ├── Film, Room, User, BookTicket, CartItem, SnackCartItem
├── service/       Business logic layer
│   ├── BookingService, PaymentService, UserService
│   ├── FilmService, SeatService, ItemService
├── interfaces/    Contracts for each service
│   ├── IBookingService, IPaymentService, IUserService
│   ├── IFilmService, ISeatService, IItemService
├── database/      CSV / file-based persistence
│   ├── FilmDatabase, UserDatabase, BookingDatabase
│   ├── RoomDatabase, ItemDatabase
└── exception/     Custom exceptions
    ├── InvalidSeat, NoExistFilm
```

---

## 🏗 OOP Concepts Applied

| Concept | Where |
|---------|-------|
| **Encapsulation** | Private fields + getters/setters in all model classes |
| **Inheritance** | `Seat` → 5 seat types; `Item` → `Beverage`, `Corn` |
| **Polymorphism** | `computePrice()` overridden per seat type |
| **Abstract class** | `Seat` is abstract with abstract `computePrice()` |
| **Interface** | 6 interfaces in `interfaces/` package |
| **Static** | All `Database` classes use static fields & methods |
| **File I/O** | `bookings.csv`, `users.dat`, `films.csv` |
| **2D Array** | `Seat[][]` matrix inside each `Room` |
| **Concurrency** | `synchronized(seat)` in `SeatService.select()` |

---

## 📁 Data Files

| File | Contents |
|------|----------|
| `Data/films.csv` | Film catalogue (14 fields, `//` separated) |
| `Data/bookings.csv` | Booking history (`\|` separated) |
| `users.dat` | User accounts (`\|` separated) |
| `Data/items.csv` | Snack/drink catalogue |
