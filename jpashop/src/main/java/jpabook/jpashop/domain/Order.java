package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;
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
// 도메인 : 엔티티가 모여 있는 계층, 모든 계층에서 사용
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id") // 보통 DBA 분들이 이 방식을 선호함.
    private Long id;

    // (fetch = FetchType.LAZY)
    @ManyToOne(fetch = LAZY) // 지연로딩이기에 new 해서 db에서 member 객체 바로 안 가져오고 order 데이터만 가져온다.
    // 근데 그렇다고 null은 넣어둘 수 없으니 hibernate가 Proxy라이브러리를 사용해 Member를 상속받는 ProxyMember를 객체를 생성해 넣어둔다.
    // 프록시 기술을 쓸 때 bytebuddy 라이브러리를 많이 써서 오류 로그에 ByteBuddyInterception이 찍히게 된다.
    // 이렇게 있다가 멤버 객체를 다룰 일이 생기면 그때 디비 멤버 객체 sql을 보내 멤버 객체 값으로 채워준다. (프록시 초기화)
    @JoinColumn(name = "member_id") // FK 이름이 member_id
    private Member member;

    // cascade = CascadeType.ALL
    @OneToMany(mappedBy = "order", cascade = ALL) // persist 전파, delete할 때 같이 지움
    // 기본이 LAZY라 세팅 안 함.
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

    //==연관관계 편의 메서드==//
    // 위치는 핵심적으로 컨트롤하는 쪽에 적어주는 게 좋다
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }// 훨씬 간결

//    public void setMember(){
//        Member member = new Member();
//        Order order = new Order();
//
//        member.getOrders().add(order);
//        order.setMember(member);
//    } 원래는 비즈니스 로직에서 이렇게 해줘야 한다 근데 이러면 코드 한 줄 깜빡할 수도 있다

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==// // 생성할 게 많을 경우엔 별도의 생성 메서드를 만드는 것이 좋다.
    // 밖에서 set 여러 개 호출해서 하는 게 아니라 생성 메서드 호출
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel(){
        // 비즈니스 로직. 체크 로직이 엔티티 안에.
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

    //==조회 로직==//

    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice(){
//        int totalPrice = 0;
//        for (OrderItem orderItem : orderItems){
//            totalPrice += orderItem.getTotalPrice();
//        }
//        return totalPrice;

//        int totalPrice = orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
//        return totalPrice;

        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }

}

