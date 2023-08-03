package jpabook.jpashop.service;

import com.sun.org.apache.bcel.internal.generic.LNEG;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // java에서 제공하는 것도 있지만 스프링에서 제공하는 어노테이션으로 쓰는 게 더 좋다.
// 쓸 수 있는 옵션이 더 많아서

import java.util.List;

@Service // component 스캔의 대상이 돼 spring bin으로 등록된다.
@Transactional(readOnly = true) // JPA의 모든 데이터 변경이나 로직은 가급적이면 트랜잭션 안에서 실행되어야 한다.
//@AllArgsConstructor
@RequiredArgsConstructor // final 인 필드에 대해서만 생성자 만들어준다.
public class MemberService {

    //    @Autowired // 주입이 된다. 스프링 빈에 있는 걸로. 필드 인젝션
    private final MemberRepository memberRepository; // 생성자 만들어 놓고 값 세팅 안하면
    // 컴파일 시점에 하라고 체크를 해준다.

//    // Setter 인젝션
//    // 장점: 테스트 코드 작성을 할 때 mock을 직접 주입해 줄 수가 있다. 필드는 그렇지 않다.
//    // 단점: 런타임에 변화가 일어날 수도 있다
//    @Autowired
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    // Constructor 인젝션
//    @Autowired // 생성자가 하나만 있는 경우엔 생략 가능. 스프링이 자동으로 인젝션 해준다.
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }
    // @AllArgsConstructor 쓰면 lombok이 알아서 만들어 준다.

    /**
     * 회원 가입
     */
    @Transactional // 읽기 전용인 기능이 많으니까 전체를 readOnly = true로 해두고 이 기능만 false로
    // 영속성 컨텍스트 만들어지고 데이터베이스 커넥션 가져오고
    // OSIV OFF라면 로직이 끝난 후 데이터 베이스 커넥션 플러시 커밋하고 영속성 컨텍스트를 없애고 데이터 베이스 커넥션 반환
    public Long join(Member member){
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName()); // 더 안전하게 하려면 unique 제약 조건 걸어주는 게 더 좋다.
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

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
        // member를 반환할 수도 있지만 그렇게 되면 커맨드와 쿼리를 한 메소드 안에서 하는 게 된다.
        // id 정도만 반환하거나 아예 반환하지 않는 게 깔끔하다.
    }
}
