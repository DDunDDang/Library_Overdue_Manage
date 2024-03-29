package output;

import classes.CheckOut;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class OverdueBook {
    private String id;
    private String name;
    private int[] message = new int[13];
    private int[] call = new int[13];
    private int[] etc = new int[13];
    private CheckOut checkOut;

    public OverdueBook(String id, String name, CheckOut checkOut) {
        this.id = id;
        this.name = name;
        this.checkOut = checkOut;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OverdueBook that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getMessage() {
        return message;
    }

    public void setMessage(int[] message) {
        this.message = message;
    }

    public int[] getCall() {
        return call;
    }

    public void setCall(int[] call) {
        this.call = call;
    }

    public int[] getEtc() {
        return etc;
    }

    public void setEtc(int[] etc) {
        this.etc = etc;
    }

    public CheckOut getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(CheckOut checkOut) {
        this.checkOut = checkOut;
    }

    public String toString() {
        long overdueCount = getOverDueCount();

        StringBuilder sb = new StringBuilder();
        sb.append("등록번호: ").append(this.id).append(" 서명: ").append(this.name).append(" (연체일: ").append(overdueCount).append("일)").append("\n")
            .append("  문자: ");
        for (int i = 1; i <= 12; i++) {
            sb.append(i).append("월: ").append(message[i]).append(" | ");
        }
        sb.append("\n");

        sb.append("  전화: ");
        for (int i = 1; i <= 12; i++) {
            sb.append(i).append("월: ").append(call[i]).append(" | ");
        }
        sb.append("\n");

        sb.append("  기타: ");
        for (int i = 1; i <= 12; i++) {
            sb.append(i).append("월: ").append(etc[i]).append(" | ");
        }
        sb.append("\n");

        return sb.toString();
    }

    public long getOverDueCount() {
        long overdueCount;

        if (checkOut.getCheckOutStatus() == CheckOut.CheckOutStatus.END) {
            overdueCount = checkOut.getDueDate().toLocalDate().until(checkOut.getEndDate().get().toLocalDate(), ChronoUnit.DAYS);
        } else {
            overdueCount = checkOut.getDueDate().toLocalDate().until(LocalDate.now(), ChronoUnit.DAYS);
        }
        return overdueCount;
    }
}
