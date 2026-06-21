package model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "course_progress")
public class CourseProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Integer progressId;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String progress;

    public CourseProgress() {}

    public CourseProgress(Course course, User user, String progress) {
        this.course = course;
        this.user = user;
        this.progress = progress;
    }

    public Integer getProgressId() { return progressId; }
    public void setProgressId(Integer progressId) { this.progressId = progressId; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getProgress() { return progress; }
    public void setProgress(String progress) { this.progress = progress; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseProgress cp)) return false;
        return Objects.equals(progressId, cp.progressId);
    }

    @Override
    public int hashCode() { return Objects.hashCode(progressId); }

    @Override
    public String toString() {
        return String.format("CourseProgress{course=%d, user=%d, progress='%s'}",
                course != null ? course.getId() : null,
                user != null ? user.getId() : null,
                progress);
    }
}