# SKKU TABLE - Festival Booth Reservation Management System

SKKU TABLE is a web application designed to efficiently manage festival booth reservations at Sungkyunkwan University. Students can explore and reserve booths at various festivals, while booth operators can easily manage reservations and time-slot operations.

## 🎯 Key Features

- **Festival & Booth Discovery**: Search and explore various festivals and booth information
- **Real-time Reservation System**: Time-slot based booth reservations with real-time availability
- **User Management**: Secure user management through Firebase authentication
- **Like System**: Like your favorite festivals and booths
- **Admin Panel**: Administrative features for festival and booth management
- **Responsive PWA**: Mobile-friendly Progressive Web App

## 🏗️ Tech Stack

### Frontend

- **Framework**: Next.js 15 (App Router)
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **State Management**: Zustand
- **Authentication**: Firebase Auth
- **UI Components**: Radix UI, Lucide React
- **PWA**: next-pwa

### Backend

- **Framework**: Spring Boot 3.5
- **Language**: Java 21
- **Database**: MySQL
- **ORM**: Spring Data JPA
- **Migration**: Flyway
- **Image Storage**: Cloudinary
- **Authentication**: Firebase Admin SDK
- **API Testing**: Bruno

### Infrastructure

- **Containerization**: Docker & Docker Compose
- **Reverse Proxy**: Caddy
- **CI/CD**: GitHub Actions
- **Deployment**: Oracle Cloud

## 📁 Project Structure

```
skku-table/
├── frontend/                 # Next.js Frontend
│   ├── app/                 # App Router Structure
│   │   ├── (auth)/         # Authentication Pages
│   │   ├── (client)/       # Client Pages
│   │   │   └── (main)/     # Main Pages
│   │   └── admin/          # Admin Pages
│   ├── components/         # Reusable Components
│   ├── libs/              # Utility Libraries
│   └── stores/            # Zustand State Management
├── backend/               # Spring Boot Backend
│   └── src/main/java/com/skkutable/
│       ├── controller/    # REST API Controllers
│       ├── service/       # Business Logic
│       ├── domain/        # JPA Entities
│       ├── repository/    # Data Access Layer
│       └── dto/          # Data Transfer Objects
├── api-testing/          # Bruno API Test Collection
├── functions/            # Firebase Cloud Functions
└── docker-compose.yml    # Development Environment Setup
```

## 🚀 Local Development Environment Setup

## Table of Contents

## Project Setup

### Clone the Repository

```bash
git clone https://github.com/skku-table/skku-table.git
```

![clone-skku-table](/assets/how-to-set-up-local-dev-env/clone.png)

Execute the above command to clone the project.

### Open Project Directory

```bash
code skku-table
```

OR

```bash
cursor skku-table
```

### Clean Up Existing Docker Containers and Volumes

![delete-container](/assets/how-to-set-up-local-dev-env/delete-container.png)
![delete-volume](/assets/how-to-set-up-local-dev-env/delete-volume.png)

Delete any existing Docker containers and volumes as shown in the images above.

## Environment Configuration

### Create Root `.env` File

Create a `.env` file in the project root directory with the following format:

```plaintext
CLOUDINARY_API_KEY=YOUR_CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET=YOUR_CLOUDINARY_API_SECRET
CLOUDINARY_CLOUD_NAME=YOUR_CLOUDINARY_CLOUD_NAME
MYSQL_DATABASE=skku-table-dev
MYSQL_ROOT_PASSWORD=YOUR_MYSQL_ROOT_PASSWORD
SPRING_DATASOURCE_URL=jdbc:mysql://database:3306/skku-table-dev?allowPublicKeyRetrieval=true&useSSL=false
SPRING_PROFILES_ACTIVE=dev
FIREBASE_SERVICE_ACCOUNT_KEY=YOUR_FIREBASE_SERVICE_ACCOUNT_KEY
```

### Create Frontend Environment Variables

Create a `.env.local` file in the `frontend/` directory with the following format:

```plaintext
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_API_KEY=YOUR_API_KEY
NEXT_PUBLIC_AUTH_DOMAIN=YOUR_AUTH_DOMAIN
NEXT_PUBLIC_PROJECT_ID=YOUR_PROJECT_ID
NEXT_PUBLIC_STORAGE_BUCKET=YOUR_STORAGE_BUCKET
NEXT_PUBLIC_MESSAGING_SENDER_ID=YOUR_MESSAGING_SENDER_ID
NEXT_PUBLIC_APP_ID=YOUR_APP_ID
NEXT_PUBLIC_MEASUREMENT_ID=YOUR_MEASUREMENT_ID
NEXT_PUBLIC_VAPID_PUBLIC_KEY=YOUR_VAPID_PUBLIC_KEY
NEXT_PUBLIC_ADMIN_SECRET=YOUR_ADMIN_SECRET
```

## Backend Setup (MySQL + Spring Boot Application)

