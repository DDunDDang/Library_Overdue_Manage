package ddunddang.overduemanger.infrastructure;

import ddunddang.overduemanger.domain.Manage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManageRepository extends JpaRepository<Manage, Long> {
}
