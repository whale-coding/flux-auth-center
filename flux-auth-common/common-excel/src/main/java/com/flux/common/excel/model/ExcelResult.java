package com.flux.common.excel.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ExcelResult
 * @Author njq
 * @Date 2026/10/28
 * @Description Excel 导入结果：解析成功的行 + 失败行的错误信息
 */
public class ExcelResult<T> {

    private final List<T> data;
    private final List<String> errors;

    public ExcelResult(List<T> data, List<String> errors) {
        this.data = data == null ? new ArrayList<>() : data;
        this.errors = errors == null ? new ArrayList<>() : errors;
    }

    /**
     * 解析成功的数据
     */
    public List<T> getData() {
        return data;
    }

    /**
     * 失败行的错误信息，形如「第 3 行：xxx」
     */
    public List<String> getErrors() {
        return errors;
    }

    public boolean hasError() {
        return !errors.isEmpty();
    }

    public int getSuccessCount() {
        return data.size();
    }

    public int getFailCount() {
        return errors.size();
    }
}
