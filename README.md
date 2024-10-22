# Smart-Contact-Manager

Smart Contact Manager is a Spring Boot application that allows users to manage their contacts efficiently. It supports basic CRUD (Create, Read, Update, Delete) operations for contacts. Users can add, view, update, and delete their personal contacts, providing a streamlined interface for managing their information securely.

## Features

- **User Authentication:** Secure login and signup functionality with password encryption.
- **Add Contacts:** Users can create and store their contacts with details like name, email, phone, and description.
- **View Contacts:** Displays a list of all contacts added by the user, with the ability to view details of each contact.
- **Update Contacts:** Users can update the details of their saved contacts.
- **Delete Contacts:** Contacts can be removed from the system.
- **User Dashboard:** Personalized dashboard for each user to manage their contacts.
- **Responsive UI:** User-friendly interface designed using HTML, CSS, and Thymeleaf templates.

## Tech Stack

- **Backend:** Spring Boot
- **Frontend:** HTML, CSS, Thymeleaf
- **Database:** MySQL
- **Build Tool:** Maven
- **Security:** Spring Security

## Project Structure
src/
├── main/
│   ├── java/com/smart/
│   │   ├── config/                 # Security and configuration classes
│   │   ├── controller/             # Controllers for handling web requests
│   │   ├── dao/                    # Data Access Object (DAO) layer for database interactions
│   │   ├── entities/               # Entity classes representing database tables
│   │   ├── helper/                 # Helper classes (e.g., message utilities)
│   │   └── SmartcontactmanagerApplication.java # Main application class
│   ├── resources/
│   │   ├── static/                 # Static assets (CSS, JS, images)
│   │   ├── templates/              # Thymeleaf templates for HTML pages
│   │   ├── application.properties  # Application configuration file
│   └── test/                       # Unit and integration tests
├── mvnw                            # Maven wrapper for Linux/Mac
├── mvnw.cmd                        # Maven wrapper for Windows
├── pom.xml                         # Project Object Model (POM) for managing dependencies
└── README.md                       # Project documentation (this file)
