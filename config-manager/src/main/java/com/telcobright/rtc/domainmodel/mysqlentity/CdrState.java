package com.telcobright.rtc.domainmodel.mysqlentity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "cdr_state")
@NoArgsConstructor
@AllArgsConstructor
public class CdrState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cdr_state")
    private Long cdrState;

    @Entity
    @Table(name = "tb_mnp")
    @Data
    public static class PhoneNumberDetails {

        @Id
        private String number; // Primary key

        private String portedDate;
        private int recipientRC;
        private int donerRC;
        private int nrhRC;
        private String numberType;
        private String portedAction;
        private int donorrc;
        private String number_type;
        private String ported_action;
        private LocalDate ported_date;
    }
}
