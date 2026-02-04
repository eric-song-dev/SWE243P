package dao;

import model.Course;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public void addCourse(String code, String title) {
        String sql = "INSERT INTO Courses (course_code, title) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setString(2, title);
            pstmt.executeUpdate();
            System.out.println("Course added: " + code);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Course getCourseByCode(String code) {
        String sql = "SELECT * FROM Courses WHERE course_code = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                            rs.getInt("course_id"),
                            rs.getString("course_code"),
                            rs.getString("title"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Not found
    }

    public void addSchedule(int courseId, String day, String startTime, String endTime) {
        String sql = "INSERT INTO CourseSchedules (course_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.setString(2, day);
            pstmt.setString(3, startTime); // Format HH:MM:SS or HH:MM
            pstmt.setString(4, endTime);
            pstmt.executeUpdate();
            System.out.println("Schedule added for course ID " + courseId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM Courses";
        try (Connection conn = DatabaseManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                courses.add(new Course(
                        rs.getInt("course_id"),
                        rs.getString("course_code"),
                        rs.getString("title")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
}
