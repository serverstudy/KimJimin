package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

// @Controller
// @ResponseBody : JSON을 XML로 바로 보낼 때 주로 사용하는 어노테이션
@RestController // @Controller, @ResponseBody 포함하고 있다
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        // @RequestBody : JSON으로 온 body를 Member에 그대로 매핑해서 넣어준다.

        Long id = memberService.join(member); // OSIV OFF라면 이렇게 반환된 후엔 영속성 컨텍스트도 끝, 데이터베이스 커넥션도 끝
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        // API를 개발할 땐 엔티티를 파라미터로 받지 말기.
        // 엔티티 함부로 노출시키지 말기.

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // PUT은 멱등하다. 같은 걸 여러 번 호출해도 결과가 똑같다.
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request){
        // 커맨드와 쿼리 분리 -  유지보수성 증대
        memberService.update(id, request.getName()); // 커맨드
        Member findMember = memberService.findOne(id); // 쿼리
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
        // Array로 반환하면 스펙이 굳어버린다. 유연성이 떨어지게 된다.
        // 예를 들어 count를 같이 보내달라고 하는 경우, JSON 스펙이 깨져버리게 된다.
    }

    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect.size(), collect); // 오브젝트 타입으로 반환
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data; // 리스트는 데이터 필드 값으로
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }
    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }
}
