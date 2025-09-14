package com.telcobright.rtc.domainmodel.mysqlentity.sms;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "retry_cause_code")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetryCauseCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "policy_id", nullable = false)
    private Integer policyId;

//    @ManyToOne(optional = false)
//    @JoinColumn(name = "cause_code", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "retry_cause_code_ibfk_1"))
//    private EnumSmsError causeCode;

    private Boolean retry;
}
