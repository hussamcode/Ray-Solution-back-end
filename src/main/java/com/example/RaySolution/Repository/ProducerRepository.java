package com.example.RaySolution.Repository;

import com.example.RaySolution.model.Producer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProducerRepository extends JpaRepository<Producer, Long> {
    Optional<Producer> findBycode(String code);

}
