package com.example.RaySolution.model;

import com.example.RaySolution.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(unique = true)
    private String code;
    @ElementCollection
    private List<String> producerCode = new ArrayList<>();
    @Column
    private LocalDateTime acceptableAT;
    @Column
    private LocalDateTime deliveryAt;
    @Enumerated(EnumType.STRING)
    @Column()
    private OrderStatus status;

    @Column()
    private String phonenumber;
    @Column
    private String name;
    @Column
    private String establishmentname;

    private Double latitude;

    private Double longitude;

    private String address;

    @PrePersist
    protected void onCreate() {

        if (status == null) {
            status = OrderStatus.AWAITING_CONFIRMATION;
        }
        if (code == null) {
            code = "ORD-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        }
    }


}
