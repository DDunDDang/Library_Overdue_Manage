package ddunddang.overduemanger.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
public class Manage {

    public Manage() {
    }

    public Manage(CheckOut checkOut, LocalDateTime manageDate, String typeStr) {
        this.checkOut = checkOut;
        this.manageDate = manageDate;
        Type type = null;

        switch (typeStr) {
            case "문자" :
                type = Type.MASSAGE;
                break;
            case "전화" :
                type = Type.CALL;
                break;
            case "기타" :
                type = Type.ETC;
                break;
        }

        this.type = type;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long manageId;
    @ManyToOne
    @JoinColumn(name = "checkout_id")
    private CheckOut checkOut;
    private LocalDateTime manageDate;
    @Getter
    private Type type;

    @Getter
    public enum Type {
        // 문자, 전화, 기타
        MASSAGE("문자"),
        CALL("전화"),
        ETC("기타");
        private String type;

        Type(String type) {
            this.type = type;
        }
    }
}
