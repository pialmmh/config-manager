package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "did_number")
@Data
@NoArgsConstructor
public class DidNumber {

    @Id
    @Column(length = 255)
    private String id;

//    @ManyToOne
//    @JoinColumn(name = "did_pool_id")
//    private DidPool didPool;

    @Column(name = "did_pool_id")
    private Integer didPoolId;

    @Column(length = 100)
    private String description;

    public DidNumber(String id, String description, Integer didPoolId) {
        this.id = id;
        this.description = description;
        this.didPoolId = didPoolId;
    }
}
