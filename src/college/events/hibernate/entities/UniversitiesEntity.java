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
@Table(name = "UNIVERSITIES", schema = "APP", catalog = "")
public class UniversitiesEntity {
    private String uniId;
    private String name;
    private String location;
    private String descritpion;
    private int students;

    @Id
    @Column(name = "UNI_ID", nullable = false, length = 255)
    public String getUniId() {
        return uniId;
    }

    public void setUniId(String uniId) {
        this.uniId = uniId;
    }

    @Basic
    @Column(name = "NAME", nullable = false, length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "LOCATION", nullable = true, length = 255)
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Basic
    @Column(name = "DESCRITPION", nullable = true, length = 255)
    public String getDescritpion() {
        return descritpion;
    }

    public void setDescritpion(String descritpion) {
        this.descritpion = descritpion;
    }

    @Basic
    @Column(name = "STUDENTS", nullable = false)
    public int getStudents() {
        return students;
    }

    public void setStudents(int students) {
        this.students = students;
    }

    @Override
    public int hashCode() {

        return Objects.hash(uniId, name, location, descritpion, students);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UniversitiesEntity that = (UniversitiesEntity) o;
        return students == that.students &&
                       Objects.equals(uniId, that.uniId) &&
                       Objects.equals(name, that.name) &&
                       Objects.equals(location, that.location) &&
                       Objects.equals(descritpion, that.descritpion);
    }
}
