package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class) // 스프링이랑 integration해서 테스트할 거라서
@SpringBootTest // 스프링이랑 integration해서 테스트할 거라서
@Transactional // 데이터 변경해야 하니까
// persist로 insert 쿼리가 가는 것은 커밋을 할 때인데 테스트 코드에서의 Transactional은 커밋이 아니라 롤백을 한다.
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;
    @Test
//    @Rollback(false) // 롤백 안하고 커밋. 또는 em 만들어 flush
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("kim");

        // when
        Long savedId = memberService.join(member);

        // then
        em.flush(); // 영속성 컨텍스트에 있는 객체가 쿼리로 디비에 반영된다.
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        // when
//        memberService.join(member1);
//        try {
//            memberService.join(member2); // 예외가 발생해야 한다.
//        } catch (IllegalStateException e){
//            return;
//        }
        memberService.join(member1);
        memberService.join(member2);

        // then
        fail("예외가 발생해야 한다.");
    }
}