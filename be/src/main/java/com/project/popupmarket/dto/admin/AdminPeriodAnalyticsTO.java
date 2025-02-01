package com.project.popupmarket.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.popupmarket.enums.ActivateStatus;
import com.project.popupmarket.enums.Role;
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
}
