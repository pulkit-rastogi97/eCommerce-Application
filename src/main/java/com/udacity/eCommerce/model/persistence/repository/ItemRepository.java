package com.udacity.eCommerce.model.persistence.repository;

import com.udacity.eCommerce.model.persistence.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	public List<Item> findByName(String name);

}
