package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue // 시퀀스 값 같은 걸 쓴다
    @Column(name = "member_id")
    private Long id;

    // @NotEmpty // 프레젠테이션 계층을 위한 검증 로직을 Entity 넣은 게 됨.
    // API 파라미터에서 엔티티를 바로 쓰면 필드 명을 바꿀 시 api 스펙도 바꿔야 함.
    // 그래서 이렇게 하지 말고 DTO를 만들어야 한다.
    private String name;


    @Embedded // 내장 타입을 포함한다는 뜻
    // 한 쪽에 Embeddable 만 쓰거나 Embedded 만 써도 되는데
    // 양 쪽에 둘 다 써주는 것도 좋다
    private Address address;

    // @JsonIgnore 쓰면 JSON 형태로 출력할 때 나오지 않는다.
    // 다양한 API를 개발하게 될 테고, 어떤 API는 이를 필요로 하고 어떤 API는 그렇지 않다.
    // 그러므로 이를 @JsonIgnore를 이용해서 Entity 단에서 처리하는 건 좋지 않다. 문제가 발생한다.
    // 무엇보다 Entity에 프레젠테이션 계층에서 해야 할 일이 첨가되는 것은 좋지 않다.
    @OneToMany(mappedBy = "member") // '연관관계의 주인이 아닌 거울이다'
    private List<Order> orders = new ArrayList<>(); // 초기화를 생성자에서 해줄 수도 있지만 이게 best practice
    // null 문제에서 안전
    // hibernate가 entity를 persist 하는 순간(em.persist(~~)) collection을 감싸버려서
    // hibernate가 제공하는 내장 collection으로 변경돼 버린다.
    // -> hibernate가 변경 사항을 추적해야 하기 때문에 그렇다.
    // 근데 set해서 또 바꿔버리면 변경 사항 추적이 안된다.
    // -> 가급적이면 수정하지도, 밖으로 꺼내지도 않는 것이 좋다. 컬렉션 자체를 바꾸지를 말기.
    // 이런 이유에서도 collection은 field에서 초기화하는 것이 가장 좋다.
}
