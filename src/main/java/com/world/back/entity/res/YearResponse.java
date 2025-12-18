package com.world.back.entity.res;

import lombok.Data;
import java.util.List;

@Data
public class YearResponse {
    private Integer currentYear;
    private List<Integer> availableYears;

    public YearResponse(Integer currentYear, List<Integer> availableYears) {
        this.currentYear = currentYear;
        this.availableYears = availableYears;
    }
}