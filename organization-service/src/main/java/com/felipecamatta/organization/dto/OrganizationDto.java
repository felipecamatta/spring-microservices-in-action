package com.felipecamatta.organization.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDto extends RepresentationModel<OrganizationDto> {

    String id;

    String name;

    String contactName;

    String contactEmail;

    String contactPhone;

}
