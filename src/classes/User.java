package classes;

import java.util.HashMap;
import java.util.List;

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

    public List<Book> getOverdueBooks() {
        List<Book> overdueBooks = checkOutHashMap.keySet().stream()
                .filter(book -> checkOutHashMap.get(book).getCheckOutStatus() != CheckOut.CheckOutStatus.END)
                .toList();
        return overdueBooks;
    }
}
