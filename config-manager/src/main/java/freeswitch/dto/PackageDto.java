package freeswitch.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PackageDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal vat;
    private LocalDateTime createDate;
    private Long validity;
    private BigDecimal ait;
    private boolean activeStatus;
    private List<PackageItemDTO> packageItems;
}
