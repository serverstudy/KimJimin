package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.bytecode.enhance.spi.interceptor.AbstractLazyLoadInterceptor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "orders")// 안 써주면 order로 인식해서 잘 안 된다.
@Getter @Setter
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id") // 보통 DBA 분들이 이 방식을 선호함.
    private Long id;

    // (fetch = FetchType.LAZY)
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id") // FK 이름이 member_id
    private Member member;

    // cascade = CascadeType.ALL
    @OneToMany(mappedBy = "order", cascade = ALL) // persist 전파, delete할 때 같이 지움
    private List<OrderItem> orderItems = new ArrayList<>();


    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "delivery_id") // 연관관계의 주인
    // 일대일 관계에서는 FK를 어느 쪽에든 둬도 되는데
    // 접근을 더 많이 하는 곳에 두는 게 편하다.
    // Delivery를 갖고 order를 조회하는 것보다 order를 갖고 delivery를 조회하는 게 더 많으니
    // Order에 두기.
    private Delivery delivery;

//    private Date date; 로 하면 annotation 매핑을 해야 하지만
    private LocalDateTime orderDate; // Java 8에서는 이를 쓰면 된다. 시, 분까지 있다
    // 스프링 부트; SpringPhysicalNamingStrategy 에 따라서 order_date로 바꾼다.
    // Java의 Camel case -> under score & lower case로 바꿔버린다.
    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문의 상태 [ORDER, CANCEL]

}
