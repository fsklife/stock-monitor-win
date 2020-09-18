package org.fsk.pojo;

import lombok.Data;

/**
 * @author fanshk
 * @date 2020/9/17 19:35
 */
@Data
public class EstimateShVolDTO {
    private String day;
    private String quarterVol;
    private String halfHourVol;
    private String currentVol;

    public static EstimateShVolDTO init() {
        EstimateShVolDTO estimateShVolDTO = new EstimateShVolDTO();
        estimateShVolDTO.setDay("");
        estimateShVolDTO.setHalfHourVol("");
        estimateShVolDTO.setQuarterVol("");
        estimateShVolDTO.setCurrentVol("");
        return estimateShVolDTO;
    }
}
