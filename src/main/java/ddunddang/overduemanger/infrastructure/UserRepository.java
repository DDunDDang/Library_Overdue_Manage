package ddunddang.overduemanger.infrastructure;

import ddunddang.overduemanger.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, String> {
}
