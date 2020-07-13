package com.udacity.eCommerce.model.persistence.repository;

import com.udacity.eCommerce.model.persistence.entity.User;
import com.udacity.eCommerce.model.persistence.entity.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<UserOrder, Long> {
	List<UserOrder> findByUser(User user);
}
