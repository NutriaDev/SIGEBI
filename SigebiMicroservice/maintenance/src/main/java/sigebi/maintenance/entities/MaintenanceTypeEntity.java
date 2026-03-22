package sigebi.maintenance.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "maintenance_types")
public class MaintenanceTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idType;

    @Column(nullable = false)
    private String name;

    public Long getIdType() {
        return idType;
    }

    public void setIdType(Long idType) {
        this.idType = idType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}