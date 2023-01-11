package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class Delivery {
    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id; // 객체는 클래스가 있으니까 그냥 id로 써주면 되는데
    // 테이블을 만들 F게 되면 그렇지 않으니까 외래키 쓰는 경우와 맞춰서 어느 테이블의 id인지 함께 써주는 게 좋다

    @JsonIgnore
    @OneToOne(mappedBy = "delivery", fetch = LAZY) // 연관관계의 거울
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; // READY, COMP
    // Enum은 주의하기. Enumerated 어노테이션 넣어주어야 하고
    // EnumType Ordinal : 숫자로 들어간다. -> 중간에 다른 상태가 생기면 곤란 -> 절대 쓰지 말기
    // String : 중간에 새로운 게 들어와도 순서에 의해 밀리는 게 없다
}
