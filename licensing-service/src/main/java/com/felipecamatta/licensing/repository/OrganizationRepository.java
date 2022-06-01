package com.felipecamatta.licensing.repository;

import com.felipecamatta.licensing.model.Organization;
import org.springframework.data.repository.CrudRepository;

public interface OrganizationRepository extends CrudRepository<Organization, String> {
}
