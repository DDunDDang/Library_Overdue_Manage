package ddunddang.overduemanger.application;

import lombok.RequiredArgsConstructor;
import ddunddang.overduemanger.domain.Book;
import ddunddang.overduemanger.domain.CheckOut;
import ddunddang.overduemanger.domain.Manage;
import ddunddang.overduemanger.domain.Users;
import ddunddang.overduemanger.infrastructure.BookRepository;
import ddunddang.overduemanger.infrastructure.CheckOutRepository;
import ddunddang.overduemanger.infrastructure.ManageRepository;
import ddunddang.overduemanger.infrastructure.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OverdueManagerService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CheckOutRepository checkOutRepository;
    private final ManageRepository manageRepository;


    public void insertFirstSCV(MultipartFile file) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        if(br.readLine() != null) {
            while ((line = br.readLine()) != null) {
                String[] datalines = line.split(",");
                try {
                    String userName = datalines[0];
                    String userId = datalines[1];
                    String bookId = datalines[3];
                    String bookName = datalines[4];
                    LocalDateTime checkOutDate  = LocalDateTime.parse(datalines[5], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    LocalDateTime dueDate = LocalDateTime.parse(datalines[6], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));


                    Users users = createUser(userId, userName);
                    Book book = createBook(bookId, bookName);

                    createCheckOut(users, book, checkOutDate, dueDate);
                } catch (NumberFormatException e) {
                    continue;  // 첫번째 줄(제목 행) 제외하기 위함
                }
            }
            br.close();
        }
    }

    public void insertSecondSCV(MultipartFile file) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        if(br.readLine() != null) {
            while ((line = br.readLine()) != null) {
                String[] datalines = line.split(",");
                try {
                    String userId = datalines[1];
                    String bookId = datalines[2];
                    LocalDateTime returnDate = LocalDateTime.parse(datalines[4], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                    insetReturnDate(userId, bookId, returnDate);
                } catch (NumberFormatException e) {
                    continue;  // 첫번째 줄(제목 행) 제외하기 위함
                }
            }
            br.close();
        }
    }

    public void insertThirdCSV(MultipartFile file) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        if(br.readLine() != null) {
            while ((line = br.readLine()) != null) {
                String[] datalines = line.split(",");
                try {
                    String type = datalines[0];
                    String userId = datalines[1];
                    LocalDateTime manageDate = LocalDateTime.parse(datalines[2] + " 12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                    createManage(userId, type, manageDate);
                } catch (NumberFormatException e) {
                    continue;  // 첫번째 줄(제목 행) 제외하기 위함
                }
            }
            br.close();
        }
    }

    private Users createUser(String userId, String userName) {
        Optional<Users> user = userRepository.findById(userId);

        return user.orElseGet(() -> userRepository.save(new Users(userId, userName)));
    }

    private Book createBook(String bookId, String bookName) {
        Optional<Book> book = bookRepository.findById(bookId);

        return book.orElseGet(() -> bookRepository.save(new Book(bookId, bookName)));
    }

    private void createCheckOut(Users users, Book book, LocalDateTime checkOutDate, LocalDateTime dueDate) {
        CheckOut checkOut = new CheckOut(book, checkOutDate, dueDate);
        users.addCheckOut(checkOut);

        checkOutRepository.save(checkOut);
    }

    private void createManage(String userId, String type, LocalDateTime manageDate) {
        Optional<Users> optionalUser = userRepository.findById(userId);
        Users user = optionalUser.orElseThrow(() -> new RuntimeException(userId + "없는 회원"));
        user.getCheckOutList().stream()
                .filter(checkOut -> checkOut.getReturnDate() == null || checkOut.getReturnDate().isAfter(manageDate))
                .forEach(checkOut -> manageRepository.save(new Manage(checkOut, manageDate, type)));
    }

    private void insetReturnDate(String userId, String bookId, LocalDateTime returnDate) {
        CheckOut checkOut = checkOutRepository.findByUsersUserIdAndBookBookId(userId, bookId);
        checkOut.setReturnDate(returnDate);

        checkOutRepository.save(checkOut);
    }
}
