package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpashopApplication {


	public static void main(String[] args) {

		Hello hello = new Hello();
		hello.setData("hello");
String data = hello.getData();
// sout 쓰면 자동 완성된다.
		System.out.println("data = "+ data);
		SpringApplication.run(JpashopApplication.class, args);
	}

}
