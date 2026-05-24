package com.example.RaySolution.Repository;


import com.example.RaySolution.model.MyProducer;
import com.example.RaySolution.model.Order;
import com.example.RaySolution.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUser(User user);

    Optional<Order> findBycode(String code);

    Optional<Order> findByIdAndUser(Long id, User user);

    Optional<Order> findBycodeAndUser(String code, User user);

}
