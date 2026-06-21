package model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Integer studentId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "zach_id", nullable = false)
    private Integer zachId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "registration_date", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime registrationDate;

    public Student() {}

    public Student(User user, Integer zachId, String name, String email, String passwordHash, OffsetDateTime registrationDate) {
        this.user = user;
        this.zachId = zachId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.registrationDate = registrationDate;
    }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Integer getZachId() { return zachId; }
    public void setZachId(Integer zachId) { this.zachId = zachId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public OffsetDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(OffsetDateTime registrationDate) { this.registrationDate = registrationDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student s)) return false;
        return Objects.equals(studentId, s.studentId);
    }

    @Override
    public int hashCode() { return Objects.hashCode(studentId); }

    @Override
    public String toString() {
        return String.format("Student{id=%d, name='%s', zachId=%d}", studentId, name, zachId);
    }
}