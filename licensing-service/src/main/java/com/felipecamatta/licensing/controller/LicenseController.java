package com.felipecamatta.licensing.controller;

import com.felipecamatta.licensing.dto.LicenseDto;
import com.felipecamatta.licensing.model.License;
import com.felipecamatta.licensing.service.LicenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "api/v1/organization/{organizationId}/license")
public class LicenseController {

    private static final Logger logger = LoggerFactory.getLogger(LicenseController.class);

    private final LicenseService licenseService;

    public LicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @GetMapping(value = "/{licenseId}")
    public ResponseEntity<LicenseDto> getLicense(@PathVariable("organizationId") String organizationId,
                                                 @PathVariable("licenseId") String licenseId) {
        logger.debug("Entering the getLicense() method for the licenseId: {}", licenseId);
        Optional<License> license = licenseService.getLicense(licenseId, organizationId);

        if (license.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        LicenseDto licenseDto = new LicenseDto();
        BeanUtils.copyProperties(license, licenseDto);
        addLinksToLicense(licenseDto);
        return ResponseEntity.ok(licenseDto);
    }

    @PutMapping(value = "/{licenseId}")
    public ResponseEntity<LicenseDto> updateLicense(@PathVariable("licenseId") String licenseId,
                                                    @RequestBody LicenseDto request) {
        logger.debug("Entering the updateLicense() method for the licenseId: {}", request.getLicenseId());
        License license = new License();
        BeanUtils.copyProperties(request, license);
        license = licenseService.updateLicense(licenseId, license);
        BeanUtils.copyProperties(license, request);
        addLinksToLicense(request);
        return ResponseEntity.ok(request);
    }

    @PostMapping
    public ResponseEntity<LicenseDto> createLicense(@RequestBody LicenseDto request) {
        logger.debug("Entering the createLicense() method for the license info: {}", request);
        License license = new License();
        BeanUtils.copyProperties(request, license);
        license = licenseService.createLicense(license);
        BeanUtils.copyProperties(license, request);
        addLinksToLicense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(request);
    }

    @DeleteMapping(value = "/{licenseId}")
    public ResponseEntity<Void> deleteLicense(@PathVariable("licenseId") String licenseId) {
        logger.debug("Entering the deleteLicense() method for the licenseId: {}", licenseId);
        licenseService.deleteLicense(licenseId);
        return ResponseEntity.noContent().build();
    }

    @RolesAllowed({"ADMIN", "USER"})
    @GetMapping
    public List<LicenseDto> getLicenses(@PathVariable("organizationId") String organizationId) {
        logger.debug("Entering the getLicenses() method for the organizationId: {}", organizationId);
        List<License> licenses = licenseService.getLicensesByOrganization(organizationId);
        return licenses.stream().map(license -> {
            LicenseDto licenseDto = new LicenseDto();
            BeanUtils.copyProperties(license, licenseDto);
            return licenseDto;
        }).collect(Collectors.toList());
    }

    private void addLinksToLicense(LicenseDto license) {
        license.add(
                linkTo(methodOn(LicenseController.class)
                        .getLicense(license.getOrganizationId(), license.getLicenseId())).withRel("getLicense"),
                linkTo(methodOn(LicenseController.class).createLicense(license)).withRel("createLicense"),
                linkTo(methodOn(LicenseController.class)
                        .updateLicense(license.getLicenseId(), license)).withRel("updateLicense"),
                linkTo(methodOn(LicenseController.class).deleteLicense(license.getLicenseId())).withRel("deleteLicense")
        );
    }

}
