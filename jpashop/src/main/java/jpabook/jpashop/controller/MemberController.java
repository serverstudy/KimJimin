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
        // && Valid 쓰려면 Member에 @NotEmpty 써줘야 한다. 코드가 지저분해진다. 

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

}
