package com.flux.common.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.flux.common.excel.model.ExcelResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ExcelReadListener
 * @Author njq
 * @Date 2026/10/28
 * @Description 默认的读取监听器：收集解析成功的数据，单行解析失败只记录不中断，
 * 导入结束后通过 {@link #getResult()} 拿到成功数据和错误行
 */
@Slf4j
public class ExcelReadListener<T> implements ReadListener<T> {

    private final List<T> data = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    @Override
    public void invoke(T item, AnalysisContext context) {
        data.add(item);
    }

    /**
     * 单行解析异常时 EasyExcel 默认会中断整个导入，这里吞掉异常改成记录错误继续往下读
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        Integer rowNum = context.getCurrentRowNum();
        String row = rowNum == null ? "未知行" : "第 " + (rowNum + 1) + " 行";
        errors.add(row + "：" + exception.getMessage());
        log.warn("Excel {} 解析失败", row, exception);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.debug("Excel 解析完成，成功 {} 行，失败 {} 行", data.size(), errors.size());
    }

    public ExcelResult<T> getResult() {
        return new ExcelResult<>(data, errors);
    }
}
