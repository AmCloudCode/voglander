package io.github.lunasaw.voglander.client.domain.excel.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
/**
 * excel的write的辅助bean
 */
public class ExcelWriteBean<T> implements Serializable {
    private BaseExcelDTO<T>       baseExcelDto;                         // 基础excelDTO

    private String                tempPath;                             // 模板路径
    private String                titleForTableHead;                    // 表头的题头
    private List<List<String>>    headList          = new ArrayList<>();// 表头数据

    private List<List<Object>>    datalist          = new ArrayList<>();// 数据

    private Map<Integer, Integer> getColumnWidthMap = new HashMap<>();  // 宽度

}
