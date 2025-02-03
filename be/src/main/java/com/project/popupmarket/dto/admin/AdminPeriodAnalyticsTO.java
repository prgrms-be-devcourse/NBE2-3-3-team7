package com.project.popupmarket.dto.admin;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPeriodAnalyticsTO {
    private List<Map<String, Object>> current;
    private List<Map<String, Object>> prev;
    private AdminDataAnalyticsTO data;
}
