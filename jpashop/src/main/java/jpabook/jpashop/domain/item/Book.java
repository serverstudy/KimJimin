package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B") // Single table이니까 저장해 둘 때 디비 입장에서 구분을 위해 필요한.
@Getter @Setter
public class Book extends Item {
    private String author;
    private String isbn;
}
