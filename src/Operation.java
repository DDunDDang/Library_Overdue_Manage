import output.ManagementSum;
import output.OverdueBook;
import repository.OverdueRepository;
import service.OverDueService;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;


public class Operation extends JFrame {


    private final OverdueRepository overdueRepository = new OverdueRepository();
    private final OverDueService overDueService = new OverDueService(overdueRepository);

    JFileChooser fileChooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV", "csv");

    private JTextArea ta = new JTextArea(10, 30);
    private JTextArea ta2 = new JTextArea(10, 30);
    private JTextField tfName = new JTextField(30);



    private ManagementSum manageNum = new ManagementSum();
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
        sp.setSize(850, 250);
        c.add(sp);

        JPanel findPanel = new JPanel();
        findPanel.setLayout(new GridLayout(1, 2));

        findPanel.add(tfName);

        JButton findButton = new JButton("대출자 번호로 찾기");
        findPanel.add(findButton);

        findPanel.setLocation(10, 500);
        findPanel.setSize(430, 50);

        c.add(findPanel);

        JScrollPane sp2 = new JScrollPane(ta2);
        sp2.setLocation(10, 570);
        sp2.setSize(850, 250);
        c.add(sp2);

        setSize(900, 900);
        setVisible(true);

        addFileChooserListener(overdueButton, overdueLabel, filePath, 0);
        addFileChooserListener(managementListButton, managementListLabel, filePath, 1);
        addFileChooserListener(endListButton, endListLabel, filePath, 2);

        addStartButtonListener(startButton);

//        addFindUserListener(findButton);
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
            String text1 = start();
            String text2 = output2();

            ta.setText(text1);
            ta2.setText(text2);
        });
    }

//    private void addFindUserListener(JButton button) {
//        button.addActionListener(e -> {
//            ta2.setText("");
//
//            String id = tfName.getText();
//
//            if (id.equals("")) {
//                ta2.append("대출자 번호를 입력해주세요.");
//            } else if (userMap.containsKey(id)) {
//                User user = userMap.get(id);
//                List<Book> overdueBooks = user.getOverdueBooks();
//                String bookList = overdueBooks.stream()
//                        .map(Book::toString)
//                        .collect(Collectors.joining("\n"));
//
//                ta2.append(
//                        "대출자 번호: " + user.getId() + " 이름: " + user.getName() + "\n"
//                                + "연체된 도서: \n"
//                                + bookList
//                );
//            } else {
//                ta2.append(id + "에 대한 정보는 없습니다.");
//            }
//
//            tfName.setText("");
//        });
//    }


    public static void main(String[] args) {
        new Operation();
    }

    public String start() {
        overDueService.insertDataFirst(filePath);
        overDueService.insertDataThird(filePath);
        overDueService.insertDataSecond(filePath, manageNum);

        return output1();
    }

    private String output1() {
        int overdueUserCount = overDueService.getOverdueUserCount();
        int newCount = overDueService.getNewOverdueUserCount();
        int longTermCount = overDueService.getLongTermOverdueUserCount();
        int endCount = overDueService.getEndUserCount();

        List<OverdueBook> overdueBooks = overDueService.getOverdueBooks();
        String bookList = overdueBooks.stream()
                .map(OverdueBook::toString)
                .collect(Collectors.joining("\n"));

        StringBuilder sb = new StringBuilder();
        sb.append("관리 횟수: ").append(manageNum.getManageNum()).append("\n").append("\n")
                .append("  문자: ").append(manageNum.getMassageNum()).append("\n").append("  ");

        for (int i = 1; i <= 12; i++) {
            sb.append(i).append("월: ").append(manageNum.getNumberOfMonthlyMessageManagement()[i]).append(" | ");
        }
        sb.append("\n");

        sb.append("  전화: ").append(manageNum.getCallNum()).append("\n").append("  ");

        for (int i = 1; i <= 12; i++) {
            sb.append(i).append("월: ").append(manageNum.getNumberOfMonthlyCallManagement()[i]).append(" | ");
        }
        sb.append("\n");

        sb.append("  기타: ").append(manageNum.getEtcNum()).append("\n").append("  ");

        for (int i = 1; i <= 12; i++) {
            sb.append(i).append("월: ").append(manageNum.getNumberOfMonthlyETCManagement()[i]).append(" | ");
        }
        sb.append("\n");

        String result = sb.toString().trim();

        return
                result + "\n" + "\n"
                + "연체자: " + overdueUserCount + "\n"
                + "     신규: " + newCount + ", 장기: " + longTermCount + ", 관리중단: " + endCount + "\n" + "\n"
                + "연체도서: " + overdueBooks.size() + "\n" + "\n"
                + bookList;
    }

    private String output2() {
        List<OverdueBook> returnedBooks = overDueService.getReturnedBooks();

        int[] manage = new int[13];
        int[] message = new int[13];
        int[] call = new int[13];
        int[] etc = new int[13];
        long avgDate = avg(returnedBooks);



        returnedBooks.forEach(overdueBook -> {
            count(manage, message, overdueBook.getMessage());
            count(manage, call, overdueBook.getCall());
            count(manage, etc, overdueBook.getEtc());
        });

        StringBuilder sb = new StringBuilder();
        sb.append("관리 횟수: ").append(sum(manage)).append("\n").append("\n")
                .append("  문자: ").append(sum(message)).append("\n").append("  ");

        for (int i = 1; i <= 12; i++) {
            sb.append(i).append("월: ").append(message[i]).append(" | ");
        }
        sb.append("\n");

        sb.append("  전화: ").append(sum(call)).append("\n").append("  ");

        for (int i = 1; i <= 12; i++) {
            sb.append(i).append("월: ").append(call[i]).append(" | ");
        }
        sb.append("\n");

        sb.append("  기타: ").append(sum(etc)).append("\n").append("  ");

        for (int i = 1; i <= 12; i++) {
            sb.append(i).append("월: ").append(etc[i]).append(" | ");
        }
        sb.append("\n");

        String result = sb.toString().trim();

        String bookList = returnedBooks.stream()
                .map(OverdueBook::toString)
                .collect(Collectors.joining("\n"));
        return "반납된 도서 통계" + "\n" + "\n"
                + "반납된 도서 수: " + returnedBooks.size() + "\n" + "\n"
                + "평균 연체 일 수: " + avgDate + "\n" + "\n"
                + result + "\n" + "\n"
                + bookList;
    }

    private void count(int[] manage, int[] arr, int[] insert) {
        for (int i = 1; i < arr.length; i++) {
            manage[i] += insert[i];
            arr[i] += insert[i];
        }
    }

    private int sum(int[] arr) {
        int result = 0;
        for (int i = 1; i < arr.length; i++) {
            result += arr[i];
        }

        return result;
    }

    private long avg(List<OverdueBook> books) {
        return books.stream().mapToLong(book -> book.getOverDueCount()).sum() / books.size();
    }
}