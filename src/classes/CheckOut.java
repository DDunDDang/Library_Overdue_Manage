package classes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CheckOut {
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private LocalDateTime endDate;
    private int lateDate;
    private int baseMonth;
    private CheckOutStatus checkOutStatus = CheckOutStatus.NEW;
    private List<Management> managementList = new ArrayList<>();

    public CheckOut(LocalDateTime startDate, LocalDateTime dueDate, int lateDate, int baseMonth) {
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.lateDate = lateDate;
        this.baseMonth = baseMonth;
    }

    public enum CheckOutStatus {
        // 신규, 장기, 관리중단
        NEW("신규"),
        LONG_TERM("장기"),
        END("관리 중단");

        private String status;

        CheckOutStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setBaseMonth(int baseMonth) {
        this.baseMonth = baseMonth;
    }

    public CheckOutStatus getCheckOutStatus() {
        return checkOutStatus;
    }

    public Optional<LocalDateTime> getEndDate() {
        return Optional.ofNullable(this.endDate);
    }

    public void setCheckOutStatus(CheckOutStatus checkOutStatus) {
        this.checkOutStatus = checkOutStatus;
    }

    public void addManagement(Management management) {
        this.managementList.add((management));
    }
}
