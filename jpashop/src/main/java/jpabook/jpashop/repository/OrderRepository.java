package jpabook.jpashop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.QMember;
import jpabook.jpashop.domain.QOrder;
import jpabook.jpashop.repository.order.simplequery.SimpleOrderQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository // JPA를 직접 사용하는 계층, 엔티티 매니저 사용
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch){
//        return em.createQuery("select o from Order o join o.member m " +
//                        "where o.status = :status " +
//                        "and m.name like :name", Order.class)
//                .setParameter("status", orderSearch.getOrderStatus())
//                .setParameter("name", orderSearch.getMemberName())
//                .setMaxResults(1000) // 최대 1000개 조회
//                .getResultList();

        String jpql = "select o from Order o join o.member m ";
        boolean isFirstCondition = true;

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null){
            if(isFirstCondition){
                jpql += "where";
                isFirstCondition = false;
            }else{
                jpql = "and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class) .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    /**
     * JPA Criteria; JPQL을 java로 작성할 수 있도록 JPA가 표준으로 제공해주는 게 있다. 이를 이용하는 방식.
     * 실무에서는 쓰지 않는다.
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if(orderSearch.getOrderStatus() != null){
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    public List<Order> findAll(OrderSearch orderSearch) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QOrder order = QOrder.order;
        QMember member = QMember.member;


        return query
                .select(order)
                .from(order)
                .join(order.member, member)
                .where(statusEq(orderSearch.getOrderStatus()), member.name.like(orderSearch.getMemberName()))
                .limit(1000)
                .fetch();
    }

    private BooleanExpression statusEq(OrderStatus statusCond) {
        if (statusCond == null) {
            return null;
        }
        return QOrder.order.status.eq(statusCond);
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" + // order를 가져올 때 member 객체 그래프도 한 번에 같이 가져온다.
                        " join fetch o.delivery d", Order.class // join이면서 select 절에서 같이 가져온다
                // fetch join: member, delivery가 LAZY이지만 이를 무시하고 프록시도 아니고 값을 모두 채워 가져온다.
                // fetch: SQL에는 없고 JPA에만 있는 문법
        ).getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        // Order를 기준으로 toOne으로 걸린 것들은 fetch join으로 가져오기
        return em.createQuery(
                "select o from Order o" + // 여기까지만 써줘도 된다. 모두 in 쿼리 방식으로 변경된다.
                        // 하지만 그만큼 네트워크를 더 많이 쓰게 되는 거니까 toOne은 그냥 join fetch로 써주는 게 좋다.
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        )
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
        // toOne 관계에 fetch join한 것은 페이징 적용이 잘 된다.
    }

    public List<Order> findAllWithItem() {
        return em.createQuery(
                // distinct를 쓰지 않으면 참조 값까지 동일하게 두 배로 나온다.
                // distinct를 쓰면 해결이 된다.
                // 디비의 distinct와는 다르다. 디비의 distinct는 모든 컬럼 값이 똑같아야만 적용이 된다.
                // JPA의 distinct는 디비에 distinct 키워드를 붙여주는 것 + 추가적인 일을 해준다. 엔티티의 id가 같으면 자체적으로 중복을 제거해준다.
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class)
                // 단 1대다에서 fetch join을 하면 페이징 불가능하다.
//                .setFirstResult(1)
//                .setMaxResults(100)
                // warning이 뜬다. 디비 쿼리 단계에서는 1대다에서 '다' 기준으로 데이터가 늘어나버리니
                // 제대로 페이징을 할 수 없어 메모리에서 페이징 처리를 해 버린다.
                // 데이터가 많을 경우엔 out of memory 문제가 발생하게 된다.

                // + 컬렉션 fetch join은 1개만 사용 가능하다.
                // 둘 이상에 사용하면 데이터가 매우 큰 폭으로 늘어나며 부정합 문제가 발생한다.
                .getResultList();
    }
}
