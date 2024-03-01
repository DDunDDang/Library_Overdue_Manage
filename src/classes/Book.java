package classes;

public class Book {
    private String id;
    private String name;

    public Book(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }
    public String toString() {
        return "    등록번호: " + id + " , 서명: " + name;
    }
}
