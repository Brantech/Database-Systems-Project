package college.events.hibernate.entities;/*
 *   File Name:
 *
 *   Classification:  Unclassified
 *
 *   Prime Contract No.: W900KK-17-C-0029
 *
 *   This work was generated under U.S. Government contract and the
 *   Government has unlimited data rights therein.
 *
 *   Copyrights:      Copyright 2014
 *                    Dignitas Technologies, LLC.
 *                    All rights reserved.
 *
 *   Distribution Statement A: Approved for public release; distribution is unlimited
 *
 *   Organizations:   Dignitas Technologies, LLC.
 *                    3504 Lake Lynda Drive, Suite 170
 *                    Orlando, FL 32817
 *
 */

import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "USERS", schema = "APP", catalog = "")
public class UsersEntity {
    private String userId;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private String email;
    private String type;
    private String studentId;
    private String uniId;

    @Id
    @Column(name = "USER_ID", nullable = false, length = 255)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "FIRSTNAME", nullable = false, length = 255)
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Basic
    @Column(name = "LASTNAME", nullable = false, length = 255)
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Basic
    @Column(name = "USERNAME", nullable = false, length = 20)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "PASSWORD", nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "EMAIL", nullable = false, length = 255)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "TYPE", nullable = false, length = 20)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "STUDENT_ID", nullable = true, length = 255)
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    @Basic
    @Column(name = "UNI_ID", nullable = true, length = 255)
    public String getUniId() {
        return uniId;
    }

    public void setUniId(String uniId) {
        this.uniId = uniId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId, firstname, lastname, username, password, email, type, studentId, uniId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersEntity that = (UsersEntity) o;
        return Objects.equals(userId, that.userId) &&
                       Objects.equals(firstname, that.firstname) &&
                       Objects.equals(lastname, that.lastname) &&
                       Objects.equals(username, that.username) &&
                       Objects.equals(password, that.password) &&
                       Objects.equals(email, that.email) &&
                       Objects.equals(type, that.type) &&
                       Objects.equals(studentId, that.studentId) &&
                       Objects.equals(uniId, that.uniId);
    }
}
