package com.telcobright.rtc.domainmodel.mysqlentity;


import freeswitch.repository.mysqlrepository.DialplanRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dialplan_mapping")
public class DialplanMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idDialplan", nullable = false)
    private Integer idDialplan;
    @Column(name = "idDialplanPrefix", nullable = false)
    private Integer idDialplanPrefix;

    @Transient
    private String dialplanName;

    private Float percent;

    public DialplanMapping(Integer dialplanId, Integer dialplanPrefixId, Float percent) {
        this.idDialplan = dialplanId;
        this.idDialplanPrefix = dialplanPrefixId;
        this.percent = percent;
    }

    public Dialplan getDialplan(DialplanRepository dialplanRepository) {
        return dialplanRepository.findById(this.idDialplan)
                .orElseThrow(() -> new RuntimeException("Dialplan not found for ID: " + this.idDialplan));
    }
}


