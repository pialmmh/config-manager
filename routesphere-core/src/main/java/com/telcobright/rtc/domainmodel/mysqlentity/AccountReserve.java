package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "acc_reserve")
@Data
public class AccountReserve {

    @Id
    @Column(name = "event_id", nullable = false)
    private String eventId;

    private String acc_id;
    private Double amount;
}

