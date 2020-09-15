package org.fsk.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @author fanshk
 * @date 2020/9/9 19:01
 */
@Builder
@Data
public class ZsTable {
    private String name;
    private String currentPrice;
    private String zsChg;
    private String zsVol;
    private String zsPrevClose;
    private String zsDeltaPrice;
}
