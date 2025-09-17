package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ratetaskreference")
public class RateTaskReference{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idRatePlan", foreignKey = @ForeignKey(name = "fk_RatePlan"))
    private RatePlan ratePlan;

    @Column(name = "Description", length = 300)
    private String description;

}
