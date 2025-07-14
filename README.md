# ♻️ EcoPoints – A Waste Management Reward System

**EcoPoints** is a desktop application built using **Java and Swing** to promote eco-friendly behavior by rewarding users for properly disposing of waste. The system allows users to submit waste with proof (image or description), earn points, and enables admins to verify and manage submissions.

---

## 🌱 Project Overview

- **Goal**: Encourage responsible waste disposal through a gamified reward system.
- **Tech Stack**: Java, Swing (GUI), JDBC (for database connectivity)
- **Target Users**: Citizens (Users) and Waste Management Officials (Admins)

---

## 🗂️ Repository Structure

```
EcoPoints/
├── src/
│   ├── Welcome.java              # App entry screen with navigation
│   ├── Registration.java         # User registration form
│   ├── Login.java                # Login for user/admin
│   ├── StudentDashboard.java     # User dashboard to submit waste and view points
│   ├── AdminDashboard.java       # Admin dashboard to view and approve submissions
│   ├── AddWaste.java             # Submission form with image/proof
│   ├── DatabaseConnection.java   # JDBC database connectivity
├── assets/                       # Optional: logo, icons
├── ecopoints.sql                 # Database schema (tables and structure)
├── README.md                     # Project documentation
└── .gitignore
```

---

## 🚀 Getting Started

### ✅ Prerequisites

- Java JDK 8 or higher
- MySQL or compatible DB
- Any Java IDE (NetBeans, IntelliJ, Eclipse)
- MySQL Connector/J (JDBC driver)

---

### 🔧 Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/your-username/EcoPoints.git
   cd EcoPoints
   ```

2. **Import Project**
   - Open the project in your Java IDE.
   - Add MySQL JDBC Driver to your project libraries.

3. **Database Setup**
   - Import `ecopoints.sql` file into your MySQL server.
   - Update `DatabaseConnection.java` with your DB credentials:
     ```java
     String url = "jdbc:mysql://localhost:3306/ecopoints";
     String username = "root";
     String password = "your_password";
     ```

4. **Run the App**
   - Start with `Welcome.java`
   - Use the navigation buttons to register, login, and explore the system.

---

## 🧪 How It Works

### 👤 User Features
- Register and login as a citizen
- Submit waste information (with optional photo path)
- Earn EcoPoints for each verified submission
- Track total earned points

### 🛠️ Admin Features
- Login securely as admin
- View all user submissions
- Approve or reject waste entries
- Manage points and records

---

## 🌟 Features

- ✅ Clean and user-friendly GUI (Java Swing)
- 📤 Waste submission with proof
- 🧾 Admin approval system
- 💰 Points reward mechanism
- 🔐 Secure login and registration
- 💻 Local database with JDBC

---

## 💡 Future Enhancements

- Add charts to visualize user contributions
- Generate downloadable reports
- Email notifications for approvals
- Leaderboard for top contributors

---

## 🧑‍💻 Contributing

Want to help? Here's how:

- Fork the repository
- Make your changes or improvements
- Create a pull request

---

## 📄 License

This project is licensed under the **MIT License** – see the [LICENSE](LICENSE) file for more information.

---

## 📬 Contact

For feedback or questions, reach out via GitHub: manojkumar2204(https://github.com/manojkumar2204)

---

**Let’s make waste management smarter, cleaner, and rewarding! 💚**
