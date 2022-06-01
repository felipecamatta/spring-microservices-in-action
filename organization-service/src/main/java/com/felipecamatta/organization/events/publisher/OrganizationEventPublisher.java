package com.felipecamatta.organization.events.publisher;

import com.felipecamatta.organization.events.model.OrganizationChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class OrganizationEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationEventPublisher.class);

    private final StreamBridge streamBridge;

    public OrganizationEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publishOrganizationChangeEvent(String action, String organizationId) {
        logger.debug("Sending Kafka message {} for Organization id: {}", action, organizationId);
        OrganizationChange change = new OrganizationChange(
                OrganizationChange.class.getTypeName(),
                action,
                organizationId,
                null
        );

        streamBridge.send("orgChangeTopic", change);
    }

}
