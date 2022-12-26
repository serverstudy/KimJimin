package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository // component 스케일에서 자동으로 스프링 빈으로 등록, 관리가 된다.
public class MemberRepository {

    @PersistenceContext // JPA가 제공하는 표준 어노테이션
    private EntityManager em; // Entity Manager를 만들어서 주입해 준다.

    // 만약 엔티티 매니저 팩토리를 주입하고 싶다면 아래 코드.
    // 원래 순수 JPA를 쓰면 EntityManagerFactory에서 직접 EntityManager를 꺼내 써야 한다.
//    @PersistenceUnit
//    private EntityManagerFactory emf;
    public void save(Member member){
        em.persist(member); // JPA가 저장
        // 영속성 컨텍스트 안에 멤버 객체를 넣고
        // 나중에 트랜잭션이 커밋되는 시점에 디비에 반영된다. insert 쿼리가 날아간다.
    }

    public Member findOne(Long id){
        return em.find(Member.class, id); // 멤버 찾아서 반환 JPA의 find 메소드
        // 단건 조회
        // 타입, pk
    }

    public List<Member> findAll(){
//        List<Member> result = em.createQuery("select m from Member m", Member.class);// jpql, 반환타입
//                         .getResultList();
//                         return result;

        // 더 간결하게.
        return em.createQuery("select m from Member m", Member.class)// jpql, 반환타입
                         .getResultList();

        // jpql : 문법은 sql과 거의 똑같지만 sql은 테이블을 대상으로 쿼리를 하는 것이고, jpql은 엔티티를 대상으로 쿼리한다.
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
