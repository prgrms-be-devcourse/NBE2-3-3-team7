package com.project.popupmarket.dto.admin;

import com.project.popupmarket.dto.land.RentalLandTO;
import com.project.popupmarket.dto.payment.ReceiptsManageTO;
import com.project.popupmarket.dto.user.UserDto;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminReceiptsDTO {
    private ReceiptsManageTO receiptsManageTO;  // 거래/영수증 정보
    private RentalLandTO rentalLand;  // 임대지 정보
    private UserDto userDto;            // 사용자 정보
}
