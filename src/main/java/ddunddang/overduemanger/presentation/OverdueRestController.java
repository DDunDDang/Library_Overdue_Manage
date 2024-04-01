package ddunddang.overduemanger.presentation;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ddunddang.overduemanger.application.ExcelService;
import ddunddang.overduemanger.application.OverdueManagerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class OverdueRestController {

    private final OverdueManagerService overdueManagerService;
    private final ExcelService excelService;

    @RequestMapping(value = "/overdue-first", method = RequestMethod.POST)
    public ResponseEntity<String> insertFirstCSV(@RequestPart MultipartFile file) throws IOException {
        overdueManagerService.insertFirstSCV(file);

        return new ResponseEntity<>("연체도서목록 파일 입력이 완료되었습니다.", HttpStatus.OK);
    }

    @RequestMapping(value = "/overdue-second", method = RequestMethod.POST)
    public ResponseEntity<String> insertSecondCSV(@RequestPart MultipartFile file) throws IOException {
        overdueManagerService.insertSecondSCV(file);

        return new ResponseEntity<>("반납목록 파일 입력이 완료되었습니다.", HttpStatus.OK);
    }

    @RequestMapping(value = "/overdue-third", method = RequestMethod.POST)
    public ResponseEntity<String> insertThirdCSV(@RequestPart MultipartFile file) throws IOException {
        overdueManagerService.insertThirdCSV(file);

        return new ResponseEntity<>("관리목록 파일 입력이 완료되었습니다.", HttpStatus.OK);
    }

    @RequestMapping(value = "/overdue/download", method = RequestMethod.GET)
    public ResponseEntity downloadExcel(HttpServletResponse response) {

        return ResponseEntity.ok(excelService.getExcel(response));
    }
}
