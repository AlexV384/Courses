package model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "lesson_progress")
public class LessonProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Integer progressId;

    @ManyToOne
    @JoinColumn(name = "course_progress_id", nullable = false)
    private CourseProgress courseProgress;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String progress;

    public LessonProgress() {}

    public LessonProgress(CourseProgress courseProgress, Lesson lesson, User user, String progress) {
        this.courseProgress = courseProgress;
        this.lesson = lesson;
        this.user = user;
        this.progress = progress;
    }

    public Integer getProgressId() { return progressId; }
    public void setProgressId(Integer progressId) { this.progressId = progressId; }
    public CourseProgress getCourseProgress() { return courseProgress; }
    public void setCourseProgress(CourseProgress courseProgress) { this.courseProgress = courseProgress; }
    public Lesson getLesson() { return lesson; }
    public void setLesson(Lesson lesson) { this.lesson = lesson; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getProgress() { return progress; }
    public void setProgress(String progress) { this.progress = progress; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LessonProgress lp)) return false;
        return Objects.equals(progressId, lp.progressId);
    }

    @Override
    public int hashCode() { return Objects.hashCode(progressId); }

    @Override
    public String toString() {
        return String.format("LessonProgress{lesson=%d, user=%d, progress='%s'}",
                lesson != null ? lesson.getId() : null,
                user != null ? user.getId() : null,
                progress);
    }
}