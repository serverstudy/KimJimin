package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// Joined : 가장 정규화된 스타일
// Single Table : 한 테이블에 모든 걸 넣는 스타일
// Table per class : Book, Movie, Album 세 개의 테이블만 나오게 하는 전략
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<Category>();

}

// 상속 관계 매핑이기 때문에 전략 설정을 해줘야 한다.
