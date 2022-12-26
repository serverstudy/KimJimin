package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
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
@Getter
public abstract class Item {
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<Category>();

    //==비즈니스 로직==//
    // 도메인 주도 설계. 엔티티 자체가 해결할 수 있는 건 비즈니스 로직이어도 엔티티 안에 넣는 것이 좋다.
    // 응집도를 높이는, 조금 더 객체지향적인 설계가 된다.
    // 애트리뷰트를 Setter를 두는 것이 아니라 핵심 비즈니스 메소드를 통해 변경을 해야 하는 것
    /**
     * stock 증가
     */
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    /**
     * stack 감소
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0){
            throw new NotEnoughStockException("need more stock.");
        }
        this.stockQuantity = restStock;
    }
}

// 상속 관계 매핑이기 때문에 전략 설정을 해줘야 한다.
