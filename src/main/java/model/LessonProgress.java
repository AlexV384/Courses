package model;

public class LessonProgress {
    private int progressId;
    private int courseProgressId;
    private int lessonId;
    private int userId;
    private String progress;

    public LessonProgress() {}

    public LessonProgress(int courseProgressId, int lessonId, int userId, String progress) {
        this.courseProgressId = courseProgressId;
        this.lessonId = lessonId;
        this.userId = userId;
        this.progress = progress;
    }

    public int getProgressId() { return progressId; }
    public void setProgressId(int progressId) { this.progressId = progressId; }
    public int getCourseProgressId() { return courseProgressId; }
    public void setCourseProgressId(int courseProgressId) { this.courseProgressId = courseProgressId; }
    public int getLessonId() { return lessonId; }
    public void setLessonId(int lessonId) { this.lessonId = lessonId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getProgress() { return progress; }
    public void setProgress(String progress) { this.progress = progress; }

    @Override
    public String toString() {
        return String.format("LessonProgress{lesson=%d, user=%d, progress='%s'}", lessonId, userId, progress);
    }
}