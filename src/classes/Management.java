package classes;

import java.time.LocalDateTime;

public class Management {
    private LocalDateTime manageDate;
    private ManagementMean management;
    public enum ManagementMean {
        // 문자, 전화, 기타
        MASSAGE("문자"),
        CALL("전화"),
        ETC("기타");
        private String mean;

        ManagementMean(String mean) {
            this.mean = mean;
        }

        public String getMean() {
            return mean;
        }
    }

    public Management(LocalDateTime manageDate, ManagementMean management) {
        this.manageDate = manageDate;
        this.management = management;
    }
}
