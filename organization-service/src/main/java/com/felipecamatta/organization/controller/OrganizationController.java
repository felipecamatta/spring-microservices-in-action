package com.felipecamatta.organization.controller;

import com.felipecamatta.organization.dto.OrganizationDto;
import com.felipecamatta.organization.model.Organization;
import com.felipecamatta.organization.service.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "api/v1/organization")
public class OrganizationController {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationController.class);

    private final OrganizationService service;

    public OrganizationController(OrganizationService service) {
        this.service = service;
    }

    @RolesAllowed({"ADMIN", "USER"})
    @GetMapping(value = "/{organizationId}")
    public ResponseEntity<OrganizationDto> getOrganization(@PathVariable("organizationId") String organizationId) {
        logger.debug("Entering the getOrganization() method for the organizationId: {}", organizationId);
        Organization organization = service.findById(organizationId);
        OrganizationDto organizationDto = new OrganizationDto();
        BeanUtils.copyProperties(organization, organizationDto);
        addLinkToOrganization(organizationDto);
        return ResponseEntity.ok(organizationDto);
    }

    @RolesAllowed({"ADMIN", "USER"})
    @PutMapping(value = "/{organizationId}")
    public ResponseEntity<OrganizationDto> updateOrganization(@PathVariable("organizationId") String id,
                                                              @RequestBody OrganizationDto request) {
        logger.debug("Entering the updateOrganization() method for the organizationId: {}", id);
        Organization organization = new Organization();
        BeanUtils.copyProperties(request, organization);
        organization = service.update(id, organization);
        BeanUtils.copyProperties(organization, request);
        addLinkToOrganization(request);
        return ResponseEntity.ok(request);
    }

    @RolesAllowed({"ADMIN", "USER"})
    @PostMapping
    public ResponseEntity<OrganizationDto> createOrganization(@RequestBody OrganizationDto organizationDto) {
        logger.debug("Entering the createOrganization() method");
        Organization organization = new Organization();
        BeanUtils.copyProperties(organizationDto, organization);
        organization = service.create(organization);
        BeanUtils.copyProperties(organization, organizationDto);
        addLinkToOrganization(organizationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(organizationDto);
    }

    @RolesAllowed({"ADMIN"})
    @DeleteMapping(value = "/{organizationId}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable("organizationId") String id) {
        logger.debug("Entering the deleteOrganization() method for the organizationId: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void addLinkToOrganization(OrganizationDto organization) {
        organization.add(
                linkTo(methodOn(OrganizationController.class).getOrganization(organization.getId())).withSelfRel(),
                linkTo(methodOn(OrganizationController.class).createOrganization(organization)).withRel("createOrganization"),
                linkTo(methodOn(OrganizationController.class).updateOrganization(organization.getId(), organization)).withRel("updateOrganization"),
                linkTo(methodOn(OrganizationController.class).deleteOrganization(organization.getId())).withRel("deleteOrganization")
        );
    }

}
