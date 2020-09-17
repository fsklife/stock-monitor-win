package org.fsk.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @author fanshk
 * @date 2020/9/17 19:35
 */
@Builder
@Data
public class EstimateShVolDTO {
    private String day;
    private String quarterVol;
    private String halfHourVol;
    private String currentVol;
}
