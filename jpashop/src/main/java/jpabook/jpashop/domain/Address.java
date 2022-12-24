package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // 내장 타입이므로 어딘가에 내장이 된다는 거 설정.
@Getter // 값 타입이라서 Setter 만들지 않고 Getter만 만든다.
// 값이라는 것 자체가 immutable하게 설계되어야 한다. 변경되면 안된다.
// 생성할 때만 값이 세팅이 되고 세터 제공 X
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() {
    } // 함부로 new로 생성하면 안됨을 표현
    // 리플렉션이나 프록시 같은 기술을 사용하기 위해 필요

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
