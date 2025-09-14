package com.telcobright.rtc.domainmodel.mysqlentity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "cdr")
public class Cdr {
    @Id
    @Column(name = "SwitchId")
    private int switchId;

    @Column(name = "IdCall", nullable = false)
    private long idCall;

    @Column(name = "SequenceNumber", nullable = false)
    private long sequenceNumber;

    @Column(name = "FileName", nullable = false)
    private String fileName;

    @Column(name = "ServiceGroup", nullable = false)
    private int serviceGroup;

    @Column(name = "IncomingRoute")
    private String incomingRoute;

    @Column(name = "OriginatingIP")
    private String originatingIP;

    @Column(name = "OPC")
    private Integer opc;

    @Column(name = "OriginatingCIC")
    private Integer originatingCIC;

    @Column(name = "OriginatingCalledNumber", nullable = false)
    private String originatingCalledNumber;

    @Column(name = "TerminatingCalledNumber")
    private String terminatingCalledNumber;

    @Column(name = "OriginatingCallingNumber")
    private String originatingCallingNumber;

    @Column(name = "TerminatingCallingNumber")
    private String terminatingCallingNumber;

    @Column(name = "PrePaid")
    private Integer prePaid;

    @Column(name = "DurationSec", nullable = false)
    private BigDecimal durationSec;

