package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.SimpleOrderQueryDto;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne
 *
 * Order
 * Order -> Member (ManyToOne)
 * Order -> Delivery (OneToOne)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        // Order에 Member가 있어 Member로 가면 Member에 Order가 있어 또 Order로 가는 무한루프에 빠진다.
        // 양방향 연관관계 문제가 생긴다.

        for (Order order : all) {
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기환
        }

        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        // ORDER 2개
        // 1 + N + N 문제 (최악의 경우; 서로 다른 유저일 때)
        // 지연로딩은 영속성 컨텍스트에서 조회하기 때문에 서로 같은 유저라면 보내지는 쿼리가 1 + N + N 보다 적을 수도 있다.
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        return orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderQueryDto> ordersV4() {
        return orderRepository.findOrderDtos();
    }
    // 재사용성은 v3가 더 좋다. v4는 화면에는 최적화되지만 재사용성이 없다.
        // 리포지토리: 엔티티의 객체 그래프를 조회할 때 사용되는 것.
        // 근데 API 스펙에 맞게 리포지토리 코드가 만들어져버린 상황.
        // 리포지토리가 화면에 의존하게 되는, 논리적으로 계층이 깨지는 상황.
    // 코드도 v3이 더 깔끔하다.
    // v3보다 v4가 select 절이 더 간결하다. 네트워크를 덜 사용한다.
        // 성능 테스트를 직접 해보는 게 맞다.
        // 하지만 대개 성능 개선은 from, where 절에서 많이 일어나는 것이지 select 절에서의 성능 개선은 큰 영향을 미치지 않는다.
        // select 절의 필드가 수십 개인 경우라면 그땐 고민을 해보는 게 좋다.
    // tradeoff가 있다.

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) { // DTO가 Entity를 파라미터로 받는 건 크게 문제되지 않는다.
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate  = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }
}
