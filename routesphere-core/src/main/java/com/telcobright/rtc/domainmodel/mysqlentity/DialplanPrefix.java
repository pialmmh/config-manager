package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dialplan_prefix")
public class DialplanPrefix implements Comparable<DialplanPrefix>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "idDialplanPrefix")
    private Set<DialplanMapping> dialplanMappings;

    @Column(name = "callSrcId", nullable = false)
    private Integer callSrcId;

    @Column(nullable = false, length = 100)
    private String prefix;

    @Column(length = 200)
    private String description;

    @Transient
    private String callSrcName;

    public DialplanPrefix(Set<DialplanMapping> dialplanMappings, Integer callSrcId, String prefix, String description) {
        this.dialplanMappings = dialplanMappings;
        this.callSrcId = callSrcId;
        this.prefix = prefix;
        this.description = description;
    }

    @Override
    public int compareTo(DialplanPrefix other) {
        return Integer.compare(other.prefix.length(), this.prefix.length()); // Descending by prefix length
    }
}
