package org.fsk.pojo;

import lombok.Data;

/**
 * @author fanshk
 * @date 2020/9/13 18:33
 */
@Data
public class SharesDTO {
    private String code;
    private String name;
    private String cutPrice;
    private String buyCondition;
    private String optStrategy;
}
