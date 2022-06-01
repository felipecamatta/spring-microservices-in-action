package com.felipecamatta.licensing.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@ToString
public class LicenseDto extends RepresentationModel<LicenseDto> {

    private String licenseId;

    private String description;

    private String organizationId;

    private String productName;

    private String licenseType;

    private String comment;

    private String organizationName;

    private String contactName;

    private String contactPhone;

    private String contactEmail;

}
