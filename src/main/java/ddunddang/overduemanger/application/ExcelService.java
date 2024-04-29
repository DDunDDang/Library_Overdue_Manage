package ddunddang.overduemanger.application;

import ddunddang.overduemanger.infrastructure.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ddunddang.overduemanger.domain.Book;
import ddunddang.overduemanger.domain.CheckOut;
import ddunddang.overduemanger.domain.Manage;
import ddunddang.overduemanger.domain.Users;
import ddunddang.overduemanger.infrastructure.CheckOutRepository;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private static final String[] FIRST_PAGE_HEADER = {"대출자 번호", "대출자 이름", "등록번호", "서명", "관리 횟수", "문자 횟수", "전화 횟수", "관리 구분"};
    private static final String[] SECOND_PAGE_HEADER = {"등록번호", "서명", "관리 횟수", "문자 횟수", "전화 횟수", "기타"};
    private static final String[] THIRD_PAGE_HEADER = {"대출자 번호", "대출자 이름", "관리 횟수", "문자 합계", "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월", "전화 합계", "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월", "기타"};
    private static final String[] TOTAL_HEADER = {"관리 총 횟수", "문자 총 횟수", "전화 총 횟수", "기타"};

    private final CheckOutRepository checkOutRepository;
    private final UserRepository userRepository;

    public Object getExcel(HttpServletResponse response) {
        List<CheckOut> checkOutList = checkOutRepository.findAll(Sort.by(Sort.Direction.ASC, "status"));
        List<Users> userList = userRepository.findAll();

        final String fileName = "연체자 통계 " + LocalDate.now().getYear();

        try (Workbook workbook = new XSSFWorkbook()) {
            createExcelPages(workbook, checkOutList, userList);

            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void createExcelPages(Workbook workbook, List<CheckOut> checkOutList, List<Users> userList) {
        createExcelFirstPage(workbook, checkOutList);
        createExcelSecondPage(workbook, checkOutList);
        createExcelThirdPage(workbook, userList);
    }

    private void createExcelFirstPage(Workbook workbook, List<CheckOut> checkOutList) {
        Sheet sheet = workbook.createSheet("연체자 목록 통계");

        CellStyle headerCellStyle = createHeaderCellStyle(workbook);
        CellStyle defaultCellStyle = createDefaultCellStyle(workbook);
        CellStyle numberCellStyle = createNumberCellStyle(workbook);


        createHeaderRow(sheet, FIRST_PAGE_HEADER, 0, headerCellStyle);

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

        CellStyle headerCellStyle = createHeaderCellStyle(workbook);
        CellStyle defaultCellStyle = createDefaultCellStyle(workbook);
        CellStyle numberCellStyle = createNumberCellStyle(workbook);

        createHeaderRow(sheet, TOTAL_HEADER, 0, headerCellStyle);
        createHeaderRow(sheet, SECOND_PAGE_HEADER, 3, headerCellStyle);


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

    private void createExcelThirdPage(Workbook workbook, List<Users> userList) {
        Sheet sheet = workbook.createSheet("연체 관리 이용자 목록");

        CellStyle headerCellStyle = createHeaderCellStyle(workbook);
        CellStyle defaultCellStyle = createDefaultCellStyle(workbook);
        CellStyle numberCellStyle = createNumberCellStyle(workbook);
        CellStyle totalNumberCellStyle = createTotalNumberCellStyle(workbook);

        createHeaderRow(sheet, THIRD_PAGE_HEADER, 1, headerCellStyle);
        Row row = sheet.createRow(0);
        for (int i = 0; i < 24; i++) {
            Cell cell = row.createCell(i + 3);
            cell.setCellStyle(defaultCellStyle);
            if (i == 0) cell.setCellValue("문자");
            if (i == 13) cell.setCellValue("전화");
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, 15));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 16, 28));


        for (int i = 0; i < userList.size(); i++) {
            Users user = userList.get(i);
            List<Manage> manageList = user.getCheckOutList().stream()
                    .flatMap(checkOut -> checkOut.getManageList().stream()).toList();

            int manageCount = manageList.size();
            AtomicInteger messageCount = new AtomicInteger(0);
            int[] monthMessage = new int[13];
            manageList.stream().filter(manage -> manage.getType().equals(Manage.Type.MASSAGE))
                    .forEach(manage -> {
                        int month = manage.getManageDate().getMonthValue();
                        monthMessage[month] += 1;
                        messageCount.getAndIncrement();
            });
            AtomicInteger callCount = new AtomicInteger(0);
            int[] monthCall = new int[13];
            manageList.stream().filter(manage -> manage.getType().equals(Manage.Type.CALL))
                    .forEach(manage -> {
                        int month = manage.getManageDate().getMonthValue();
                        monthCall[month] += 1;
                        callCount.getAndIncrement();
                    });
            int etcCount = manageCount - messageCount.get() - callCount.get();

            row = sheet.createRow(i + 2);
            createCell(row, 0, user.getUserId(), defaultCellStyle);
            createCell(row, 1, user.getUserName(), defaultCellStyle);
            createCell(row, 2, manageCount, numberCellStyle);
            createCell(row, 3, messageCount.get(), totalNumberCellStyle);
            for (int j = 1; j < monthMessage.length; j++) {
                createCell(row, j + 3, monthMessage[j], numberCellStyle);
            }
            createCell(row, 16, callCount.get(), totalNumberCellStyle);
            for (int j = 1; j < monthCall.length; j++) {
                createCell(row, j + 16, monthCall[j], numberCellStyle);
            }
            createCell(row, 29, etcCount, numberCellStyle);
        }
    }

    private void createHeaderRow(Sheet sheet, String[] headers, int rowIndex, CellStyle headerStyle) {
        Row row = sheet.createRow(rowIndex);

        for (int i = 0; i < headers.length; i++) {
            createCell(row, i, headers[i], headerStyle);
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

    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderRight(BorderStyle.MEDIUM);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        cellStyle.setBorderTop(BorderStyle.MEDIUM);
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
        cellStyle.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

        return cellStyle;
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

    private CellStyle createTotalNumberCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
        cellStyle.setFillBackgroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        return cellStyle;
    }
}