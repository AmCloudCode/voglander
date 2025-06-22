package io.github.lunasaw.voglander.client.domain.excel.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author luna
 */
@Data

public class ExcelReadResultDTO<T> implements Serializable {

    /**
     * 序列化后的数据
     */
    private List<T> readResultList = new ArrayList<>();

}
