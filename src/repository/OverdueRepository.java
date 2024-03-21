package repository;

import classes.Book;
import classes.CheckOut;
import classes.User;
import output.OverdueBook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OverdueRepository {
    Map<String, User> userMap = new ConcurrentHashMap<>();
    Map<String, Book> bookList = new ConcurrentHashMap<>();
    Map<String, OverdueBook> overdueBookList = new ConcurrentHashMap<>();

    public List<OverdueBook> getOverdueBookList() {
        List<OverdueBook> overdueBooks = new ArrayList<>();

        userMap.keySet().forEach(key -> {
            User user = userMap.get(key);
            HashMap<Book, CheckOut> checkOutHashMap = user.getCheckOutHashMap();

            checkOutHashMap.keySet().forEach(book -> {
                OverdueBook overdueBook;
                CheckOut checkOut = checkOutHashMap.get(book);
                String bookId = book.getId();
                if (overdueBookList.containsKey(bookId)) {
                    overdueBook = overdueBookList.get(bookId);
                } else {
                    overdueBook = new OverdueBook(bookId, book.getName(), checkOut);
                }

                checkOutHashMap.get(book).getManagementList().forEach(management -> {
                    int month = management.getManageDate().getMonthValue();
                    String means = management.getManagement().getMean();

                    switch (means) {
                        case "문자":
                            int[] message = overdueBook.getMessage();
                            message[month] += 1;
                            break;
                        case "전화":
                            int[] call = overdueBook.getCall();
                            call[month] += 1;
                            break;
                        case "기타":
                            int[] etc = overdueBook.getEtc();
                            etc[month] += 1;
                            break;
                    }
                });

                overdueBookList.put(bookId, overdueBook);
            });
        });

        overdueBookList.keySet().forEach(key -> {
            overdueBooks.add(overdueBookList.get(key));
        });

        return overdueBooks;
    }

    public List<OverdueBook> getReturnedBook(List<Book> books) {
        List<OverdueBook> overdueBooks = new ArrayList<>();

        books.stream().map(book -> book.getId()).forEach(key -> {
            overdueBooks.add(overdueBookList.get(key));
        });

        return overdueBooks;
    }

    public List<User> getUserList() {
        return userMap.keySet().stream().map(id -> userMap.get(id)).toList();
    }

    public User addUser(String[] array) {
        String id = array[1];
        return userMap.computeIfAbsent(id, k -> {
            String name = array[0];
            String phoneNumber = array[2];
            return new User(id, name, phoneNumber);
        });
    }

    public Book addBook(String[] array) {
        String bookId = array[3];
        String bookName = array[4];

        return bookList.computeIfAbsent(bookId, k -> new Book(bookId, bookName));
    }

    public User getUser(String id) {
        return userMap.get(id);
    }

    public int getOverdueUserCount() {
        return (int) userMap.values().stream()
                .filter(this::hasOverdueCheckout)
                .count();
    }

    private boolean hasOverdueCheckout(User user) {
        return user.getCheckOutHashMap().values().stream()
                .anyMatch(checkOut -> checkOut.getCheckOutStatus() != CheckOut.CheckOutStatus.END);
    }

    public int getNewOverdueUserCount() {
        return (int) userMap.values().stream()
                .filter(this::hasNewCheckout)
                .count();
    }

    private boolean hasNewCheckout(User user) {
        return user.getCheckOutHashMap().values().stream()
                .anyMatch(checkOut -> checkOut.getCheckOutStatus() == CheckOut.CheckOutStatus.NEW);
    }

    public int getLongTermOverdueUserCount() {
        return (int) userMap.values().stream()
                .filter(this::hasLongTermCheckout)
                .count();
    }

    private boolean hasLongTermCheckout(User user) {
        return user.getCheckOutHashMap().values().stream()
                .anyMatch(checkOut -> checkOut.getCheckOutStatus() == CheckOut.CheckOutStatus.LONG_TERM);
    }

    public int getEndUserCount() {
        return (int) userMap.values().stream()
                .filter(this::hasEndCheckout)
                .count();
    }

    private boolean hasEndCheckout(User user) {
        return user.getCheckOutHashMap().values().stream()
                .allMatch(checkOut -> checkOut.getCheckOutStatus() == CheckOut.CheckOutStatus.END);
    }
}
