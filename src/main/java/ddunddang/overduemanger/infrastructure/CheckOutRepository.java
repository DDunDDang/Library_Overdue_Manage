package ddunddang.overduemanger.infrastructure;

import ddunddang.overduemanger.domain.CheckOut;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckOutRepository extends JpaRepository<CheckOut, Long> {
    CheckOut findByUsersUserIdAndBookBookId(String userId, String bookId);
}