    @Column(name = "EndTime", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    @Column(name = "ConnectTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date connectTime;

    @Column(name = "AnswerTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date answerTime;

    @Column(name = "ChargingStatus")
    private Integer chargingStatus;

    @Column(name = "PDD")
    private Float pdd;

    @Column(name = "CountryCode")
    private String countryCode;

    @Column(name = "AreaCodeOrLata")
    private String areaCodeOrLata;

    @Column(name = "ReleaseDirection")
    private Integer releaseDirection;

    @Column(name = "ReleaseCauseSystem")
    private Integer releaseCauseSystem;

    @Column(name = "ReleaseCauseEgress")
    private Integer releaseCauseEgress;

    @Column(name = "OutgoingRoute")
    private String outgoingRoute;

    @Column(name = "TerminatingIP")
    private String terminatingIP;

    @Column(name = "DPC")
    private Integer dpc;

    @Column(name = "TerminatingCIC")
    private Integer terminatingCIC;

    @Column(name = "StartTime", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "InPartnerId", nullable = false)
    private int inPartnerId;

    @Column(name = "CustomerRate")
    private BigDecimal customerRate;

    @Column(name = "OutPartnerId", nullable = false)
    private Integer outPartnerId;

    @Column(name = "SupplierRate")
    private BigDecimal supplierRate;

    @Column(name = "MatchedPrefixY")
    private String matchedPrefixY;

    @Column(name = "UsdRateY")
    private BigDecimal usdRateY;

    @Column(name = "MatchedPrefixCustomer")
    private String matchedPrefixCustomer;

    @Column(name = "MatchedPrefixSupplier")
    private String matchedPrefixSupplier;

    @Column(name = "InPartnerCost")
    private BigDecimal inPartnerCost;

    @Column(name = "OutPartnerCost")
    private BigDecimal outPartnerCost;

    @Column(name = "CostAnsIn")
    private BigDecimal costAnsIn;

    @Column(name = "CostIcxIn")
    private BigDecimal costIcxIn;

    @Column(name = "Tax1", nullable = false)
    private BigDecimal tax1;

    @Column(name = "IgwRevenueIn")
    private BigDecimal igwRevenueIn;

    @Column(name = "RevenueAnsOut")
    private BigDecimal revenueAnsOut;

    @Column(name = "RevenueIgwOut")
    private BigDecimal revenueIgwOut;

    @Column(name = "RevenueIcxOut")
    private BigDecimal revenueIcxOut;

    @Column(name = "Tax2", nullable = false)
    private BigDecimal tax2;

    @Column(name = "XAmount")
    private BigDecimal xAmount;

    @Column(name = "YAmount")
    private BigDecimal yAmount;

    @Column(name = "AnsPrefixOrig")
    private String ansPrefixOrig;

    @Column(name = "AnsIdOrig")
    private Integer ansIdOrig;

    @Column(name = "AnsPrefixTerm")
    private String ansPrefixTerm;

    @Column(name = "AnsIdTerm")
    private Integer ansIdTerm;

    @Column(name = "ValidFlag")
    private Integer validFlag;

    @Column(name = "PartialFlag")
    private Integer partialFlag;

    @Column(name = "ReleaseCauseIngress")
    private Integer releaseCauseIngress;

    @Column(name = "InRoamingOpId")
    private Integer inRoamingOpId;

    @Column(name = "OutRoamingOpId")
    private Integer outRoamingOpId;

    @Column(name = "CalledPartyNOA")
    private Integer calledPartyNOA;

    @Column(name = "CallingPartyNOA")
    private Integer callingPartyNOA;

    @Column(name = "AdditionalSystemCodes")
    private String additionalSystemCodes;

    @Column(name = "AdditionalPartyNumber")
    private String additionalPartyNumber;

    @Column(name = "ResellerIds")
    private String resellerIds;

    @Column(name = "ZAmount")
    private BigDecimal zAmount;

    @Column(name = "PreviousRoutes")
    private String previousRoutes;

    @Column(name = "E1Id")
    private Integer e1Id;

    @Column(name = "MediaIp1")
    private String mediaIp1;

    @Column(name = "MediaIp2")
    private String mediaIp2;

    @Column(name = "MediaIp3")
    private String mediaIp3;

    @Column(name = "MediaIp4")
    private String mediaIp4;

    @Column(name = "CallReleaseDuration")
    private Float callReleaseDuration;

    @Column(name = "E1IdOut")
    private Integer e1IdOut;

    @Column(name = "InTrunkAdditionalInfo")
    private String inTrunkAdditionalInfo;

    @Column(name = "OutTrunkAdditionalInfo")
    private String outTrunkAdditionalInfo;

    @Column(name = "InMgwId")
    private String inMgwId;

    @Column(name = "OutMgwId")
    private String outMgwId;

    @Column(name = "MediationComplete", nullable = false)
    private int mediationComplete;

    @Column(name = "Codec")
    private String codec;

    @Column(name = "ConnectedNumberType")
    private Integer connectedNumberType;

    @Column(name = "RedirectingNumber")
    private String redirectingNumber;

    @Column(name = "CallForwardOrRoamingType")
    private Integer callForwardOrRoamingType;

    @Column(name = "OtherDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date otherDate;

    @Column(name = "SummaryMetaTotal")
    private BigDecimal summaryMetaTotal;

    @Column(name = "TransactionMetaTotal")
    private BigDecimal transactionMetaTotal;

    @Column(name = "ChargeableMetaTotal")
    private BigDecimal chargeableMetaTotal;

    @Column(name = "ErrorCode")
    private String errorCode;

    @Column(name = "NERSuccess")
    private Integer nerSuccess;

    @Column(name = "RoundedDuration")
    private BigDecimal roundedDuration;

    @Column(name = "PartialDuration")
    private BigDecimal partialDuration;

    @Column(name = "PartialAnswerTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date partialAnswerTime;

    @Column(name = "PartialEndTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date partialEndTime;

    @Column(name = "FinalRecord")
    private Long finalRecord;

    @Column(name = "Duration1")
    private BigDecimal duration1;

    @Column(name = "Duration2")
    private BigDecimal duration2;

    @Column(name = "Duration3")
    private BigDecimal duration3;

    @Column(name = "Duration4")
    private BigDecimal duration4;

    @Column(name = "PreviousPeriodCdr")
    private Integer previousPeriodCdr;

    @Column(name = "UniqueBillId")
    private String uniqueBillId;

    @Column(name = "AdditionalMetaData")
    private String additionalMetaData;

    @Column(name = "Category")
    private Integer category;

    @Column(name = "SubCategory")
    private Integer subCategory;

    @Column(name = "ChangedByJobId")
    private Long changedByJobId;

    @Column(name = "SignalingStartTime", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date signalingStartTime;

    public int getSwitchId() {
        return switchId;
    }

    public void setSwitchId(int switchId) {
        this.switchId = switchId;
    }

    public long getIdCall() {
        return idCall;
    }

    public void setIdCall(long idCall) {
        this.idCall = idCall;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(int serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public String getIncomingRoute() {
        return incomingRoute;
    }

    public void setIncomingRoute(String incomingRoute) {
        this.incomingRoute = incomingRoute;
    }

    public String getOriginatingIP() {
        return originatingIP;
    }

    public void setOriginatingIP(String originatingIP) {
        this.originatingIP = originatingIP;
    }

    public Integer getOpc() {
        return opc;
    }

    public void setOpc(Integer opc) {
        this.opc = opc;
    }

    public Integer getOriginatingCIC() {
        return originatingCIC;
    }

    public void setOriginatingCIC(Integer originatingCIC) {
        this.originatingCIC = originatingCIC;
    }

    public String getOriginatingCalledNumber() {
        return originatingCalledNumber;
    }

    public void setOriginatingCalledNumber(String originatingCalledNumber) {
        this.originatingCalledNumber = originatingCalledNumber;
    }

    public String getTerminatingCalledNumber() {
        return terminatingCalledNumber;
    }

    public void setTerminatingCalledNumber(String terminatingCalledNumber) {
        this.terminatingCalledNumber = terminatingCalledNumber;
    }

    public String getOriginatingCallingNumber() {
        return originatingCallingNumber;
    }

    public void setOriginatingCallingNumber(String originatingCallingNumber) {
        this.originatingCallingNumber = originatingCallingNumber;
    }

    public String getTerminatingCallingNumber() {
        return terminatingCallingNumber;
    }

    public void setTerminatingCallingNumber(String terminatingCallingNumber) {
        this.terminatingCallingNumber = terminatingCallingNumber;
    }

    public Integer getPrePaid() {
        return prePaid;
    }

    public void setPrePaid(Integer prePaid) {
        this.prePaid = prePaid;
    }

    public BigDecimal getDurationSec() {
        return durationSec;
    }

    public void setDurationSec(BigDecimal durationSec) {
        this.durationSec = durationSec;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(Date connectTime) {
        this.connectTime = connectTime;
    }

    public Date getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(Date answerTime) {
        this.answerTime = answerTime;
    }

    public Integer getChargingStatus() {
        return chargingStatus;
    }

    public void setChargingStatus(Integer chargingStatus) {
        this.chargingStatus = chargingStatus;
    }

    public Float getPdd() {
        return pdd;
    }

    public void setPdd(Float pdd) {
        this.pdd = pdd;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAreaCodeOrLata() {
        return areaCodeOrLata;
    }

    public void setAreaCodeOrLata(String areaCodeOrLata) {
        this.areaCodeOrLata = areaCodeOrLata;
    }

    public Integer getReleaseDirection() {
        return releaseDirection;
    }

    public void setReleaseDirection(Integer releaseDirection) {
        this.releaseDirection = releaseDirection;
    }

    public Integer getReleaseCauseSystem() {
        return releaseCauseSystem;
    }

    public void setReleaseCauseSystem(Integer releaseCauseSystem) {
        this.releaseCauseSystem = releaseCauseSystem;
    }

    public Integer getReleaseCauseEgress() {
        return releaseCauseEgress;
    }

    public void setReleaseCauseEgress(Integer releaseCauseEgress) {
        this.releaseCauseEgress = releaseCauseEgress;
    }

    public String getOutgoingRoute() {
        return outgoingRoute;
    }

    public void setOutgoingRoute(String outgoingRoute) {
        this.outgoingRoute = outgoingRoute;
    }

    public String getTerminatingIP() {
        return terminatingIP;
    }

    public void setTerminatingIP(String terminatingIP) {
        this.terminatingIP = terminatingIP;
    }

    public Integer getDpc() {
        return dpc;
    }

    public void setDpc(Integer dpc) {
        this.dpc = dpc;
    }

    public Integer getTerminatingCIC() {
        return terminatingCIC;
    }

    public void setTerminatingCIC(Integer terminatingCIC) {
        this.terminatingCIC = terminatingCIC;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getInPartnerId() {
        return inPartnerId;
    }

    public void setInPartnerId(int inPartnerId) {
        this.inPartnerId = inPartnerId;
    }

    public BigDecimal getCustomerRate() {
        return customerRate;
    }

    public void setCustomerRate(BigDecimal customerRate) {
        this.customerRate = customerRate;
    }

    public Integer getOutPartnerId() {
        return outPartnerId;
    }

    public void setOutPartnerId(Integer outPartnerId) {
        this.outPartnerId = outPartnerId;
    }

    public BigDecimal getSupplierRate() {
        return supplierRate;
    }

    public void setSupplierRate(BigDecimal supplierRate) {
        this.supplierRate = supplierRate;
    }

    public String getMatchedPrefixY() {
        return matchedPrefixY;
    }

    public void setMatchedPrefixY(String matchedPrefixY) {
        this.matchedPrefixY = matchedPrefixY;
    }

    public BigDecimal getUsdRateY() {
        return usdRateY;
    }

    public void setUsdRateY(BigDecimal usdRateY) {
        this.usdRateY = usdRateY;
    }

    public String getMatchedPrefixCustomer() {
        return matchedPrefixCustomer;
    }

    public void setMatchedPrefixCustomer(String matchedPrefixCustomer) {
        this.matchedPrefixCustomer = matchedPrefixCustomer;
    }

    public String getMatchedPrefixSupplier() {
        return matchedPrefixSupplier;
    }

    public void setMatchedPrefixSupplier(String matchedPrefixSupplier) {
        this.matchedPrefixSupplier = matchedPrefixSupplier;
    }

    public BigDecimal getInPartnerCost() {
        return inPartnerCost;
    }

    public void setInPartnerCost(BigDecimal inPartnerCost) {
        this.inPartnerCost = inPartnerCost;
    }

    public BigDecimal getOutPartnerCost() {
        return outPartnerCost;
    }

    public void setOutPartnerCost(BigDecimal outPartnerCost) {
        this.outPartnerCost = outPartnerCost;
    }

    public BigDecimal getCostAnsIn() {
        return costAnsIn;
    }

    public void setCostAnsIn(BigDecimal costAnsIn) {
        this.costAnsIn = costAnsIn;
    }

    public BigDecimal getCostIcxIn() {
        return costIcxIn;
    }

    public void setCostIcxIn(BigDecimal costIcxIn) {
        this.costIcxIn = costIcxIn;
    }

    public BigDecimal getTax1() {
        return tax1;
    }

    public void setTax1(BigDecimal tax1) {
        this.tax1 = tax1;
    }

    public BigDecimal getIgwRevenueIn() {
        return igwRevenueIn;
    }

    public void setIgwRevenueIn(BigDecimal igwRevenueIn) {
        this.igwRevenueIn = igwRevenueIn;
    }

    public BigDecimal getRevenueAnsOut() {
        return revenueAnsOut;
    }

    public void setRevenueAnsOut(BigDecimal revenueAnsOut) {
        this.revenueAnsOut = revenueAnsOut;
    }

    public BigDecimal getRevenueIgwOut() {
        return revenueIgwOut;
    }

    public void setRevenueIgwOut(BigDecimal revenueIgwOut) {
        this.revenueIgwOut = revenueIgwOut;
    }

    public BigDecimal getRevenueIcxOut() {
        return revenueIcxOut;
    }

    public void setRevenueIcxOut(BigDecimal revenueIcxOut) {
        this.revenueIcxOut = revenueIcxOut;
    }

    public BigDecimal getTax2() {
        return tax2;
    }

    public void setTax2(BigDecimal tax2) {
        this.tax2 = tax2;
    }

    public BigDecimal getxAmount() {
        return xAmount;
    }

    public void setxAmount(BigDecimal xAmount) {
        this.xAmount = xAmount;
    }

    public BigDecimal getyAmount() {
        return yAmount;
    }

    public void setyAmount(BigDecimal yAmount) {
        this.yAmount = yAmount;
    }

    public String getAnsPrefixOrig() {
        return ansPrefixOrig;
    }

    public void setAnsPrefixOrig(String ansPrefixOrig) {
        this.ansPrefixOrig = ansPrefixOrig;
    }

    public Integer getAnsIdOrig() {
        return ansIdOrig;
    }

    public void setAnsIdOrig(Integer ansIdOrig) {
        this.ansIdOrig = ansIdOrig;
    }

    public String getAnsPrefixTerm() {
        return ansPrefixTerm;
    }

    public void setAnsPrefixTerm(String ansPrefixTerm) {
        this.ansPrefixTerm = ansPrefixTerm;
    }

    public Integer getAnsIdTerm() {
        return ansIdTerm;
    }

    public void setAnsIdTerm(Integer ansIdTerm) {
        this.ansIdTerm = ansIdTerm;
    }

    public Integer getValidFlag() {
        return validFlag;
    }

    public void setValidFlag(Integer validFlag) {
        this.validFlag = validFlag;
    }

    public Integer getPartialFlag() {
        return partialFlag;
    }

    public void setPartialFlag(Integer partialFlag) {
        this.partialFlag = partialFlag;
    }

    public Integer getReleaseCauseIngress() {
        return releaseCauseIngress;
    }

    public void setReleaseCauseIngress(Integer releaseCauseIngress) {
        this.releaseCauseIngress = releaseCauseIngress;
    }

    public Integer getInRoamingOpId() {
        return inRoamingOpId;
    }

    public void setInRoamingOpId(Integer inRoamingOpId) {
        this.inRoamingOpId = inRoamingOpId;
    }

    public Integer getOutRoamingOpId() {
        return outRoamingOpId;
    }

    public void setOutRoamingOpId(Integer outRoamingOpId) {
        this.outRoamingOpId = outRoamingOpId;
    }

    public Integer getCalledPartyNOA() {
        return calledPartyNOA;
    }

    public void setCalledPartyNOA(Integer calledPartyNOA) {
        this.calledPartyNOA = calledPartyNOA;
    }

    public Integer getCallingPartyNOA() {
        return callingPartyNOA;
    }

    public void setCallingPartyNOA(Integer callingPartyNOA) {
        this.callingPartyNOA = callingPartyNOA;
    }

    public String getAdditionalSystemCodes() {
        return additionalSystemCodes;
    }

    public void setAdditionalSystemCodes(String additionalSystemCodes) {
        this.additionalSystemCodes = additionalSystemCodes;
    }

    public String getAdditionalPartyNumber() {
        return additionalPartyNumber;
    }

    public void setAdditionalPartyNumber(String additionalPartyNumber) {
        this.additionalPartyNumber = additionalPartyNumber;
    }

    public String getResellerIds() {
        return resellerIds;
    }

    public void setResellerIds(String resellerIds) {
        this.resellerIds = resellerIds;
    }

    public BigDecimal getzAmount() {
        return zAmount;
    }

    public void setzAmount(BigDecimal zAmount) {
        this.zAmount = zAmount;
    }

    public String getPreviousRoutes() {
        return previousRoutes;
    }

    public void setPreviousRoutes(String previousRoutes) {
        this.previousRoutes = previousRoutes;
    }

    public Integer getE1Id() {
        return e1Id;
    }

    public void setE1Id(Integer e1Id) {
        this.e1Id = e1Id;
    }

    public String getMediaIp1() {
        return mediaIp1;
    }

    public void setMediaIp1(String mediaIp1) {
        this.mediaIp1 = mediaIp1;
    }

    public String getMediaIp2() {
        return mediaIp2;
    }

    public void setMediaIp2(String mediaIp2) {
        this.mediaIp2 = mediaIp2;
    }

    public String getMediaIp3() {
        return mediaIp3;
    }

    public void setMediaIp3(String mediaIp3) {
        this.mediaIp3 = mediaIp3;
    }

    public String getMediaIp4() {
        return mediaIp4;
    }

    public void setMediaIp4(String mediaIp4) {
        this.mediaIp4 = mediaIp4;
    }

    public Float getCallReleaseDuration() {
        return callReleaseDuration;
    }

    public void setCallReleaseDuration(Float callReleaseDuration) {
        this.callReleaseDuration = callReleaseDuration;
    }

    public Integer getE1IdOut() {
        return e1IdOut;
    }

    public void setE1IdOut(Integer e1IdOut) {
        this.e1IdOut = e1IdOut;
    }

    public String getInTrunkAdditionalInfo() {
        return inTrunkAdditionalInfo;
    }

    public void setInTrunkAdditionalInfo(String inTrunkAdditionalInfo) {
        this.inTrunkAdditionalInfo = inTrunkAdditionalInfo;
    }

    public String getOutTrunkAdditionalInfo() {
        return outTrunkAdditionalInfo;
    }

    public void setOutTrunkAdditionalInfo(String outTrunkAdditionalInfo) {
        this.outTrunkAdditionalInfo = outTrunkAdditionalInfo;
    }

    public String getInMgwId() {
        return inMgwId;
    }

    public void setInMgwId(String inMgwId) {
        this.inMgwId = inMgwId;
    }

    public String getOutMgwId() {
        return outMgwId;
    }

    public void setOutMgwId(String outMgwId) {
        this.outMgwId = outMgwId;
    }

    public int getMediationComplete() {
        return mediationComplete;
    }

    public void setMediationComplete(int mediationComplete) {
        this.mediationComplete = mediationComplete;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public Integer getConnectedNumberType() {
        return connectedNumberType;
    }

    public void setConnectedNumberType(Integer connectedNumberType) {
        this.connectedNumberType = connectedNumberType;
    }

    public String getRedirectingNumber() {
        return redirectingNumber;
    }

    public void setRedirectingNumber(String redirectingNumber) {
        this.redirectingNumber = redirectingNumber;
    }

    public Integer getCallForwardOrRoamingType() {
        return callForwardOrRoamingType;
    }

    public void setCallForwardOrRoamingType(Integer callForwardOrRoamingType) {
        this.callForwardOrRoamingType = callForwardOrRoamingType;
    }

    public Date getOtherDate() {
        return otherDate;
    }

    public void setOtherDate(Date otherDate) {
        this.otherDate = otherDate;
    }

    public BigDecimal getSummaryMetaTotal() {
        return summaryMetaTotal;
    }

    public void setSummaryMetaTotal(BigDecimal summaryMetaTotal) {
        this.summaryMetaTotal = summaryMetaTotal;
    }

    public BigDecimal getTransactionMetaTotal() {
        return transactionMetaTotal;
    }

    public void setTransactionMetaTotal(BigDecimal transactionMetaTotal) {
        this.transactionMetaTotal = transactionMetaTotal;
    }

    public BigDecimal getChargeableMetaTotal() {
        return chargeableMetaTotal;
    }

    public void setChargeableMetaTotal(BigDecimal chargeableMetaTotal) {
        this.chargeableMetaTotal = chargeableMetaTotal;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getNerSuccess() {
        return nerSuccess;
    }

    public void setNerSuccess(Integer nerSuccess) {
        this.nerSuccess = nerSuccess;
    }

    public BigDecimal getRoundedDuration() {
        return roundedDuration;
    }

    public void setRoundedDuration(BigDecimal roundedDuration) {
        this.roundedDuration = roundedDuration;
    }

    public BigDecimal getPartialDuration() {
        return partialDuration;
    }

    public void setPartialDuration(BigDecimal partialDuration) {
        this.partialDuration = partialDuration;
    }

    public Date getPartialAnswerTime() {
        return partialAnswerTime;
    }

    public void setPartialAnswerTime(Date partialAnswerTime) {
        this.partialAnswerTime = partialAnswerTime;
    }

    public Date getPartialEndTime() {
        return partialEndTime;
    }

    public void setPartialEndTime(Date partialEndTime) {
        this.partialEndTime = partialEndTime;
    }

    public Long getFinalRecord() {
        return finalRecord;
    }

    public void setFinalRecord(Long finalRecord) {
        this.finalRecord = finalRecord;
    }

    public BigDecimal getDuration1() {
        return duration1;
    }

    public void setDuration1(BigDecimal duration1) {
        this.duration1 = duration1;
    }

    public BigDecimal getDuration2() {
        return duration2;
    }

    public void setDuration2(BigDecimal duration2) {
        this.duration2 = duration2;
    }

    public BigDecimal getDuration3() {
        return duration3;
    }

    public void setDuration3(BigDecimal duration3) {
        this.duration3 = duration3;
    }

    public BigDecimal getDuration4() {
        return duration4;
    }

    public void setDuration4(BigDecimal duration4) {
        this.duration4 = duration4;
    }

    public Integer getPreviousPeriodCdr() {
        return previousPeriodCdr;
    }

    public void setPreviousPeriodCdr(Integer previousPeriodCdr) {
        this.previousPeriodCdr = previousPeriodCdr;
    }

    public String getUniqueBillId() {
        return uniqueBillId;
    }

    public void setUniqueBillId(String uniqueBillId) {
        this.uniqueBillId = uniqueBillId;
    }

    public String getAdditionalMetaData() {
        return additionalMetaData;
    }

    public void setAdditionalMetaData(String additionalMetaData) {
        this.additionalMetaData = additionalMetaData;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(Integer subCategory) {
        this.subCategory = subCategory;
    }

    public Long getChangedByJobId() {
        return changedByJobId;
    }

    public void setChangedByJobId(Long changedByJobId) {
        this.changedByJobId = changedByJobId;
    }

    public Date getSignalingStartTime() {
        return signalingStartTime;
    }

    public void setSignalingStartTime(Date signalingStartTime) {
        this.signalingStartTime = signalingStartTime;
    }

}
