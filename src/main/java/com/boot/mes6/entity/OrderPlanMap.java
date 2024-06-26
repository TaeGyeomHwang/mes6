package com.boot.mes6.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "order_plan_map")
@Getter
@Setter
public class OrderPlanMap {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderPlanMapNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_no", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_no", nullable = false)
    private Plan plan;
}
