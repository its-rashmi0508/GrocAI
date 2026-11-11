# GrocAI
<div align="center">

# 🛒 GrocAI (GroceryMate)

### Smart groceries, balanced nutrition

An Android app that helps you plan your shopping list, stay within your monthly budget, scan receipts, and eat better — with an on-device AI assistant that works fully offline.

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Language-Java-ED8B00?logo=openjdk&logoColor=white)
![Min SDK](https://img.shields.io/badge/minSdk-24-blue)
![Target SDK](https://img.shields.io/badge/targetSdk-34-blue)
![ML Kit](https://img.shields.io/badge/OCR-ML%20Kit-4285F4?logo=google&logoColor=white)

</div>

---

## 📖 Table of Contents

1. [About the Project](#-about-the-project)
2. [Features](#-features)
3. [Screenshots](#-screenshots)
4. [Tech Stack](#-tech-stack)
5. [How It Works](#-how-it-works)
6. [Project Structure](#-project-structure)
7. [Getting Started](#-getting-started)
8. [Usage Guide](#-usage-guide)
9. [Security](#-security)
10. [Known Limitations](#-known-limitations)
11. [Roadmap](#-roadmap)
12. [Contributing](#-contributing)
13. [Author](#-author)
14. [License](#-license)

---

## 📌 About the Project

**GrocAI** is a smart grocery and nutrition management app built natively for Android in **Java**. It combines three things people usually handle separately:

- a **shopping list** with prices and quantities,
- a **monthly budget tracker** that updates from what you actually buy, and
- a **nutrition coach** that checks whether your cart is balanced (carbs, protein, fats, fiber).

Everything runs **on the device** — no account server, no cloud, no API keys. Your shopping data, receipts and chat history never leave your phone.

> The Android Studio project is named **GroceryMate** (`com.example.grocerymate`); the app shown to users is called **GrocAI**.

---

🎬 Demo Video
https://drive.google.com/file/d/1aNU7xBFDihUF5vUcXl9DgTX9E3URk_Oj/view?usp=sharing

▶️ Click the link to watch the GrocAI promo video

</div>

---

## ✨ Features

### 🔐 Secure account system
- Create an account or log in with name, email, password and monthly budget
- Passwords are **salted and hashed (PBKDF2)** — never stored in plain text
- Strong-password rules with a **live strength meter** and confirm-password check
- **Brute-force protection**: account locks after 5 wrong attempts
- Each account keeps its **own list and budget** on the device

### 🏠 Home dashboard
- Time-based greeting (Good Morning / Afternoon / Evening / Night)
- Monthly budget card with progress bar, **Spent** and **Remaining**
- Today's summary: items in list, estimated total, estimated calories
- Nutrition pie chart with AI status (Good / Fair / Needs Attention)
- Quick actions: **Shopping List**, **Scan Receipt**, **AI Suggestions**, **Monthly Insights**

### 📝 Shopping list
- Add items with name, category, unit, price and quantity
- Category filter chips: All, Vegetables, Fruits, Dairy, Grains, Meats, Others
- Smart emoji icons for hundreds of food names (e.g. 🍠 for sweet potato)
- Tick items off as **bought** — they count toward your spending
- Delete with an **UNDO** option
- Everything is **saved automatically** and survives closing the app

### 💰 Budget tracking
- Donut chart showing % of budget used (turns red when over budget)
- Budget, spent and remaining amounts — all calculated from your list
- Category-wise spending breakdown, sorted biggest first
- Edit your monthly budget with the ✏️ button

### 📊 Monthly Insights
- Bar chart: **where your money went**, by category
- Grouped bar chart: **your diet vs. a healthy target** (carbs / protein / fats / fiber)
- **"How to improve"** tips generated from your actual cart

### 🧾 Receipt scanning (on-device OCR)
- Take a photo or pick one from the gallery
- Text is read with **Google ML Kit** (bundled model — works offline)
- Items and prices are detected, categorised automatically, and shown for review
- Untick any mistakes, then add the rest — they are added as **already bought**
- Receipt photos are deleted right after scanning

### 🤖 AI assistant (chatbot)
Ask questions in plain English and get answers based on **your own data**:

| Ask about | Example |
|---|---|
| Budget | "How is my budget?", "How much have I spent on dairy?" |
| Nutrition | "How is my diet balance?" |
| Shopping advice | "What should I buy next?" |
| Pending items | "What is left to buy?" |
| Your list | "What's in my list?" |
| Calories | "How many calories is my list?" |
| Savings | "How can I save money?" |
| Expensive items | "What are my most expensive items?" |

Quick-reply chips make the most common questions one tap away.

### 👤 Profile
- Edit your name and family size
- Reminder switch (preference is saved)
- Log out safely (clears the session and in-memory data)

### 🔔 AI nutrition alerts
When the cart is badly out of balance, the app can send a system notification.

---

## 📸 Screenshots

<div align="center">

<img src="screenshots/login.jpg" width="220" alt="Login">
<img src="screenshots/home.jpg" width="220" alt="Home">
<img src="screenshots/list.jpg" width="220" alt="Shopping List">

<img src="screenshots/budget.jpg" width="220" alt="Budget">
<img src="screenshots/insights.jpg" width="220" alt="Monthly Insights">
<img src="screenshots/aichat.jpg" width="220" alt="AI Assistant">

</div>
---

## 🧰 Tech Stack

| Area | Technology |
|---|---|
| Language | Java 17 |
| IDE / Build | Android Studio, Gradle 8.7, Android Gradle Plugin 8.5.2 |
| UI | Material Components 1.12, ConstraintLayout, RecyclerView, CardView, **ViewBinding** |
| Architecture | Single-activity + Fragments, bottom navigation, singleton data store |
| Charts | Custom Canvas views — `PieChartView`, `DonutProgressView`, `BarChartView` (no chart library) |
| Storage | `SharedPreferences` with JSON (`org.json`) — per-account |
| Security | PBKDF2 (HmacSHA256) salted hashes, constant-time comparison, rate limiting |
| OCR | Google **ML Kit Text Recognition 16.0.1** (on-device) |
| AI logic | Rule-based nutrition analyzer + intent-based chatbot (no network) |
| SDK | `minSdk 24` · `targetSdk 34` · `compileSdk 34` |

---

## ⚙️ How It Works

### Budget logic
`Spent` is **not a fixed number** — it is the total of all list items you have **ticked as bought**. That keeps the Home screen, Budget screen, Insights and chatbot all consistent with your list.

```
Spent      = Σ (price × quantity) of ticked items
Remaining  = Monthly budget − Spent
Used %     = Spent ÷ Monthly budget
```

### Nutrition analysis
`NutritionAnalyzer` adds up carbs, protein, fats and fiber across the cart (per-unit values × quantity), converts them into percentages, and compares each with a healthy band. It returns:

- a status — **Good**, **Fair** or **Needs Attention**
- a list of insights (✅ good / ⚠️ warning / 🚨 alert) with advice

### Receipt parsing
1. ML Kit returns text lines with their positions on the photo.
2. Lines on the same horizontal row are joined (name … qty … price).
3. Header/footer lines (total, GST, date, payment, etc.) are skipped.
4. The trailing number is the price; numbers before it are used to work out quantity.
5. The name is cleaned and a category is guessed from keywords.

### Chatbot
`ChatBot.reply()` detects the intent from keywords in the question and builds the answer from live `DataStore` data. It is a single entry point, so a real LLM could be connected later through **your own backend** (never embed an AI API key inside the app).

---

## 🗂️ Project Structure

```
MAD_Activity/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/grocerymate/
│       │   ├── LoginActivity.java        # Sign up / log in
│       │   ├── MainActivity.java         # Bottom navigation host
│       │   ├── adapter/                  # GroceryAdapter, CategoryAdapter,
│       │   │                             # AiTipAdapter, ChatAdapter
│       │   ├── ai/                       # NutritionAnalyzer, NutritionResult,
│       │   │                             # Insight, ChatBot
│       │   ├── data/                     # DataStore (list + budget), Session
│       │   ├── model/                    # GroceryItem, UserProfile, CategorySpend
│       │   ├── ui/                       # Home, List, Budget, Insights, Chat,
│       │   │                             # Profile fragments + dialogs/sheets
│       │   ├── util/                     # Auth, ReceiptScanner, CategoryGuesser,
│       │   │                             # FoodIcons, NotificationHelper
│       │   └── view/                     # PieChartView, DonutProgressView,
│       │                                 # BarChartView
│       └── res/                          # layouts, drawables, colors, styles
├── build.gradle
├── settings.gradle
└── gradle/
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Koala or newer recommended)
- **JDK 17** (bundled with Android Studio as *jbr-17*)
- **Android SDK 34**
- Internet connection for the first Gradle sync (downloads dependencies, including ML Kit)
- An Android emulator (API 24+) **or** a physical phone with USB debugging

### Installation
1. Let **Gradle sync** finish (progress bar at the bottom).
2. If Android Studio asks to install SDK 34 or update Gradle, accept.
3. If you see a JDK error: **Settings → Build Tools → Gradle → Gradle JDK → jbr-17**.
4. Choose an emulator or connected phone and press **Run ▶** (`Shift + F10`).

### Build an APK

**Build → Build Bundle(s) / APK(s) → Build APK(s)**
The file is created at `app/build/outputs/apk/debug/app-debug.apk`.

---

## 📱 Usage Guide

1. **Create an account** — enter name, email, a strong password and your monthly budget.
2. **Add groceries** — tap the green **+** button; pick a category, price and quantity.
3. **Tick items as bought** in the *List* tab — your **Spent** amount updates instantly.
4. **Scan a receipt** — Home → *Scan Receipt* → take/pick a photo → review → *Add to list*.
5. **Check your diet** — the Home pie chart and *Monthly Insights* show what to improve.
6. **Ask the AI** — Home → *AI Suggestions* opens the chat assistant.
7. **Change your budget** — Budget tab → ✏️ icon.

---

## 🔒 Security

| Protection | Details |
|---|---|
| No plain-text passwords | Only a salted PBKDF2 hash is stored (random 16-byte salt per account, 120,000 iterations, HmacSHA256; HmacSHA1 on Android 7.x) |
| Strong passwords | 8–64 characters, upper + lower + number + symbol, not common, must not contain name/email |
| Brute-force defence | 5 wrong attempts → 30 s lock, doubling per failure up to 10 min |
| No account enumeration | Login errors are generic ("Incorrect email or password") |
| Constant-time compare | Prevents timing attacks on the hash |
| Backups disabled | `allowBackup="false"` so credentials aren't copied to cloud backups |
| Private data | List, budget and receipts stay on the device; receipt photos are deleted after scanning |

> **Note:** these protections secure accounts **on this device**. A login that works across multiple devices would need a server (for example Firebase Authentication).

---

## ⚠️ Known Limitations

- Accounts and data are **local to one phone** — there is no cloud sync.
- **Receipt OCR is best-effort.** Accuracy depends on photo quality and receipt layout, which is why detected items are shown for review first.
- Nutrition values for manually added items are **estimates per category**.
- The chatbot is **keyword-based**, not a language model; unusual phrasing may get a generic reply.
- No "forgot password" flow (there is no server to send an email from).
- The reminder switch saves your preference but does not schedule reminders yet.

---

## 🗺️ Roadmap

- [ ] Room database for larger lists and history
- [ ] Cloud login & sync (Firebase Auth + Firestore)
- [ ] Real LLM-powered assistant through a secure backend
- [ ] Barcode scanning for quick item entry
- [ ] Scheduled shopping reminders
- [ ] Change-password screen
- [ ] Monthly history and trends
- [ ] Dark mode
- [ ] Multi-language support (Hindi, Kannada, …)

---

## 👩‍💻 Author

**Rashmi Kumari**
MCA student, Jain University, Bengaluru

**Komal Gupta**
MCA student, Jain University, Bengaluru

- GitHub: [its-rashmi0508](https://github.com/its-rashmi0508)
---


<div align="center">

⭐ If you found this project useful, please give it a star!

</div>
