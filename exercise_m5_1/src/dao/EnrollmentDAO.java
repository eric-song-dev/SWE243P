package dao;

import model.Student;
import model.Course;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    public void enrollStudent(int studentId, int courseId) {
        String sql = "INSERT INTO Enrollments (student_id, course_id) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.executeUpdate();
            System.out.println("Enrolled student ID " + studentId + " into course ID " + courseId);
        } catch (SQLException e) {
            System.err.println("Enrollment failed (maybe already enrolled?): " + e.getMessage());
        }
    }

    // Query: Which students are in each course (or specific course)
    // Let's implement getting students for a specific course
    public List<Student> getStudentsInCourse(int courseId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.* FROM Students s JOIN Enrollments e ON s.student_id = e.student_id WHERE e.course_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(new Student(
                            rs.getInt("student_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    // Query: Which courses each student is in
    public List<Course> getCoursesForStudent(int studentId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.* FROM Courses c JOIN Enrollments e ON c.course_id = e.course_id WHERE e.student_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(new Course(
                            rs.getInt("course_id"),
                            rs.getString("course_code"),
                            rs.getString("title")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    // Query: Schedules for a student on a given day
    public void printStudentScheduleOnDay(int studentId, String day) {
        String sql = "SELECT c.course_code, c.title, sch.start_time, sch.end_time " +
                "FROM Enrollments e " +
                "JOIN Courses c ON e.course_id = c.course_id " +
                "JOIN CourseSchedules sch ON c.course_id = sch.course_id " +
                "WHERE e.student_id = ? AND sch.day_of_week = ? " +
                "ORDER BY sch.start_time";

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setString(2, day);

            System.out.println("Schedule for Student ID " + studentId + " on " + day + ":");
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.printf("%s (%s): %s - %s%n",
                            rs.getString("course_code"),
                            rs.getString("title"),
                            rs.getTime("start_time"),
                            rs.getTime("end_time"));
                }
                if (!found) {
                    System.out.println("No classes scheduled.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
