package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello") // hello라는 url로 오면 이 컨트롤러 호출
    public String hello(Model model){ // spring ui에 있는 것. 데이터를 실어서 뷰에 넘길 수 있게 해준다.
        model.addAttribute("data", "hello"); // name이 data인 키의 값을 hello로 해서 넘길 것이다.
        return "hello"; // 화면 이름
        // 1. hello.html로 .html이 자동으로 붙는다.
        // 2. resources > templates의 hello.html을 찾아 열어준다.
        // 이걸 스프링 부트가 알아서 해주는 것.
        // resources:templates/ +{ViewName}+ .html 이렇게 매핑해주는 것.
        // 설정값 바꾸고 싶으면 boot 쪽 들어가 매뉴얼 보면 prefix, suffix에 대해 바꿔줄 수 있다.
    }
}
// spring initializer가 resources 파일 만들어준다.