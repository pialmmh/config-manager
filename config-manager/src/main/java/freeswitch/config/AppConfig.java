package freeswitch.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class AppConfig {
    public Set<String> configReloadExclusionTables = new HashSet<>(Arrays.asList(
            "acc_chargeable",
            "acc_ledger_summary",
            "acc_ledger_summary_billed",
            "acc_transaction",
            "cdr",
            "cdr_state",
            "cdrdiscarded",
            "cdrerror",
            "cdrerror_2025_02_01",
            "cdrfieldlist",
            "cdrfieldmappingbyswitchtype",
            "cdrinconsistent",
            "cdrinconsistent_jan_2020",
            "cdrmeta",
            "cdrnocompression",
            "cdrpartiallastaggregatedrawinstance",
            "cdrpartialrawinstance",
            "cdrpartialreference",
            "cdrsummarymeta_day_01",
            "cdrsummarymeta_day_02",
            "cdrsummarymeta_day_03",
            "cdrsummarymeta_day_04",
            "cdrsummarymeta_day_05",
            "cdrsummarymeta_day_06",
            "cdrsummarymeta_hr_01",
            "cdrsummarymeta_hr_02",
            "cdrsummarymeta_hr_03",
            "cdrsummarymeta_hr_04",
            "cdrsummarymeta_hr_05",
            "cdrsummarymeta_hr_06",
            "ce5920",
            "lcr",
            "ratetask",
            "sum_voice_day_01",
            "sum_voice_day_02",
            "sum_voice_day_03",
            "sum_voice_day_04",
            "sum_voice_day_05",
            "sum_voice_day_06",
            "sum_voice_hr_01",
            "sum_voice_hr_02",
            "sum_voice_hr_03",
            "sum_voice_hr_04",
            "sum_voice_hr_05",
            "sum_voice_hr_06",
            "campaign_task",
            "autoincrementcounter",
            "audit_log",
            "allerror",
            "invoice",
            "invoice_item",
            "ipaddressorpointcode",
            "ipaddressorpointcodecopy",
            "ipaddressorpointcodecopy2",
            "job",
            "jobcompletion",
            "jobsegment",
            "ledger_summary_meta",
            "login_history",
            "mockcdr",
            "nov_ios_roam_2018",
            "rerrorc"
    ));

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // For Hibernate proxies
        mapper.registerModule(new Hibernate5Module());
        // For Java 8 Date/Time
        mapper.registerModule(new JavaTimeModule());
        // Optional: disable serialization of dates as timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

}
