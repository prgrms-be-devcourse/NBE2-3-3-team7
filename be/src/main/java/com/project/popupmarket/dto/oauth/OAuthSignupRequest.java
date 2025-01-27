package com.project.popupmarket.dto.oauth;

import com.project.popupmarket.enums.Role;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthSignupRequest {
    @NotEmpty(message = "uuid는 필수입니다")
    private String uuid;
    @NotEmpty(message = "이름은 필수입니다")
    private String name;

    @NotEmpty(message = "브랜드명은 필수입니다")
    private String brand;

    @NotEmpty(message = "전화번호는 필수입니다")
    private String tel;

    @NotEmpty(message = "사업자 등록번호는 필수입니다")
    private String businessId;

    @NotEmpty(message = "ROLE은 필수입니다")
    private Role role;
}
