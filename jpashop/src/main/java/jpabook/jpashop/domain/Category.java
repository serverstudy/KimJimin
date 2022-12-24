package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class Category {
    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"), // 중간 테이블의 id
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    // 관계형 디비는 컬렉션 관계를 양쪽에 가질 수 있는 게 아니기 떄문에
    // 일대다 다대일로 풀어내는 중간 테이블이 있어야 한다.
    private List<Category> items = new ArrayList<>();

    // 카테고리 구조 : 계층 구조
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

}
