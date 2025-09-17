package freeswitch.dto;

import lombok.Data;

@Data
public class RetailPartnerCreateDto {
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private Integer idpartner;
}
