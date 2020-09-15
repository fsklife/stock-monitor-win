package org.fsk.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @author fanshk
 * @date 2020/9/12 15:52
 */
@Builder
@Data
public class StrategyTable {
    private String sgStName;
    private String sgStCurrPrice;
    private String stCutPrice;
    private String stCutRate;
    private String stBuyCondition;
    private String stRemark;
}
