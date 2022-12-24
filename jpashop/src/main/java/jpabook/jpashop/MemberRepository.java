package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository // 컴포넌트 스케일의 대상이 되는 어노테이션 중에 하나. 자동으로 스프링 빈에 등록이 된다.
public class MemberRepository {
    // Repository : 멤버를 찾아주는. DAO랑 비슷.
    @PersistenceContext // JPA를 쓰기 때문에 엔티티 매니저가 있어야 한다. 이걸 쓰면 엔티티 매니저를 주입을 해준다.
    private EntityManager em; // 스프링 부트를 쓰기 떄문에 모든 게 스프링 컨테이너 위에서 동작한다.

    public Long save(Member member){
        em.persist(member);
        return member.getId();
        // 커맨드와 쿼리를 분리하라는 원칙. 저장을 하고 나면 가급적이면 리턴 값을 안 만드는 게 좋은데,
        // id 정도 있으면 나중에 다시 조회하기 좋으니까
    }

    public Member find(Long id){ // 조회
        return em.find(Member.class, id);
    }
}
