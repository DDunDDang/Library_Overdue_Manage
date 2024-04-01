package ddunddang.overduemanger.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Book {

    public Book() {
    }

    public Book(String bookId, String bookName) {
        this.bookId = bookId;
        this.bookName = bookName;
    }

    @Getter
    @Setter
    @Id
    private String bookId;
    @Getter
    private String bookName;
}
