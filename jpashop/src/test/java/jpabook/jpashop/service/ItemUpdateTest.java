package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {

    @Autowired EntityManager em;

    @Test
    public void updateTest() throws Exception {
        Book book = em.find(Book.class, 1L);

        //TX
        book.setName("asdsd");

        //TX commit
        // JPA가 변경본에 대해서 변경사항을 찾아 업데이트 쿼리를 자동으로 생성해 데이터베이스에 반영한다.
        // 더티체킹. 변경 감지
        // 이게 기본 매커니즘





    }

}
