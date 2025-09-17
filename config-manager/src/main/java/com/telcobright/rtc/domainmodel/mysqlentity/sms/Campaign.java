package com.telcobright.rtc.domainmodel.mysqlentity.sms;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "campaign")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CAMPAIGN_ID")
    private Integer id;

    @Column(name = "CAMPAIGN_NAME", length = 255)
    private String name;

    @Column(name = "SENDER_ID", length = 255)
    private String senderId;

    @Column(name = "SMS_COUNT")
    private Integer smsCount;

    @Column(name = "SMS_ENCODING", length = 60)
    private String smsEncoding;

    @Column(name = "STATUS")
    private Integer status;

//    @ManyToOne
//    @JoinColumn(name = "ID_PARTNER", nullable = false)
//    private Partner partner;

//    @ManyToOne
//    @JoinColumn(name = "ID_SMSQUEUE_KAFKA_TOPIC")
//    private Topic topic;

    @Column(name = "SENT_TASK_COUNT")
    private Integer sentTaskCount;

    @Column(name = "FAILED_TASK_COUNT")
    private Integer failedTaskCount;

    @Column(name = "PENDING_TASK_COUNT")
    private Integer pendingTaskCount;

    @Column(name = "TOTAL_TASK_COUNT")
    private Integer totalTaskCount;

    @Column(name = "LAST_UPDATED_STAMP")
    private LocalDateTime lastUpdatedStamp;

    @Column(name = "LAST_UPDATED_TX_STAMP")
    private LocalDateTime lastUpdatedTxStamp;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @Column(name = "CREATED_STAMP")
    private LocalDateTime createdStamp;

    @Column(name = "CREATED_TX_STAMP")
    private LocalDateTime createdTxStamp;

    @Column(name = "IS_UNICODE")
    private Boolean isUnicode;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @Column(name = "EXPIRE_AT")
    private LocalDateTime expireAt;

    @Column(name = "CLIENT_TRANS_ID", length = 255)
    private String clientTransId;

    @Column(name = "IS_LONG_S_M_S")
    private Boolean isLongSms;

    @Column(name = "RN_CODE")
    private Integer rnCode;

    @Column(name = "IS_FLASH")
    private Boolean isFlash;

    @Column(name = "PRIORITY")
    private Integer priority;

    @Column(name = "TASK_BATCH_SIZE")
    private Integer taskBatchSize;

    @Column(name = "OFFSET")
    private Integer offset;

    @Column(name = "FIELD1")
    private Integer field1;

    @Column(name = "FIELD2")
    private Integer field2;

    @Column(name = "FIELD3", length = 255)
    private String field3;

    @Column(name = "FIELD4", length = 255)
    private String field4;

    @Column(name = "FIELD5", length = 255)
    private String field5;

    @Column(name = "MESSAGE", length = 255 )
    private String message;

//    //    @ManyToOne
//    @OneToOne
//    @JoinColumn(name = "SCHEDULE_POLICY_ID")
//    private SchedulePolicy schedulePolicy;

    @OneToOne
    @JoinColumn(name = "POLICY_ID")
    private Policy policy;

//    @JsonIgnore
//    @OneToMany(fetch = FetchType.LAZY)
//    @JoinColumn(name = "CAMPAIGN_ID")
//    private List<CampaignTask> campaignTasks;

}
