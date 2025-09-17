package freeswitch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DidAssignmentDto {
    private String didNumberId;
    private Integer idPartner;
    private String partnerName;
    private Date startDate;
    private Date expiryDate;
    private String description;
}
