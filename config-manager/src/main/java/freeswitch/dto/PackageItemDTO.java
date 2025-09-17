package freeswitch.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PackageItemDTO {
    private Long id;
    private Long idPackage;
    private BigDecimal quantity;
    private String idUom;
    private int category;
    private String prefix;
    private String description;
}
