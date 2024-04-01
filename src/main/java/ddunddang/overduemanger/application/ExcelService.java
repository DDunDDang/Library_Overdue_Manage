package ddunddang.overduemanger.application;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ddunddang.overduemanger.domain.Book;
import ddunddang.overduemanger.domain.CheckOut;
import ddunddang.overduemanger.domain.Manage;
import ddunddang.overduemanger.domain.Users;
import ddunddang.overduemanger.infrastructure.CheckOutRepository;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private static final String[] FIRST_PAGE_HEADER = {"대출자 번호", "대출자 이름", "등록번호", "서명", "관리 횟수", "문자 횟수", "전화 횟수", "관리 구분"};
    private static final String[] SECOND_PAGE_HEADER = {"등록번호", "서명", "관리 횟수", "문자 횟수", "전화 횟수", "기타"};
    private static final String[] TOTAL_HEADER = {"관리 총 횟수", "문자 총 횟수", "전화 총 횟수", "기타"};

    private final CheckOutRepository checkOutRepository;

    public Object getExcel(HttpServletResponse response) {
        List<CheckOut> checkOutList = checkOutRepository.findAll(Sort.by(Sort.Direction.ASC, "status"));

        final String fileName = "연체자 통계 " + LocalDate.now().getYear();

        try (Workbook workbook = new XSSFWorkbook()) {
            createExcelPages(workbook, checkOutList);

            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void createExcelPages(Workbook workbook, List<CheckOut> checkOutList) {
        createExcelFirstPage(workbook, checkOutList);
        createExcelSecondPage(workbook, checkOutList);
    }

    private void createExcelFirstPage(Workbook workbook, List<CheckOut> checkOutList) {
        Sheet sheet = workbook.createSheet("연체자 목록 통계");
        createHeaderRow(sheet, FIRST_PAGE_HEADER, 0);

        CellStyle defaultCellStyle = createDefaultCellStyle(workbook);
        CellStyle numberCellStyle = createNumberCellStyle(workbook);

        for (int i = 0; i < checkOutList.size(); i++) {
            CheckOut checkOut = checkOutList.get(i);
            Users user = checkOut.getUsers();
            Book book = checkOut.getBook();
            List<Manage> manageList = checkOut.getManageList();

            Row row = sheet.createRow(i + 1);
            createCell(row, 0, user.getUserId(), defaultCellStyle);
            createCell(row, 1, user.getUserName(), defaultCellStyle);
            createCell(row, 2, book.getBookId(), defaultCellStyle);
            createCell(row, 3, book.getBookName(), defaultCellStyle);
            createCell(row, 4, manageList.size(), numberCellStyle);

            long messageCount = manageList.stream().filter(manage -> manage.getType() == Manage.Type.MASSAGE).count();
            createCell(row, 5, (int) messageCount, numberCellStyle);

            long callCount = manageList.stream().filter(manage -> manage.getType() == Manage.Type.CALL).count();
            createCell(row, 6, (int) callCount, numberCellStyle);

            createCell(row, 7, checkOut.getStatus().getStatus(), defaultCellStyle);
        }
    }

    private void createExcelSecondPage(Workbook workbook, List<CheckOut> checkOutList) {
        Sheet sheet = workbook.createSheet("연체 도서 목록");
        createHeaderRow(sheet, TOTAL_HEADER, 0);
        createHeaderRow(sheet, SECOND_PAGE_HEADER, 3);

        CellStyle defaultCellStyle = createDefaultCellStyle(workbook);
        CellStyle numberCellStyle = createNumberCellStyle(workbook);

        int totalManage = 0;
        int totalMessage = 0;
        int totalCall = 0;
        int totalEtc = 0;

        for (CheckOut checkOut : checkOutList) {
            List<Manage> manageList = checkOut.getManageList();
            totalManage += manageList.size();

            long messageCount = manageList.stream().filter(manage -> manage.getType() == Manage.Type.MASSAGE).count();
            totalMessage += messageCount;

            long callCount = manageList.stream().filter(manage -> manage.getType() == Manage.Type.CALL).count();
            totalCall += callCount;

            long etcCount = manageList.stream().filter(manage -> manage.getType() == Manage.Type.ETC).count();
            totalEtc += etcCount;
        }

        Row totalRow = sheet.createRow(1);
        createCell(totalRow, 0, totalManage, defaultCellStyle);
        createCell(totalRow, 1, totalMessage, defaultCellStyle);
        createCell(totalRow, 2, totalCall, defaultCellStyle);
        createCell(totalRow, 3, totalEtc, defaultCellStyle);

        for (int i = 0; i < checkOutList.size(); i++) {
            CheckOut checkOut = checkOutList.get(i);
            Book book = checkOut.getBook();
            List<Manage> manageList = checkOut.getManageList();

            Row row = sheet.createRow(i + 4);
            createCell(row, 0, book.getBookId(), defaultCellStyle);
            createCell(row, 1, book.getBookName(), defaultCellStyle);
            createCell(row, 2, manageList.size(), numberCellStyle);

            long messageCount = manageList.stream().filter(manage -> manage.getType() == Manage.Type.MASSAGE).count();
            createCell(row, 3, (int) messageCount, numberCellStyle);

            long callCount = manageList.stream().filter(manage -> manage.getType() == Manage.Type.CALL).count();
            createCell(row, 4, (int) callCount, numberCellStyle);

            long etcCount = manageList.stream().filter(manage -> manage.getType() == Manage.Type.ETC).count();
            createCell(row, 5, (int) etcCount, numberCellStyle);
        }
    }

    private void createHeaderRow(Sheet sheet, String[] headers, int rowIndex) {
        Row row = sheet.createRow(rowIndex);
        CellStyle defaultCellStyle = createDefaultCellStyle(sheet.getWorkbook());

        for (int i = 0; i < headers.length; i++) {
            createCell(row, i, headers[i], defaultCellStyle);
        }
    }

    private void createCell(Row row, int columnIndex, Object value, CellStyle cellStyle) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellStyle(cellStyle);

        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        }
    }

    private CellStyle createDefaultCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellStyle;
    }

    private CellStyle createNumberCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
        return cellStyle;
    }
}