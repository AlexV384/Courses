package model;

public class Sert {
    private int sertId;
    private int courseId;
    private int userId;

    public Sert() {}

    public Sert(int courseId, int userId) {
        this.courseId = courseId;
        this.userId = userId;
    }

    public int getSertId() { return sertId; }
    public void setSertId(int sertId) { this.sertId = sertId; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    @Override
    public String toString() {
        return String.format("Sert{course=%d, user=%d}", courseId, userId);
    }
}