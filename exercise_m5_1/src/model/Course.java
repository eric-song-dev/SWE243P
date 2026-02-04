package model;

public class Course {
    private int id;
    private String code;
    private String title;

    public Course(int id, String code, String title) {
        this.id = id;
        this.code = code;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s", id, code, title);
    }
}
