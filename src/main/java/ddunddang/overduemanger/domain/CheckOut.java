package ddunddang.overduemanger.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class CheckOut {

    public CheckOut() {
    }

    public CheckOut(Book book, LocalDateTime checkOutDate, LocalDateTime dueDate) {
        this.book = book;
        this.checkOutDate = checkOutDate;
        this.dueDate = dueDate;

        long overdueTerm = dueDate.toLocalDate().until(LocalDate.now(), ChronoUnit.DAYS);
        this.status = overdueTerm < 90 ? Status.NEW : Status.LONG_TERM;
        }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
    @OneToMany(mappedBy = "checkOut")
    private List<Manage> manageList = new ArrayList<>();
    private Status status;
    private LocalDateTime checkOutDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
        this.status = Status.END;
    }

    @Getter
    public enum Status {
        // 신규, 장기, 관리중단
        NEW("신규"),
        LONG_TERM("장기"),
        END("관리 중단");

        private String status;

        Status(String status) {
            this.status = status;
        }
    }
}
