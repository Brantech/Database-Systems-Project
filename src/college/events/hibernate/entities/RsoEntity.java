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
@Table(name = "RSO", schema = "APP", catalog = "")
public class RsoEntity {
    private int rsoId;
    private String adminId;
    private int members;

    @Id
    @Column(name = "RSO_ID", nullable = false)
    public int getRsoId() {
        return rsoId;
    }

    public void setRsoId(int rsoId) {
        this.rsoId = rsoId;
    }

    @Basic
    @Column(name = "ADMIN_ID", nullable = false, length = 255)
    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    @Basic
    @Column(name = "MEMBERS", nullable = false)
    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    @Override
    public int hashCode() {

        return Objects.hash(rsoId, adminId, members);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RsoEntity rsoEntity = (RsoEntity) o;
        return rsoId == rsoEntity.rsoId &&
                       members == rsoEntity.members &&
                       Objects.equals(adminId, rsoEntity.adminId);
    }
}
