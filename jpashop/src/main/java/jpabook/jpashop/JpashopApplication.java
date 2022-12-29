package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // 이 어노테이션이 있으면 해당 패키지와 이 패키지 하위에 있는 것들을 모두 스프링이 컴포넌트 스케일화 해
// 스프링 빈에 자동 등록
public class JpashopApplication {


	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

}
