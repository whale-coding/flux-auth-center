package com.flux.common.excel.util;

import com.alibaba.excel.annotation.ExcelProperty;
import com.flux.common.core.result.ResultCode;
import com.flux.common.excel.model.ExcelResult;
import com.flux.common.web.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Excel 导入导出单测，读写真实文件，不需要 Spring 上下文
 */
class ExcelUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("导出后再读回来，内容一致且没有错误行")
    void exportThenRead() {
        File file = tempDir.resolve("user.xlsx").toFile();
        List<User> users = List.of(new User("张三", 20), new User("李四", 30));

        ExcelUtils.export(file, "用户", User.class, users);
        ExcelResult<User> result = ExcelUtils.read(file, User.class);

        assertTrue(file.exists());
        assertFalse(result.hasError());
        assertEquals(2, result.getSuccessCount());
        assertEquals("张三", result.getData().get(0).getName());
        assertEquals(30, result.getData().get(1).getAge());
    }

    @Test
    @DisplayName("空列表只写表头，读回来是 0 行")
    void exportEmptyData() {
        File file = tempDir.resolve("empty.xlsx").toFile();

        ExcelUtils.export(file, "用户", User.class, List.of());
        ExcelResult<User> result = ExcelUtils.read(file, User.class);

        assertFalse(result.hasError());
        assertEquals(0, result.getSuccessCount());
    }

    @Test
    @DisplayName("文件不存在时抛业务异常")
    void readMissingFile() {
        BizException e = assertThrows(BizException.class,
                () -> ExcelUtils.read(new File(tempDir.toFile(), "not-exist.xlsx"), User.class));
        assertEquals(ResultCode.USER_UPLOAD_FILE_ERROR, e.getResultCode());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        @ExcelProperty("姓名")
        private String name;

        @ExcelProperty("年龄")
        private Integer age;
    }
}