Open the `docker-compose.yml` file to run the backend.

![docker-compose-yml](/assets/how-to-set-up-local-dev-env/docker-compose-yml.png)

To run the `database` service, click the `Run Service` play button above.  
This will start the MySQL container.  
Once MySQL is running, start the Spring Boot Application.

To run the `application` service, click the `Run Service` play button above.  
This will start the Spring Boot Application.

Great! The backend is now running.
You can now open Bruno and test the backend API running locally as shown below. (Make requests according to the seed data described below.)

![test-backend-api-with-bruno](/assets/how-to-set-up-local-dev-env/test-backend-api-with-bruno.png)

## Seed Data

When the backend runs locally (on your laptop or desktop), seed data is automatically loaded into the database.

The seed data is located in the `backend/src/main/resources/db/seed/dev/R_seed_data.sql` file in the repository.

## Seed Data Management

- `docker compose run --rm dev-flyway-clean` - Deletes seed data
- `docker compose run --rm dev-flyway-migrate` - Applies seed data
- `docker compose run --rm dev-flyway-info` - Shows migration information

## Frontend Setup (Next.js)

Navigate to the frontend directory and run the development server as usual.

```bash
cd frontend
pnpm install  # If needed
pnpm run dev
```

The frontend runs at `http://localhost:3000` and connects to the backend API at `http://localhost:8080`.

## Backend Development Setup (IDE Direct Execution)

If you want to run Spring Boot directly from your IDE, configure as follows:

### Create Separate `.env` File (Optional)

For running only the backend, you can create a separate `.env` file in the project root:

```plaintext
CLOUDINARY_API_KEY=YOUR_CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET=YOUR_CLOUDINARY_API_SECRET
CLOUDINARY_CLOUD_NAME=YOUR_CLOUDINARY_CLOUD_NAME
MYSQL_DATABASE=skku-table-dev
MYSQL_ROOT_PASSWORD=YOUR_MYSQL_ROOT_PASSWORD
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/skku-table-dev?allowPublicKeyRetrieval=true&useSSL=false
SPRING_PROFILES_ACTIVE=dev
FIREBASE_SERVICE_ACCOUNT_KEY=YOUR_FIREBASE_SERVICE_ACCOUNT_KEY
```

**Note:** Use `localhost:3306` in `SPRING_DATASOURCE_URL` (not `database:3306` which is for Docker containers).

### Configure Spring Boot Profile

Click the icon next to the `Run` button in the top-right corner of IntelliJ IDEA, then select `Edit Configurations...` from the menu.

![edit-configurations](/assets/how-to-set-up-local-dev-env/edit-configurations.png)

![spring-boot-profile](/assets/how-to-set-up-local-dev-env/spring-boot-profile.png)

Add `dev` to the `Active Profiles` section.  
In the `Environment variables` section, add the path to your `.env` file. (Click the folder icon on the right to directly select the `.env` file.)

### Rebuild Project

In IntelliJ IDEA, click `Build` - `Rebuild Project`.  
This will rebuild the project.

### Run Backend

Run the MySQL container as usual, then start the backend.  
Spring will apply the dev profile and connect to the local database.  
Seed data will also be applied.

## Java Code Formatting

[Google Style Guide Repository](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)

![settings-code-style](/assets/how-to-set-up-local-dev-env/settings-code-style.png)

Download intellij-java-google-style.xml, then in IntelliJ IDEA go to `Preferences` - `Code Style` - `Java` and click the `Schema` tab.

You'll see a gear icon as shown above. Click it to select the downloaded file.

![import-scheme](/assets/how-to-set-up-local-dev-env/import-scheme.png)

![intellij-idea-code-style](/assets/how-to-set-up-local-dev-env/intellij-idea-code-style.png)

schema - gear icon - import downloaded file

## Apply Code Formatting

Press `Ctrl + Alt + L` (or `Cmd + Alt + L` on Mac) to apply code formatting.

## 📝 API Documentation

API documentation is available through Bruno test collections in the `api-testing/` directory. The collection includes comprehensive test cases for:

- User authentication and management
- Festival operations
- Booth management
- Reservation system (v1 and v2)
- Image upload functionality

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔧 Development Notes

### Key Changes Summary

1. **Frontend Environment Variables**: Create separate `.env.local` file in `frontend/` directory for Next.js environment variables
2. **Backend Environment Variables**: Added Firebase service account key, MySQL root password, and other new environment variables
3. **Database Connection URL**: Added `allowPublicKeyRetrieval=true&useSSL=false` parameters
4. **Production Environment**: CD script dynamically generates `.env.production` file during deployment

### Development Workflow

1. Start MySQL container using Docker Compose
2. Run Spring Boot application (either via Docker or IDE)
3. Run Next.js frontend development server
4. Use Bruno for API testing and development

For any issues or questions, please check the [Issues](https://github.com/skku-table/skku-table/issues) section or create a new issue.
