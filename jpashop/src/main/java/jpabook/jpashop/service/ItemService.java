package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService { // ItemRepository에 단순히 위임만 하는 클래스
    private final ItemRepository itemRepository;

    @Transactional // 메소드에 가까운 것이 우선권을 가진다.
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, Book param){
        Item findItem = itemRepository.findOne(itemId);

        // 실무에서는 이렇게 여러 개의 set으로 하지 말고 의미있는 메소드를 만들어서 이를 사용해야 한다.
        findItem.setPrice(param.getPrice());
        findItem.setName(param.getName());
        findItem.setStockQuantity(param.getStockQuantity());
//        itemRepository.save(findItem); // 할 필요 없다.
        // findOne으로 찾아 온 findItem은 영속 상태이다.
        // @Transactional에 의해 트랜잭션 커밋이 된다.
        // JPA는 flush를 한다. 영속성 엔티티 값들 중에서 변경 사항을 모두 찾는다.
        // 업데이트 쿼리를 보낸다.

        // 이게 머지보다 더 나은 방법.
    }
    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }
}
