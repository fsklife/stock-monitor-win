package org.fsk.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author fanshk
 * @date 2020/9/13 17:57
 */
@Data
public class SettingFileDTO {
    private StockMarketDTO stock;
    private List<SharesDTO> shares;
}
