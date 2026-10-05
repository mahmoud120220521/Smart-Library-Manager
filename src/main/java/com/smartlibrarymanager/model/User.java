package com.smartlibrarymanager.model;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    private String loginToken;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;

    public User() {
        // Required by JPA/Hibernate
    }

    public User(int id, String firstName, String lastName, String email, String passwordHash) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.loginToken = "";
    }

    // Getters and Setters
    public String getLoginToken() { return loginToken; }
    public void setLoginToken(String loginToken) { this.loginToken = loginToken; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    @Override
    public String toString() {
        return id + "," + firstName + "," + lastName + "," + email + "," + passwordHash;
    }
}