package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.OptionalDouble;

@Service // 비즈니스 로직, 트랜잭션 처리
// 근데 핵심 비즈니스 로직은 엔티티에 있고, 서비스는 엔티티에 필요한 요청을 위임, 호출, 연결하기만 한다.
// 이런 방식을 도메인 모델 패턴이라고 한다.
// 반대로 엔티티에는 getter, setter만 있고 비즈니스 로직이 거의 없고 웬만해서 다 서비스에서 처리하는 방식도 있다.
// 이런 방식은 트랜잭션 스크립트 패턴이라고 한다.
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional // 데이터 변경하니까 필요
    public Long order(Long memberId, Long itemId, int count){

        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order); // orderItem, Delivery는 Cascade로 되어 있기에 persist 따로 안 해줘도 된다.
        return order.getId();
    }

    /**
     * 취소
     */
    @Transactional
    public void cancelOrder(Long orderId){

        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);

        // 주문 취소
        order.cancel();
        // SQL을 직접 다루는 스타일로 하면 데이터 변경 후에 레포지토리 밖에서 업데이트 쿼리를 직접 짜 날려야 한다.
        // 하지만 JPA를 쓰면 데이터만 바꾸면 JPA가 알아서 더티 체킹을 통해 업데이트 쿼리를 날려준다.
    }
    /**
     * 검색
     */
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAllByString(orderSearch);
        // 이렇게 단순 위임만 하는 코드의 경우는 서비스 코드는 생략하고 컨트롤러에서 레포지토리 접근하게 해도 된다.
    }
}
