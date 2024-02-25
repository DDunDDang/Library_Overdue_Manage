package classes;

import java.time.LocalDateTime;

public class CheckOut {
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private LocalDateTime endDate;
    private int lateDate;
    private int baseMonth;
    private CheckOutStatus checkOutStatus = CheckOutStatus.NEW;
    private Management management;

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

    public enum Management {
        // 문자, 전화, 기타
        MASSAGE("문자"),
        CALL("전화"),
        ETC("기타");
        private String means;

        Management(String means) {
            this.means = means;
        }

        public String getMeans() {
            return means;
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

    public void setCheckOutStatus(CheckOutStatus checkOutStatus) {
        this.checkOutStatus = checkOutStatus;
    }

    public void setManagement(Management management) {
        this.management = management;
    }
}
