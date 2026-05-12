# 🎬 CNT Cinema Booking System

A **Cinema Booking System** built with Java (Swing GUI) for the OOP course project.  
Demonstrates core OOP principles: Encapsulation, Inheritance, Polymorphism, Abstraction, and Interfaces.

---

## 👥 Authors
| Name | Student ID | Role |
|------|-----------|------|
| Cong Nguyen | 2402052 | Backend / Database |
| Tin Nguyen  | 2402061     | Services / OOP Design |
| Ngoc Le     | 2402026 | GUI / Frontend |

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

# Or build a runnable JAR
mvn package
java -jar dist/CNTCinema.jar
```

### Option 2 — NetBeans
Open the project folder in NetBeans and press **Run** (Shift+F6 on `MainFrame.java`).

### Option 3 — Run Tests
```bash
# Right-click CinemaTest.java → Run File
# Or via Maven:
mvn test
```

> **Default admin account:** username `admin` / password `123`  
> **Register** a new account to use as a normal user.

---

## ✨ Features

### 👤 User Features
| Feature | Description |
|---------|-------------|
| 🎥 Browse films | View all films with poster, director, cast, summary |
| 🔍 Search | Search films by title in real time |
| 💺 Seat selection | Visual 2D seat map with colour-coded seat types |
| 🛒 Add to Cart | Select seats → select snacks → save to cart for later |
| 📖 Book Now | Select seats → select snacks → pay immediately |
| 💳 Payment | Invoice summary with voucher + payment method selection |
| 🎟️ Voucher | Apply discount codes at checkout |
| 🍿 Snack order | Order popcorn & drinks (standalone or with booking) |
| 📋 Booking History | Invoice-style history with booking ID, film, seat, items |
| ⚙️ Settings | Change name, password, language (EN/VI/JP) |

### 🛠 Admin Features
| Feature | Description |
|---------|-------------|
| 🎬 Film management | Add, delete, change state of films |
| 🍿 Snack management | Add, edit price, delete snack items |

---

## 🎫 Seat Types & Prices

| Type | Row | Color | Price |
|------|-----|-------|-------|
| Standard | A, B | 🟢 Green | 80,000 VND |
| Recliner | C | 🔵 Blue | 104,000 VND |
| VIP | D | 🟡 Gold | 120,000 VND |
| Premium | E | 🟣 Purple | 160,000 VND |
| Couple | F | 🔴 Pink | 144,000 VND |

---

## 🎟️ Available Vouchers

| Code | Discount | Min Order |
|------|----------|-----------|
| `WELCOME10` | 10% off | No minimum |
| `SUMMER20` | 20% off | 200,000 VND |
| `VIP30` | 30% off | 300,000 VND |
| `FLASH50` | 50% off | 500,000 VND |
| `STUDENT15` | 15% off | 100,000 VND |

---

## 🧩 Package Structure

```
src/
├── cinema/        Entry point (Cinema.java)
├── GUI/           All Swing panels and frames
│   ├── MainFrame, FilmPanel, SeatPanel, CartPanel
│   ├── PayPanel, SnackOrderPanel, UserPanel
│   ├── LoginFrame, RegisterFrame, AdminPanel
│   ├── SettingPanel, SearchBar, SearchResultPanel
├── model/         Data / domain classes
│   ├── Seat (abstract) → StandardSeat, VIPSeat,
│   │                      ReclineSeat, CoupleSeat, PremiumSeat
│   ├── Item → Beverage, Corn
│   ├── Film, Room, User, BookTicket
│   ├── CartItem, SnackCartItem, Voucher, LanguageManager
├── service/       Business logic layer
│   ├── PaymentService, BookingService, UserService
│   ├── FilmService, SeatService, ItemService
├── interfaces/    Service contracts
│   ├── IPaymentService, IBookingService, IUserService
│   ├── IFilmService, ISeatService, IItemService
├── database/      File-based persistence (CSV)
│   ├── FilmDatabase, UserDatabase, BookingDatabase
│   ├── RoomDatabase, ItemDatabase, VoucherDatabase
├── exception/     Custom exceptions
│   ├── InvalidSeat, NoExistFilm
│   ├── SeatAlreadyBookedException, InvalidVoucherException
└── Test/          Unit tests
    └── CinemaTest.java
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
| **File I/O** | `bookings.csv`, `users.dat`, `films.csv`, `vouchers.csv` |
| **2D Array** | `Seat[][]` matrix inside each `Room` |
| **Concurrency** | `synchronized(seat)` in `SeatService.select()` |
| **Custom Exception** | `SeatAlreadyBookedException`, `InvalidSeat`, `NoExistFilm` |

---

## 📁 Data Files

| File | Format | Contents |
|------|--------|----------|
| `Data/films.csv` | `//` separated, 14 fields | Film catalogue |
| `Data/bookings.csv` | `\|` separated, 7 fields | Booking history + cart (PENDING/PAID) |
| `Data/items.csv` | `\|` separated | Snack & drink catalogue |
| `Data/vouchers.csv` | `\|` separated | Discount voucher codes |
| `users.dat` | `\|` separated | User accounts |

---

## 🔁 User Flow

```
Browse Films
    ↓
Film Detail Page
    ├── Add to Cart → Pick Seats → Pick Snacks → Cart → Pay → Confirm → ✅ Booked
    └── Book Now   → Pick Seats → Pick Snacks → Pay  → Confirm → ✅ Booked
```

---

## 🧪 Test Cases (48 total)

Run `CinemaTest.java` to verify:
- Polymorphism: each seat type computes correct price
- Inheritance: all seat/item types extend base class
- State management: seat available → booked → available
- Concurrency: only 1 of 2 threads can book same seat
- Payment: correct total calculation with tickets + snacks
- UserService: changeName, changePassword, upgradeVIP
- FilmService: changeState between all states
- Room 2D array: correct dimensions, no null seats
- Voucher: discount applied, inactive/min-order edge cases
