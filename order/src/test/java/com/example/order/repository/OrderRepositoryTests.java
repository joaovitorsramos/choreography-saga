package com.example.order.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.order.domain.Order;
import com.example.order.domain.OrderItem;
import com.example.order.domain.Status;

@RunWith(SpringRunner.class)
@DataJpaTest
public class OrderRepositoryTests {

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	TestEntityManager entityManager;

	@Test
	public void whenFindByIdThenReturnOrder() {

		// given
		List<OrderItem> orderItemsList = new ArrayList<>();

		orderItemsList.add(new OrderItem("123_aspirin", 100, "Paulista", 100.00));
		orderItemsList.add(new OrderItem("456_ibuprofen", 200, "Brooklin", 100.00));
		Order order = new Order("1111", "1111", "101010", Status.APPROVAL_PENDING, orderItemsList);
		entityManager.persist(order);
		entityManager.flush();

		// when
		Optional<Order> found = orderRepository.findById(order.getOrderId());

		// then
		assertThat(found.get().getCustomerId()).isEqualTo(order.getCustomerId());
	}

}
