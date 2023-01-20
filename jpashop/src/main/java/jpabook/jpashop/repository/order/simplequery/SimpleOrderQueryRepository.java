package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SimpleOrderQueryRepository {

    private final EntityManager em;

    // 화면에 dependent한 게 리포지토리에 있으면 용도가 애매해진다.
    // 별도로 분리해 관리하는 게 유지보수에 훨씬 좋다.

    // 조회 전용으로 화면에 맞춰 쓰는 함수
    public List<SimpleOrderQueryDto> findOrderDtos() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.simplequery.SimpleOrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", SimpleOrderQueryDto.class)
                .getResultList();
    }
}
// v4까지 했는데도 성능 개선이 더 필요하거나 디비에서 제공하는 네이티브한 기능을 써야 할 때는
// JPA가 제공하는 네이티브 SQL이나
// queryRepository에서 entity manager가 아니라 직접 데이터베이스 커넥션을 받거나 스프링 JDBC Template을 사용해서
// SQL을 직접 사용한다.
