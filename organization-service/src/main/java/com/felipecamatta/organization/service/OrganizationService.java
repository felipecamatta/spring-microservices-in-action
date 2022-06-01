package com.felipecamatta.organization.service;

import com.felipecamatta.organization.events.publisher.OrganizationEventPublisher;
import com.felipecamatta.organization.model.Organization;
import com.felipecamatta.organization.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.ScopedSpan;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

    private final OrganizationRepository organizationRepository;
    private final OrganizationEventPublisher organizationEventPublisher;
    private final Tracer tracer;

    public OrganizationService(OrganizationRepository organizationRepository,
                               OrganizationEventPublisher organizationEventPublisher,
                               Tracer tracer) {
        this.organizationRepository = organizationRepository;
        this.organizationEventPublisher = organizationEventPublisher;
        this.tracer = tracer;
    }

    public Organization findById(String organizationId) {
        ScopedSpan newSpan = tracer.startScopedSpan("getOrgDBCall");
        Optional<Organization> opt = organizationRepository.findById(organizationId);
        if (opt.isEmpty()) {
            String message = String.format("Unable to find an organization with theOrganization id %s",
                    organizationId);
            logger.error(message);
            throw new IllegalArgumentException(message);
        }
        logger.debug("Retrieving Organization Info: {}", opt.get());
        newSpan.tag("peer.service", "postgres");
        newSpan.event("Client received");
        newSpan.end();
        return opt.orElse(null);
    }

    public Organization create(Organization organization) {
        organization.setId(UUID.randomUUID().toString());

        ScopedSpan newSpan = tracer.startScopedSpan("createOrgDBCall");
        organization = organizationRepository.save(organization);
        logger.debug("Creating Organization Info: {}", organization);
        newSpan.tag("peer.service", "postgres");
        newSpan.event("Client received");
        newSpan.end();

        organizationEventPublisher.publishOrganizationChangeEvent("SAVE", organization.getId());

        return organization;
    }

    public Organization update(String id, Organization organization) {
        ScopedSpan newSpan = tracer.startScopedSpan("updateOrgDBCall");
        organization.setId(id);
        organizationRepository.save(organization);
        logger.debug("Updating Organization Info: {}", organization);
        newSpan.tag("peer.service", "postgres");
        newSpan.event("Client received");
        newSpan.end();

        organizationEventPublisher.publishOrganizationChangeEvent("UPDATE", organization.getId());

        return organization;
    }

    public void delete(String organizationId) {
        ScopedSpan newSpan = tracer.startScopedSpan("deleteOrgDBCall");
        organizationRepository.deleteById(organizationId);
        logger.debug("Deleting Organization: {}", organizationId);
        newSpan.tag("peer.service", "postgres");
        newSpan.event("Client received");
        newSpan.end();

        organizationEventPublisher.publishOrganizationChangeEvent("DELETE", organizationId);
    }

}
