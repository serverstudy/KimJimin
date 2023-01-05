package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.awt.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model){
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form){
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        // setter 이렇게 쓰지 말고 생성메서드 만들어서 쓰는 게 더 좋은 설계

        itemService.saveItem(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model){
        List<Item> items = itemService.findItems();
        model.addAttribute("items",items);
        return "items/itemList";
    }

    @GetMapping("items/{itemId}/edit") // path variable.
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
        Book item = (Book) itemService.findOne(itemId); // 캐스팅하는 게 좋은 건 아니지만 예제를 단순화하기 위해

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form){
//        Book book = new Book();
//
//        book.setId(form.getId()); // 조심해야 함. 임의 수정이 가능할 수도 있어서.
        // 유저가 권한이 있는지 체크해주는 로직을 추가하거나
        // 객체를 세션에 담아 처리하거나
        // 등의 처리를 해줘야 한다.

        // 객체는 새 객체지만 새로운 book이 아니라 id가 세팅이 되어 있는 book.
        // JPA에 한 번 들어갔다가 나온 book.
        // 준영속 상태 객체
        // -> JPA가 관리하지 않는다.
        // JPA가 관리하는 영속성 엔티티는 변경 감지가 일어나는데, 이는 그렇지 않다.
        // 이런 준영속성 엔티티를 수정하는 방법 : 1. 변경 감지 사용 2. 병합 사용
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
//        itemService.saveItem(book);

        // 더 나은 설계는, 위의 주석 처리한 코드처럼 어설픈 객체를 새로 만들어 수행하는 것이 아니라 아래처럼.
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());
        // 파라미터로 전달하기엔 업데이트할 게 너무 많다 -> DTO 만들어 이용
        return "redirect:/items";
    }
}
