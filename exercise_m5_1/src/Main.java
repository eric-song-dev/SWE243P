import dao.CourseDAO;
import dao.EnrollmentDAO;
import dao.StudentDAO;
import model.Course;
import model.Student;
import util.DatabaseManager;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentDAO studentDAO = new StudentDAO();
    private static final CourseDAO courseDAO = new CourseDAO();
    private static final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    public static void main(String[] args) {
        try {
            DatabaseManager.getConnection(); // Force init if needed
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Database connection failed. Exiting.");
            return;
        }

        while (true) {
            printMenu();
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addNewStudent();
                    break;
                case "2":
                    addNewCourse();
                    break;
                case "3":
                    enrollStudentInCourse();
                    break;
                case "4":
                    listStudentsInCourse();
                    break;
                case "5":
                    listCoursesForStudent();
                    break;
                case "6":
                    checkStudentSchedule();
                    break;
                case "7":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("=== Student Course Management System ===");
        System.out.println("1. Add New Student");
        System.out.println("2. Add New Course");
        System.out.println("3. Enroll Student in Course");
        System.out.println("4. Query: Students in a Course");
        System.out.println("5. Query: Courses for a Student");
        System.out.println("6. Query: Student Schedule on a Day");
        System.out.println("7. Exit");
    }

    private static void addNewStudent() {
        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine();
        studentDAO.addStudent(firstName, lastName);
    }

    private static void addNewCourse() {
        System.out.print("Enter Course Code (e.g. CS101): ");
        String code = scanner.nextLine();
        System.out.print("Enter Course Title: ");
        String title = scanner.nextLine();
        courseDAO.addCourse(code, title);

        // Optionally add schedule immediately?
        System.out.print("Add schedule for this course? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            addScheduleToCourse(code);
        }
    }

    private static void addScheduleToCourse(String courseCode) {
        Course c = courseDAO.getCourseByCode(courseCode);
        if (c == null) {
            System.out.println("Course not found.");
            return;
        }
        System.out.print("Enter Day (Monday, Tuesday, ...): ");
        String day = scanner.nextLine();
        System.out.print("Enter Start Time (HH:MM:SS): ");
        String start = scanner.nextLine();
        System.out.print("Enter End Time (HH:MM:SS): ");
        String end = scanner.nextLine();
        courseDAO.addSchedule(c.getId(), day, start, end);
    }

    private static void enrollStudentInCourse() {
        // Simple listing to help user
        listAllStudents();
        System.out.print("Enter Student ID: ");
        int sid = Integer.parseInt(scanner.nextLine());

        listAllCourses();
        System.out.print("Enter Course ID: ");
        int cid = Integer.parseInt(scanner.nextLine());

        enrollmentDAO.enrollStudent(sid, cid);
    }

    private static void listAllStudents() {
        List<Student> students = studentDAO.getAllStudents();
        System.out.println("--- Students ---");
        for (Student s : students)
            System.out.println(s);
    }

    private static void listAllCourses() {
        List<Course> courses = courseDAO.getAllCourses();
        System.out.println("--- Courses ---");
        for (Course c : courses)
            System.out.println(c);
    }

    private static void listStudentsInCourse() {
        listAllCourses();
        System.out.print("Enter Course ID: ");
        int cid = Integer.parseInt(scanner.nextLine());
        List<Student> students = enrollmentDAO.getStudentsInCourse(cid);
        System.out.println("Students in Course " + cid + ":");
        for (Student s : students)
            System.out.println(s);
    }

    private static void listCoursesForStudent() {
        listAllStudents();
        System.out.print("Enter Student ID: ");
        int sid = Integer.parseInt(scanner.nextLine());
        List<Course> courses = enrollmentDAO.getCoursesForStudent(sid);
        System.out.println("Courses for Student " + sid + ":");
        for (Course c : courses)
            System.out.println(c);
    }

    private static void checkStudentSchedule() {
        listAllStudents();
        System.out.print("Enter Student ID: ");
        int sid = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Day of Week (e.g. Monday): ");
        String day = scanner.nextLine();
        enrollmentDAO.printStudentScheduleOnDay(sid, day);
    }
}
