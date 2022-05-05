package com.G3.scm.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface RoleRepository extends CrudRepository<Role, String> {
	Role findByPapel(String role);

}
