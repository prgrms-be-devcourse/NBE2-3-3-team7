package com.project.popupmarket.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private String brand;
    private String profileImage;
    private String tel;
}
