package com.fymod.datarest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "mytable", path = "mytable")
public interface MyTableRepository extends JpaRepository<MyTable, Long>{
	
}

