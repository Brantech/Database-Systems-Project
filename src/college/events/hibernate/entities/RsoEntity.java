package college.events.hibernate.entities;

import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "RSO", schema = "APP", catalog = "")
public class RsoEntity {
    private String rsoId;
    private String adminId;
    private String name;
    private String description;
    private String type;
    private int members;
    private String uniId;

    @Id
    @Column(name = "RSO_ID", nullable = false, length = 255)
    public String getRsoId() {
        return rsoId;
    }

    public void setRsoId(String rsoId) {
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
    @Column(name = "NAME", nullable = false, length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "DESCRIPTION", nullable = false, length = 255)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "TYPE", nullable = false, length = 255)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "MEMBERS", nullable = false)
    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    @Basic
    @Column(name = "UNI_ID", nullable = false, length = 255)
    public String getUniId() {
        return uniId;
    }

    public void setUniId(String uniId) {
        this.uniId = uniId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RsoEntity rsoEntity = (RsoEntity) o;
        return members == rsoEntity.members &&
                Objects.equals(rsoId, rsoEntity.rsoId) &&
                Objects.equals(adminId, rsoEntity.adminId) &&
                Objects.equals(name, rsoEntity.name) &&
                Objects.equals(description, rsoEntity.description) &&
                Objects.equals(type, rsoEntity.type) &&
                Objects.equals(uniId, rsoEntity.uniId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rsoId, adminId, name, description, type, members, uniId);
    }
}
