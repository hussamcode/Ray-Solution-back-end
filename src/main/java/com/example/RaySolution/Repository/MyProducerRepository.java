package com.example.RaySolution.Repository;

import com.example.RaySolution.model.MyProducer;
import com.example.RaySolution.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MyProducerRepository extends JpaRepository<MyProducer, Long> {
    List<MyProducer> findAllByUser(User user);

    Optional<MyProducer> findByIdAndUser(Long id, User user);

    Optional<MyProducer> findBycodeAndUser(String code, User user);
}