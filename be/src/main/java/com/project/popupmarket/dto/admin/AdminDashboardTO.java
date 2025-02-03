package com.project.popupmarket.dto.admin;

import com.project.popupmarket.dto.payment.ReceiptsManageTO;
import lombok.*;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardTO {
    private ReceiptsManageTO receipts;
    private String customer;
    private String landlord;
}
