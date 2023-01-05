package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {

//    Logger log = LoggerFactory.getLogger(getClass());
    // org.slf4j
    // @Slf4j 쓰면 된다.

    @RequestMapping("/")
    public String home(){
        log.info("home controller");
        return "home"; // home.html로 찾아가서
    }
}
