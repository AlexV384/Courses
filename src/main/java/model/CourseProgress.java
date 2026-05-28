package model;

public class CourseProgress {
    private int progressId;
    private int courseId;
    private int userId;
    private String progress;

    public CourseProgress() {}

    public CourseProgress(int courseId, int userId, String progress) {
        this.courseId = courseId;
        this.userId = userId;
        this.progress = progress;
    }

    public int getProgressId() { return progressId; }
    public void setProgressId(int progressId) { this.progressId = progressId; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getProgress() { return progress; }
    public void setProgress(String progress) { this.progress = progress; }

    @Override
    public String toString() {
        return String.format("CourseProgress{course=%d, user=%d, progress='%s'}", courseId, userId, progress);
    }
}