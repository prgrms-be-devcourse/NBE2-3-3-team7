package com.project.popupmarket.dto.admin;

import lombok.*;

import java.util.List;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDataAnalyticsTO {
    private List<String> labels;
    private List<Long> data;
}