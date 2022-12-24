package jpabook.jpashop;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.websocket.server.ServerEndpoint;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue// 식별자를 id에 매핑, 자동 생성되게
    private Long id;
    private String username;

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    } command + N > getter and setter해서 만들어 줘도 되지만 lombok을 쓰니까
}
