package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        // 기존에 controller에 만들어 둔 OrderDto를 쓰게 되면 Repository가 Controller를 참조하게 되는 순환 관계 만들어지게 된다.
        List<OrderQueryDto> result = findOrders(); // query 1번 -> N개 파생 : N + 1 문제 발생
        // 컬렉션 아닌 것들은 바로 가져오고

        // 컬렉션에 해당하는 건 forEach 루프를 돌려 직접 채우기
        // toMany 관계는 조인하면 row 수가 증가하기 때문에 findOrders에서 join해서 가져오지 않은 것.
        // findOrderItems 라는 별도의 메서드로 조회
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); // 파생된 query N번.
            o.setOrderItems(orderItems);
        });

        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" + // OrderItem 입장에서 item은 toOne 관계라서 join 해도 괜찮다.
                        " where oi.order.id = :orderId", OrderItemQueryDto.class
        ).setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class
                // toOne 관계는 조인을 해도 데이터의 row 수가 증가하지 않아 toOne 관계들은 join 해서 최적화하여 조회
        ).getResultList();
    }
}
