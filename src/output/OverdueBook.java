package output;

import java.util.Objects;

public class OverdueBook {
    private String id;
    private String name;
    private int[] message = new int[13];
    private int[] call = new int[13];
    private int[] etc = new int[13];

    public OverdueBook(String id, String name) {
        this.id = id;
        this.name = name;
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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("등록번호: ").append(this.id).append(" 서명: ").append(this.name).append("\n")
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
}
