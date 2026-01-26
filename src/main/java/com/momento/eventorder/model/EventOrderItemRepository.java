package com.momento.eventorder.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventOrderItemRepository extends JpaRepository<EventOrderItemVO, Integer>{

}
