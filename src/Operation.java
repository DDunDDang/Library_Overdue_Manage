import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Operation extends JFrame {
    JFileChooser fileChooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV", "csv");
    private JTextArea ta = new JTextArea(10, 30);
    private HashMap<String, User> userMap = new HashMap<>();
    private int manageNum = 0;
    private int massageNum = 0;
    private int callNum = 0;
    private int etcNum = 0;
    private static final String[] filePath = new String[3];
    public Operation() {
        setTitle("연체자 통계");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = getContentPane();
        c.setLayout(null);

        JPanel buttonPanel_1 = new JPanel();
        buttonPanel_1.setLayout(new GridLayout(3, 2));

        JButton overdueButton = new JButton("연체도서목록 파일 열기");
        buttonPanel_1.add(overdueButton);

        JLabel overdueLabel = new JLabel("파일 없음");
        buttonPanel_1.add(overdueLabel);

        JButton managementListButton = new JButton("관리목록 파일 열기");
        buttonPanel_1.add(managementListButton);

        JLabel managementListLabel = new JLabel("파일 없음");
        buttonPanel_1.add(managementListLabel);

        JButton endListButton = new JButton("반납목록 파일 열기");
        buttonPanel_1.add(endListButton);

        JLabel endListLabel = new JLabel("파일 없음");
        buttonPanel_1.add(endListLabel);

        buttonPanel_1.setLocation(10, 20);
        buttonPanel_1.setSize(430, 100);
        c.add(buttonPanel_1);

        JPanel buttonPanel_2 = new JPanel();
        buttonPanel_2.setLayout(new GridLayout(1, 1));

        JButton startButton = new JButton("시작");
        buttonPanel_2.add(startButton);

        buttonPanel_2.setLocation(10, 140);
        buttonPanel_2.setSize(430, 50);
        c.add(buttonPanel_2);

        JScrollPane sp = new JScrollPane(ta);
        sp.setLocation(10, 220);
        sp.setSize(430, 250);
        c.add(sp);

        setSize(450, 600);
        setVisible(true);

        addFileChooserListener(overdueButton, overdueLabel, filePath, 0);
        addFileChooserListener(managementListButton, managementListLabel, filePath, 1);
        addFileChooserListener(endListButton, endListLabel, filePath, 2);

        addStartButtonListener(startButton);
    }

    private void addFileChooserListener(JButton button, JLabel label, String[] filePath, int idx) {
        button.addActionListener(e -> {
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(getParent());

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getPath();
                label.setText(path);
                filePath[idx] = path;
            }
        });
    }

    private void addStartButtonListener(JButton button) {
        button.addActionListener(e -> {
            String text = start();

            ta.setText(text);
        });
    }


    public static void main(String[] args) {
        new Operation();
    }

    public String start() {
        insertDataFirst();
        insertDataSecond();
        insertDataThird();
        return output();
    }

    private void insertDataFirst() {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath[0]), Charset.forName("UTF-8"))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] array = line.split(",");
                if (array.length < 9) {
                    continue;
                }
                User user = createUser(array);
                Book book = createBook(array);
                CheckOut checkOut = createCheckOut(array);
                user.insertCheckOutInfo(book, checkOut);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertDataSecond() {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath[1]), Charset.forName("UTF-8"))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] array = line.split(",");
                if (array.length < 4) {
                    continue;
                }
                String means = array[0];
                String id = array[1];
                int baseMonth = Integer.parseInt(array[3]);
                User user = userMap.get(id);
                if (user != null) {
                    updateUserCheckOutInfo(user, means, baseMonth);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertDataThird() {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath[2]), Charset.forName("UTF-8"))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] array = line.split(",");
                if (array.length < 5) {
                    continue;
                }
                String id = array[1];
                String bookId = array[2];
                LocalDateTime endDate = LocalDateTime.parse(array[4], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                User user = userMap.get(id);
                if (user != null) {
                    updateUserCheckOutStatus(user, bookId, endDate);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String output() {
        int overdueUserCount = (int) userMap.values().stream()
                .filter(this::hasOverdueCheckout)
                .count();

        int newCount = (int) userMap.values().stream()
                .filter(this::hasNewCheckout)
                .count();

        int longTermCount = (int) userMap.values().stream()
                .filter(this::hasLongTermCheckout)
                .count();

        int endCount = (int) userMap.values().stream()
                .filter(this::hasEndCheckout)
                .count();

        int overdueBookCount = countOverdueBooks();

        return
                "관리 횟수: " + manageNum + "\n"
                + "     문자: " + massageNum + ", 전화: " + callNum + ", 기타: " + etcNum + "\n"
                + "연체자: " + overdueUserCount + "\n"
                + "     신규: " + newCount + ", 장기: " + longTermCount + ", 관리중단: " + endCount + "\n"
                + "연체도서: " + overdueBookCount;
    }

    private boolean hasNewCheckout(User user) {
        return user.getCheckOutHashMap().values().stream()
                .anyMatch(checkOut -> checkOut.getCheckOutStatus() == CheckOut.CheckOutStatus.NEW);
    }

    private boolean hasLongTermCheckout(User user) {
        return user.getCheckOutHashMap().values().stream()
                .anyMatch(checkOut -> checkOut.getCheckOutStatus() == CheckOut.CheckOutStatus.LONG_TERM);
    }

    private boolean hasEndCheckout(User user) {
        return user.getCheckOutHashMap().values().stream()
                .allMatch(checkOut -> checkOut.getCheckOutStatus() == CheckOut.CheckOutStatus.END);
    }

    private boolean hasOverdueCheckout(User user) {
        return user.getCheckOutHashMap().values().stream()
                .anyMatch(checkOut -> checkOut.getCheckOutStatus() != CheckOut.CheckOutStatus.END);
    }

    private int countOverdueBooks() {
        return (int) userMap.values().stream()
                .flatMap(user -> user.getCheckOutHashMap().values().stream())
                .filter(checkOut -> checkOut.getCheckOutStatus() != CheckOut.CheckOutStatus.END)
                .count();
    }

    private User createUser(String[] array) {
        String id = array[1];
        return userMap.computeIfAbsent(id, k -> {
            String name = array[0];
            String phoneNumber = array[2];
            return new User(id, name, phoneNumber);
        });
    }

    private Book createBook(String[] array) {
        String bookId = array[3];
        String bookName = array[4];
        return new Book(bookId, bookName);
    }

    private CheckOut createCheckOut(String[] array) {
        LocalDateTime startDate = LocalDateTime.parse(array[5], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime dueDate = LocalDateTime.parse(array[6], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        int lateDate = Integer.parseInt(array[7]);
        int baseMonth = Integer.parseInt(array[8]);
        return new CheckOut(startDate, dueDate, lateDate, baseMonth);
    }

    private void updateUserCheckOutInfo(User user, String means, int baseMonth) {
        user.getCheckOutHashMap().values().forEach(checkOut -> {
            checkOut.setBaseMonth(baseMonth);
            switch (means) {
                case "문자":
                    checkOut.setManagement(CheckOut.Management.MASSAGE);
                    massageNum++;
                    break;
                case "전화":
                    checkOut.setManagement(CheckOut.Management.CALL);
                    callNum++;
                    break;
                case "기타":
                    checkOut.setManagement(CheckOut.Management.ETC);
                    etcNum++;
                    break;
                default:
                    break;
            }
            manageNum++;
        });
    }

    private void updateUserCheckOutStatus(User user, String bookId, LocalDateTime endDate) {
        user.getCheckOutHashMap().entrySet().stream()
                .filter(entry -> entry.getKey().getId().equals(bookId))
                .map(entry -> entry.getValue())
                .forEach(checkOut -> {
                    checkOut.setEndDate(endDate);
                    checkOut.setCheckOutStatus(CheckOut.CheckOutStatus.END);
                });
    }
}

class Book {
    private String id;
    private String name;

    public Book(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }
}

class CheckOut {
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

class User {
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