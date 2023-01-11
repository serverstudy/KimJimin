package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication // 이 어노테이션이 있으면 해당 패키지와 이 패키지 하위에 있는 것들을 모두 스프링이 컴포넌트 스케일화 해
// 스프링 빈에 자동 등록
public class JpashopApplication {


	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	@Bean
	Hibernate5Module hibernate5Module() {
		Hibernate5Module hibernate5Module = new Hibernate5Module(); //강제 지연 로딩 설정
		hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);

		return new Hibernate5Module();
	}
	// 지연로딩은 무시하고 진행하도록.
	// Order 조회할 때 Memebr, Delivery는 LAZY로 되어 있으니까 Order만 조회
}
