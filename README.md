# 🎬 Cinebook – Movie Ticket Booking System

A web-based movie ticket booking application built using **Java, Spring Boot, and MySQL**.

Cinebook provides a convenient platform for users to explore available movies, view showtimes, book movie tickets, and manage their bookings through a user-friendly web interface.

## ✨ Features

### 🎥 Movie Browsing
- Browse available movies.
- Explore movie details.
- View available movie showtimes.

### 🎟️ Ticket Booking
- Book movie tickets online.
- Select movies and available showtimes.
- Enter the required booking details.
- Manage ticket bookings.

### 📋 Booking Management
- View booking information.
- Manage existing bookings.
- Access booking details conveniently.

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Java | Backend programming |
| Spring Boot | Application framework |
| MySQL | Database management |
| JDBC | Database connectivity |
| HTML | Web page structure |
| CSS | Styling and design |
| JavaScript | Frontend interactions |
| Maven | Project and dependency management |
| Thymeleaf | Server-side HTML rendering (if used) |

## ⚙️ Requirements

Before running the project, make sure you have:

- Java Development Kit (JDK)
- MySQL Server 8.0+
- MySQL Workbench (optional, for database management)
- Git
- Visual Studio Code or another Java IDE

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/baraths2025-cse/Cinebook.git
```

### 2. Open the project

```bash
cd Cinebook
```

### 3. Configure the MySQL database

Make sure your MySQL Server is running.

Open the project in VS Code and locate:

`src/main/resources/application.properties`

Check the database configuration and make sure the database name, username, password, and JDBC URL match your local MySQL setup.

Create the required database and tables according to the application's database configuration and SQL queries.

### 4. Configure database credentials

Update your database credentials in the application's configuration.

For better security, avoid hardcoding passwords in publicly shared source code. Use environment variables for sensitive credentials where supported.

### 5. Run the application

On Windows PowerShell, execute:

```powershell
.\mvnw.cmd spring-boot:run
```

Wait for Spring Boot to start successfully.

Open your browser and visit:

```text
http://localhost:8080
```

## 📁 Project Structure

```text
Cinebook/
│
├── .mvn/
│   └── wrapper/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/cinebook/
│   │   │       └── Java application files
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   │
│   └── test/
│
├── uploads/
│
├── .gitattributes
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## 🔒 Security and Privacy

- Database credentials should be kept private.
- Sensitive configuration files should not be exposed publicly.
- User and booking information should be handled securely.
- Uploaded files should be managed carefully.

## 👨‍💻 Developer

**Barath**

Computer Science and Engineering

GitHub: [baraths2025-cse](https://github.com/baraths2025-cse)

**Project Repository:** [Cinebook](https://github.com/baraths2025-cse/Cinebook)

---

*Built as a movie ticket booking project to make discovering movies and booking tickets more convenient and accessible.*