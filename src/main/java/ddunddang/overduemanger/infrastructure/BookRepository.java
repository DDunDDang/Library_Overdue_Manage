package ddunddang.overduemanger.infrastructure;

import ddunddang.overduemanger.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, String> {
}
