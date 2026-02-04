# Student Course Management System (Exercise M5.1)

This project implements a Student Course Management System using Java and MySQL.

## Prerequisites
- Java JDK 8 or higher
- MySQL Server running on `localhost:3306`
- MySQL Connector/J

## Configuration
The database connection settings are located in `src/util/DatabaseManager.java`.
If you have a new database name, username, or password, you will need to update the connection string in `DatabaseManager.java`.

## Database Initialization
The application automatically creates the database `m5_1_db` and necessary tables if they do not exist, using the script `init_db.sql`.

## Project Structure
- `src/Main.java`: Entry point with CLI menu
- `src/model/`: Data models (`Student.java`, `Course.java`)
- `src/dao/`: Data Access Objects for database interactions
  - `StudentDAO.java`: Student CRUD operations
  - `CourseDAO.java`: Course and schedule operations
  - `EnrollmentDAO.java`: Enrollment and query operations
- `src/util/DatabaseManager.java`: Database connection management
- `init_db.sql`: Database schema definition

## Running the Application

```bash
cd exercise_m5_1
```

```bash
./run.sh
```


## Menu Options

The application provides a command-line interface with the following menu:

```
=== Student Course Management System ===
1. Add New Student
2. Add New Course
3. Enroll Student in Course
4. Query: Students in a Course
5. Query: Courses for a Student
6. Query: Student Schedule on a Day
7. Exit
```

| Option | Description |
|--------|-------------|
| 1 | Enroll a new student by entering first and last name |
| 2 | Create a new course with code and title, optionally add schedule |
| 3 | Register an existing student for an existing course |
| 4 | List all students enrolled in a specific course |
| 5 | List all courses a specific student is enrolled in |
| 6 | View a student's class schedule for a given day of the week |
| 7 | Exit the program (data persists in MySQL) |

## Database Schema (ER Diagram)

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│    Students     │       │   Enrollments   │       │     Courses     │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ student_id (PK) │───┐   │ enrollment_id   │   ┌───│ course_id (PK)  │
│ first_name      │   └──>│ student_id (FK) │   │   │ course_code     │
│ last_name       │       │ course_id (FK)  │<──┘   │ title           │
└─────────────────┘       └─────────────────┘       └────────┬────────┘
                                                             │
                                                             │ 1:N
                                                             ▼
                                                   ┌─────────────────┐
                                                   │ CourseSchedules │
                                                   ├─────────────────┤
                                                   │ schedule_id (PK)│
                                                   │ course_id (FK)  │
                                                   │ day_of_week     │
                                                   │ start_time      │
                                                   │ end_time        │
                                                   └─────────────────┘
```

### Primary Key Design: Surrogate Key vs Composite Key

| Aspect | Surrogate Key (`enrollment_id`) | Composite Key (`student_id + course_id`) |
|--------|--------------------------------|------------------------------------------|
| **Simplicity** | Extra column | More concise schema |
| **FK References** | Single column | Must reference two columns |
| **ORM Support** | Better compatibility | Requires composite key class |
| **Index Size** | Smaller (single INT) | Larger (two INTs) |
| **Re-enrollment** | Supports multiple enrollments | One per student-course pair |

> This project uses surrogate key for better ORM compatibility and future extensibility.

### Table Relationships
- **Students ↔ Courses**: Many-to-Many relationship via `Enrollments` table
- **Courses → CourseSchedules**: One-to-Many (a course can have multiple meeting times)

## Features
- **Add Student**: Enrolls a new student into the program.
- **Add Course**: Creates a new course with optional schedule.
- **Add Schedule**: Adds meeting times (day + time range) for a course.
- **Enroll Student**: Register a student for a course.
- **Queries**:
  - List students in a course.
  - List courses for a student.
  - View a student's schedule for a specific day.
- **Persistence**: Data is saved in MySQL and persists between runs.
