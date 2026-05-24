package com.example.RaySolution.model;

import com.example.RaySolution.Brand;
import jakarta.persistence.*;
import lombok.*;


@Entity(name = "producer")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Producer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] image;

    @Column(nullable = false)
    private Integer stowage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Brand brand;

    @Column(nullable = false)
    private Integer producerAdd;


    @PrePersist
    protected void onCreate() {
        if (brand == null) {
            brand = Brand.Other;
        }
        if (producerAdd == null) {
            producerAdd = 0;
        }

    }


}
