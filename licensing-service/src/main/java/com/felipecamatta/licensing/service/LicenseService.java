package com.felipecamatta.licensing.service;

import com.felipecamatta.licensing.model.License;
import com.felipecamatta.licensing.model.Organization;
import com.felipecamatta.licensing.repository.LicenseRepository;
import com.felipecamatta.licensing.service.client.OrganizationClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.ScopedSpan;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeoutException;

@Service
public class LicenseService {

    private static final Logger logger = LoggerFactory.getLogger(LicenseService.class);

    private final OrganizationClient organizationClient;
    private final LicenseRepository licenseRepository;
    private final Tracer tracer;

    public LicenseService(OrganizationClient organizationClient,
                          LicenseRepository licenseRepository,
                          Tracer tracer) {
        this.organizationClient = organizationClient;
        this.licenseRepository = licenseRepository;
        this.tracer = tracer;
    }

    public Optional<License> getLicense(String licenseId, String organizationId) {
        Optional<License> license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
        if (license.isEmpty()) {
            logger.debug("License with id {} not found", licenseId);
            return license;
        }

        Organization organization = retrieveOrganizationInfo(organizationId);
        if (null != organization) {
            license.get().setOrganizationName(organization.getName());
            license.get().setContactName(organization.getContactName());
            license.get().setContactEmail(organization.getContactEmail());
            license.get().setContactPhone(organization.getContactPhone());
        }

        return license;
    }

    private Organization retrieveOrganizationInfo(String organizationId) {
        return organizationClient.getOrganization(organizationId);
    }

    public License createLicense(License license) {
        license.setLicenseId(UUID.randomUUID().toString());

        ScopedSpan newSpan = tracer.startScopedSpan("createLicenDBCall");
        licenseRepository.save(license);
        logger.debug("Creating License Info: {}", license);
        newSpan.tag("peer.service", "postgres");
        newSpan.event("Client received");
        newSpan.end();

        return license;
    }

    public License updateLicense(String id, License license) {
        ScopedSpan newSpan = tracer.startScopedSpan("updateLicenDBCall");
        license.setLicenseId(id);
        licenseRepository.save(license);
        logger.debug("Updating License Info: {}", license);
        newSpan.tag("peer.service", "postgres");
        newSpan.event("Client received");
        newSpan.end();

        return license;
    }

    public void deleteLicense(String licenseId) {
        ScopedSpan newSpan = tracer.startScopedSpan("deleteLicenDBCall");
        licenseRepository.deleteById(licenseId);
        logger.debug("Deleting License: {}", licenseId);
        newSpan.tag("peer.service", "postgres");
        newSpan.event("Client received");
        newSpan.end();
    }

    @CircuitBreaker(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
    @RateLimiter(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
//    @Bulkhead(name = "bulkheadLicenseService", fallbackMethod = "buildFallbackLicenseList",
//    type = Bulkhead.Type.THREADPOOL)
    @Retry(name = "retryLicenseService", fallbackMethod = "buildFallbackLicenseList")
    public List<License> getLicensesByOrganization(String organizationId) {
        randomlyRunLong();
        return licenseRepository.findByOrganizationId(organizationId);
    }

    private void randomlyRunLong() {
        Random rand = new Random();
        int randomNum = rand.nextInt(3) + 1;
        if (randomNum == 3) sleep();
    }

    private void sleep() {
        try {
            Thread.sleep(5000);
            throw new java.util.concurrent.TimeoutException();
        } catch (InterruptedException | TimeoutException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private List<License> buildFallbackLicenseList(String organizationId, Throwable t) {
        List<License> fallbackList = new ArrayList<>();
        License license = new License();
        license.setLicenseId("0000000-00-00000");
        license.setOrganizationId(organizationId);
        license.setProductName("Sorry no licensing information currently available");
        fallbackList.add(license);
        return fallbackList;
    }

}