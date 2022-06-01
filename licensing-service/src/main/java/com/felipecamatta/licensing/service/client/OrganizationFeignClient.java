package com.felipecamatta.licensing.service.client;

import com.felipecamatta.licensing.config.FeignClientConfig;
import com.felipecamatta.licensing.model.Organization;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "organization-service", configuration = FeignClientConfig.class)
public interface OrganizationFeignClient {

    @GetMapping("api/v1/organization/{organizationId}")
    Organization getOrganization(@PathVariable String organizationId);

}
