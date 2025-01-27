package com.project.popupmarket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "business_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessId {
    @Id
    private Long userId;    // User의 id를 PK로 직접 사용

    @Column(nullable = false)
    private String businessId;  // 사업자 등록번호

    @Builder
    public BusinessId(Long userId, String businessId) {
        this.userId = userId;
        this.businessId = businessId;
    }
}

