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
            // 준영속성 상태 엔티티를 영속 상태로 변경
            // ItemService에 작성한 updateItem 코드를 JPA가 한 줄로 해주는 기능.
            // 근데 보낸 item이 영속성 엔티티로 바뀌는 건 아니고 merge 후 반환된 게 영속성 엔티티

            // 주의점; 변경 감지 기능을 사용하면 원하는 속성만 선택해 변경할 수 있지만, 병합은 그렇지 않다.
            // 모든 속성이 다 바뀌게 된다.
            // 병합 시 값이 없으면 null로 업데이트 해버린다.
            // 실무에서는 merge보다는 변경 감지 기능 쓰기.
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
