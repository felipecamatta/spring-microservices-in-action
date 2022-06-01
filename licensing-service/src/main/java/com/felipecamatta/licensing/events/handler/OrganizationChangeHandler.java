package com.felipecamatta.licensing.events.handler;

import com.felipecamatta.licensing.events.model.OrganizationChange;
import com.felipecamatta.licensing.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class OrganizationChangeHandler {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationChangeHandler.class);

    private final OrganizationRepository organizationRepository;

    public OrganizationChangeHandler(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Bean
    public Consumer<OrganizationChange> inboundOrgChangesChannel() {
        return organization -> {
            logger.debug("Received a message of type {}", organization.getType());

            switch (organization.getAction()) {
                case "GET":
                    logger.debug("Received a GET event from the organization service for organization id {}", organization.getOrganizationId());
                    break;
                case "SAVE":
                    logger.debug("Received a SAVE event from the organization service for organization id {}", organization.getOrganizationId());
                    organizationRepository.deleteById(organization.getOrganizationId());
                    break;
                case "UPDATE":
                    logger.debug("Received a UPDATE event from the organization service for organization id {}", organization.getOrganizationId());
                    organizationRepository.deleteById(organization.getOrganizationId());
                    break;
                case "DELETE":
                    logger.debug("Received a DELETE event from the organization service for organization id {}", organization.getOrganizationId());
                    organizationRepository.deleteById(organization.getOrganizationId());
                    break;
                default:
                    logger.error("Received an UNKNOWN event from the organization service of type {}", organization.getType());
                    break;
            }
        };
    }

}
