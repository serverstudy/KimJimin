package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
//Junit에게 스프링 관련된 걸로 테스트할 거라는 걸 알려주는 것
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    // memberRepository injection 받기
    // tdd 쓰고 탭하면 만들어지게 할 수 있다. Live template에 설정하면  intelliJ에서 해준다.
    @Test
    @Transactional // 스프링 것을 쓰기를 권장 - 쓸 수 있는 옵션이 많기 때문
    public void testMember() throws Exception{
        // given
        Member member = new Member();
        member.setUsername("memberA");

        // when
        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.find(saveId);
// 엔티티 매니저를 통한 모든 데이터 변경은 항상 트랜잭션 안에서 이루어져야 한다.
        // then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());

    }

}