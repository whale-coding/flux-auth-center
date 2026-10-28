package com.flux.common.excel.util;

import com.alibaba.excel.EasyExcel;
import com.flux.common.core.result.ResultCode;
import com.flux.common.excel.listener.ExcelReadListener;
import com.flux.common.excel.model.ExcelResult;
import com.flux.common.web.exception.BizException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @ClassName ExcelUtils
 * @Author njq
 * @Date 2026/10/28
 * @Description EasyExcel 导入导出工具。导出的实体用 @ExcelProperty 标表头，
 * 只想导出部分字段时在类上加 @ExcelIgnoreUnannotated
 */
public final class ExcelUtils {

    private static final String DEFAULT_SHEET_NAME = "Sheet1";
    private static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private ExcelUtils() {
    }

    // ==================== 导出 ====================

    /**
     * 导出到浏览器，默认 sheet 名
     */
    public static <T> void export(HttpServletResponse response, String fileName, Class<T> head, List<T> data) {
        export(response, fileName, DEFAULT_SHEET_NAME, head, data);
    }

    /**
     * 导出到浏览器。fileName 不用自己编码，这里统一按 RFC 5987 处理中文
     */
    public static <T> void export(HttpServletResponse response, String fileName, String sheetName,
                                  Class<T> head, List<T> data) {
        try {
            response.setContentType(XLSX_CONTENT_TYPE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=utf-8''" + encoded + ".xlsx");
            export(response.getOutputStream(), sheetName, head, data);
        } catch (IOException e) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR, "导出 Excel 失败", e);
        }
    }

    /**
     * 导出到输出流。输出流由调用方负责关闭
     */
    public static <T> void export(OutputStream out, String sheetName, Class<T> head, List<T> data) {
        // autoCloseStream(false)：流交给调用方关，EasyExcel 提前关掉会把响应截断
        EasyExcel.write(out, head)
                .autoCloseStream(Boolean.FALSE)
                .sheet(sheetName)
                .doWrite(data == null ? List.of() : data);
    }

    /**
     * 导出到文件，文件不存在会自动创建
     */
    public static <T> void export(File file, String sheetName, Class<T> head, List<T> data) {
        EasyExcel.write(file, head)
                .sheet(sheetName)
                .doWrite(data == null ? List.of() : data);
    }

    // ==================== 导入 ====================

    /**
     * 读取上传的文件，第一个 sheet
     */
    public static <T> ExcelResult<T> read(MultipartFile file, Class<T> head) {
        if (file == null || file.isEmpty()) {
            throw new BizException(ResultCode.USER_UPLOAD_FILE_ERROR, "上传文件不能为空");
        }
        try (InputStream in = file.getInputStream()) {
            return read(in, head);
        } catch (IOException e) {
            throw new BizException(ResultCode.USER_UPLOAD_FILE_ERROR, "读取上传文件失败", e);
        }
    }

    public static <T> ExcelResult<T> read(File file, Class<T> head) {
        if (file == null || !file.exists()) {
            throw new BizException(ResultCode.USER_UPLOAD_FILE_ERROR, "文件不存在");
        }
        ExcelReadListener<T> listener = new ExcelReadListener<>();
        EasyExcel.read(file, head, listener).sheet().doRead();
        return listener.getResult();
    }

    /**
     * 从输入流读取，流由调用方负责关闭
     */
    public static <T> ExcelResult<T> read(InputStream in, Class<T> head) {
        ExcelReadListener<T> listener = new ExcelReadListener<>();
        EasyExcel.read(in, head, listener).sheet().doRead();
        return listener.getResult();
    }
}
