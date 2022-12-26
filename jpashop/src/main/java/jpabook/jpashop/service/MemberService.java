package jpabook.jpashop.service;

import com.sun.org.apache.bcel.internal.generic.LNEG;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // java에서 제공하는 것도 있지만 스프링에서 제공하는 어노테이션으로 쓰는 게 더 좋다.
// 쓸 수 있는 옵션이 더 많아서

import java.util.List;

@Service // component 스캔의 대상이 돼 spring bin으로 등록된다.
@Transactional(readOnly = true) // JPA의 모든 데이터 변경이나 로직은 가급적이면 트랜잭션 안에서 실행되어야 한다.
public class MemberService {

    @Autowired // 주입이 된다.
    private MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    @Transactional // 읽기 전용인 기능이 많으니까 전체를 readOnly = true로 해두고 이 기능만 false로
    public Long join(Member member){
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }


    /**
     * 회원 전체 조회
     */
     // 조회 기능엔 readOnly = true를 주면 JPA가 조회 기능에 맞게 성능을 더 최적화한다.
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    /**
     * 회원 한 명 조회
     */
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}
