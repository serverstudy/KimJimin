package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue // 시퀀스 값 같은 걸 쓴다
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded // 내장 타입을 포함한다는 뜻
    // 한 쪽에 Embeddable 만 쓰거나 Embedded 만 써도 되는데
    // 양 쪽에 둘 다 써주는 것도 좋다
    private Address address;

    @OneToMany(mappedBy = "member") // '연관관계의 주인이 아닌 거울이다'
    private List<Order> orders = new ArrayList<>();
}
