package model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "sert")
public class Sert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sert_id")
    private Integer sertId;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Sert() {}

    public Sert(Course course, User user) {
        this.course = course;
        this.user = user;
    }

    public Integer getSertId() { return sertId; }
    public void setSertId(Integer sertId) { this.sertId = sertId; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sert s)) return false;
        return Objects.equals(sertId, s.sertId);
    }

    @Override
    public int hashCode() { return Objects.hashCode(sertId); }

    @Override
    public String toString() {
        return String.format("Sert{course=%d, user=%d}",
                course != null ? course.getId() : null,
                user != null ? user.getId() : null);
    }
}