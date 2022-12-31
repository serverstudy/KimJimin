package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model){ // Model
        model.addAttribute("memberForm", new MemberForm()); // Controller에서 View로 넘어갈 때 데이터를 실어서 넘긴다.
        // memberForm을 이름으로 하는 빈 껍데기 MemberForm 객체를 갖고 간다.
        // validation 같은 걸 하기 위해.
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result){ // form이 파라미터로 넘어온다.
        // Member가 아니라 MemberForm을 따로 만들어줘서 MemberForm으로 처리하는 이유 : Member에는 id, orders 처럼 form 다룰 때 불필요한 것들 같이 있으니까.
        // && Valid 쓰려면 Member에 @NotEmpty 써줘야 한다.
        // Entity에 화면에서 처리되기 위해 필요한 코드들이 증가하면서 코드가 지저분해진다.
        // 화면 종속적인 부분이 계속 늘어난다.
        // 유지보수가 어려워진다.
        // Entity는 최대한 다른 곳에 dependency 없이 순수하게 유지하는 게 중요하다. 오직 핵심 비즈니스 로직에만 dependency가 있도록
        // 이래야 애플리케이션이 커져도 유지보수하기가 너무 힘들어지지 않는다.
        // 화면에 맞는 API 같은 건 Entity가 아니라 DTO, Form 객체를 사용하는 게 좋다.

        // BindingResult를 안 써주면 @Valid로 검사할 때 오류가 발견됐으면 튕겨버리는데
        // BindingResult를 써주면 오류가 BindingResult에 담기고 코드가 이어서 실행된다.

        if (result.hasErrors()){
            return "members/createMemberForm";
        }
        // Spring, Thymeleaf가 integration이 잘 돼 있다.
        // 위 코드를 써주면 이동하는 화면으로 BindingResult를 지닌 채 가 주고 에러를 화면에 보여준다.

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member); // 저장
        return "redirect:/"; // 보통 저장하는 등의 이런 경우엔 재로딩되면 안좋기 때문에 redirect를 많이 한다.
        // redirect로 홈에 보내는 코드.
    }

    @GetMapping("/members")
    public String list(Model model){ // Model 이라는 객체를 통해서 화면에 데이터를 전달한다.
        List<Member> members = memberService.findMembers();
        // 사실 여기서도 member Entity를 쓰기보다는 DTO로 변환을 해서 쓰는 게 좋다.
        // 필요한 데이터들만 뽑아 쓰는 것.
        // 서버에서 템플릿 엔진으로 렌더링할 때는 이렇게 해도 큰 문제가 되지 않는데
        // API를 만들 때는 절대 이렇게 해서는 안된다. 절대 Entity를 외부로 반환해서는 안된다.
        // Entity에 필드를 변경하면 API 스펙도 바뀌어 버리는 문제 + 보안상 문제
        model.addAttribute("members",members);
        return "members/memberList";
    }
}
