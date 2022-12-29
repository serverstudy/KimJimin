package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){ // Item은 JPA 저장하기 전까지 id가 없다.
        if(item.getId() == null){ // 새로 생성한 객체라는 의미 -> 신규 등록
            em.persist(item);
        }else{ // 디비에 이미 있어서 가져 온 객체라는 의미 -> 업데이트
            em.merge(item);
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
