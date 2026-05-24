package com.example.RaySolution.model;

import com.example.RaySolution.Brand;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "myproducer")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyProducer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Integer producerAdd;


}
