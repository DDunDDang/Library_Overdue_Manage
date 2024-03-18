package service;

import classes.Book;
import classes.CheckOut;
import classes.Management;
import classes.User;
import output.ManagementSum;
import output.OverdueBook;
import repository.OverdueRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OverDueService {
    private OverdueRepository overdueRepository;

    public OverDueService(OverdueRepository overdueRepository) {
        this.overdueRepository = overdueRepository;
    }

    public void insertDataFirst(String[] filePath) {
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

    public void insertDataSecond(String[] filePath, ManagementSum manageNum) {
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
                // todo: 관리 날짜가 LocalDate 가 아닌 LocalDateTime 형태로 뽑을 수 있는지 확인
                LocalDateTime mangeDate = LocalDateTime.parse(array[2] + " 12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                int baseMonth = Integer.parseInt(array[3]);
                User user = overdueRepository.getUser(id);
                if (user != null) {
                    updateUserCheckOutInfo(user, means, baseMonth, mangeDate, manageNum);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insertDataThird(String[] filePath) {
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
                User user = overdueRepository.getUser(id);
                if (user != null) {
                    updateUserCheckOutStatus(user, bookId, endDate);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getOverdueUserCount() {
        return overdueRepository.getOverdueUserCount();
    }

    public int getNewOverdueUserCount() {
        return overdueRepository.getNewOverdueUserCount();
    }

    public int getLongTermOverdueUserCount() {
        return overdueRepository.getLongTermOverdueUserCount();
    }

    public int getEndUserCount() {
        return overdueRepository.getEndUserCount();
    }

    public List<OverdueBook> getOverdueBooks() {
        return overdueRepository.getOverdueBookList();
    }







    private User createUser(String[] array) {
        return overdueRepository.addUser(array);
    }

    private Book createBook(String[] array) {
        return overdueRepository.addBook(array);
    }

    private CheckOut createCheckOut(String[] array) {
        LocalDateTime startDate = LocalDateTime.parse(array[5], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime dueDate = LocalDateTime.parse(array[6], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        int lateDate = Integer.parseInt(array[7]);
        int baseMonth = Integer.parseInt(array[8]);
        return new CheckOut(startDate, dueDate, lateDate, baseMonth);
    }

    /*
    * 관리가 되는 기준
    * 1. 관리 당시에 반납 속성이 비어있어야 한다. (null)
    * 2. 반납 처리가 되어 있더라도 관리일 이후라면 관리가 되어야 한다.
    */

    private void updateUserCheckOutInfo(User user, String mean, int baseMonth, LocalDateTime manageDate, ManagementSum manageNum) {
        user.getCheckOutHashMap().values().stream()
                .filter(checkOut -> checkOut.getEndDate().isEmpty() ||
                        checkOut.getEndDate().get().isAfter(manageDate))
                .forEach(checkOut -> {
                    checkOut.setBaseMonth(baseMonth);
                    switch (mean) {
                        case "문자":
                            checkOut.addManagement(new Management(manageDate, Management.ManagementMean.MASSAGE));
                            manageNum.increaseMassageNum(baseMonth);
                            break;
                        case "전화":
                            checkOut.addManagement(new Management(manageDate, Management.ManagementMean.CALL));
                            manageNum.increaseCallNum(baseMonth);
                            break;
                        case "기타":
                            checkOut.addManagement(new Management(manageDate, Management.ManagementMean.ETC));
                            manageNum.increaseEtcNum(baseMonth);
                            break;
                        default:
                            break;
                    }
                    manageNum.increaseManageNum();
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
