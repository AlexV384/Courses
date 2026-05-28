package model;

import java.time.OffsetDateTime;

public class Student {
    private int studentId;
    private int userId;
    private int zachId;
    private String name;
    private String email;
    private String passwordHash;
    private OffsetDateTime registrationDate;

    public Student() {}

    public Student(int userId, int zachId, String name, String email, String passwordHash, OffsetDateTime registrationDate) {
        this.userId = userId;
        this.zachId = zachId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.registrationDate = registrationDate;
    }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getZachId() { return zachId; }
    public void setZachId(int zachId) { this.zachId = zachId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public OffsetDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(OffsetDateTime registrationDate) { this.registrationDate = registrationDate; }

    @Override
    public String toString() {
        return String.format("Student{id=%d, name='%s', zachId=%d}", studentId, name, zachId);
    }
}