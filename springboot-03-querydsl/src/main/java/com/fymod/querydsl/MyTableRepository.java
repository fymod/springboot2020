package com.fymod.querydsl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface MyTableRepository extends JpaRepository<MyTable, Long>, QuerydslPredicateExecutor<MyTable>{
	
}

