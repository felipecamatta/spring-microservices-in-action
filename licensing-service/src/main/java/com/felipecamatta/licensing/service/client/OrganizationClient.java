package com.felipecamatta.licensing.service.client;

import com.felipecamatta.licensing.model.Organization;
import com.felipecamatta.licensing.repository.OrganizationRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.ScopedSpan;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;

@Component
public class OrganizationClient {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationClient.class);

    private final OrganizationFeignClient organizationFeignClient;
    private final OrganizationRepository organizationRepository;
    private final Tracer tracer;

    public OrganizationClient(OrganizationFeignClient organizationFeignClient,
                              OrganizationRepository organizationRepository,
                              Tracer tracer
    ) {
        this.organizationFeignClient = organizationFeignClient;
        this.organizationRepository = organizationRepository;
        this.tracer = tracer;
    }

    private Organization checkRedisCache(String organizationId) {
        logger.debug("Caching organization to the Redis cache: {}", organizationId);
        ScopedSpan newSpan = tracer.startScopedSpan("readLicensingDataFromRedis");
        try {
            return organizationRepository
                    .findById(organizationId)
                    .orElse(null);
        } catch (Exception ex) {
            logger.error("Error encountered while trying to retrieve organization {} check Redis Cache.  Exception {}",
                    organizationId, ex);
            return null;
        } finally {
            newSpan.tag("peer.service", "redis");
            newSpan.event("Client received");
            newSpan.end();
        }
    }

    private void cacheOrganizationObject(Organization organization) {
        logger.debug("Trying to locate organization from the Redis cache: {}", organization.getId());
        ScopedSpan newSpan = tracer.startScopedSpan("writeLicensingDataToRedis");
        try {
            organizationRepository.save(organization);
            logger.debug("Cached organization successfully: {}", organization.getId());
        } catch (Exception ex) {
            logger.error("Unable to cache organization {} in Redis. Exception {}",
                    organization.getId(), ex);
        } finally {
            newSpan.tag("peer.service", "redis");
            newSpan.event("Client received");
            newSpan.end();
        }
    }

    @CircuitBreaker(name = "organizationService")
    public Organization getOrganization(String organizationId) {
        Organization organization = checkRedisCache(organizationId);

        if (organization != null) {
            logger.debug("Successfully retrieved an organization {} from Redis cache: {}",
                    organizationId, organization);
            return organization;
        }

        logger.debug("Unable to locate organization from the Redis cache: {}", organizationId);

        organization = organizationFeignClient.getOrganization(organizationId);

        if (organization != null) {
            cacheOrganizationObject(organization);
        }

        return organization;
    }

}
