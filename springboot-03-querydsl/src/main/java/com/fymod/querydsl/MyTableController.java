package com.fymod.querydsl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

@RestController
public class MyTableController {
	
	@PersistenceContext
    private EntityManager entityManager;
    
    private JPAQueryFactory queryFactory;
    
    @PostConstruct
    public void init() {
        queryFactory = new JPAQueryFactory(entityManager);
    }

	@GetMapping("/query")
	public List<MyTable> query(String name) {
		QMyTable qMyTable = QMyTable.myTable;
		return queryFactory
				.selectFrom(qMyTable)
				.where(qMyTable.name.eq(name)) //查询条件
				.offset(0) //偏移量，即从第几条开始匹配
				.limit(10) //每页最大记录数
				.orderBy(qMyTable.tableId.desc()) //排序规则
				.fetch(); //如果返回结果是单个对象，可以使用fetchOne
	}
	
	@GetMapping("/query2")
	public List<MyTableDto> query2() {
		QMyTable qMyTable = QMyTable.myTable;
		return queryFactory
				.select(Projections.bean(MyTableDto.class, qMyTable.name.as("myname")))
				.from(qMyTable)
				.fetch(); //如果返回结果是单个对象，可以使用fetchOne
	}
	
}
