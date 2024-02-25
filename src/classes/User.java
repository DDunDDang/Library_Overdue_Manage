package classes;

import java.util.HashMap;

public class User {
    private String id;
    private String name;
    private String phoneNumber;
    private HashMap<Book, CheckOut> checkOutHashMap = new HashMap<>();

    public User(String id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public HashMap<Book, CheckOut> getCheckOutHashMap() {
        return checkOutHashMap;
    }

    public void insertCheckOutInfo(Book book, CheckOut checkOut) {
        this.checkOutHashMap.put(book, checkOut);
    }
}
