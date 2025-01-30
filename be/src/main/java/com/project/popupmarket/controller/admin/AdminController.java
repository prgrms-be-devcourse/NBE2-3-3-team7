package com.project.popupmarket.controller.admin;

import com.project.popupmarket.dto.admin.AdminReceiptsDTO;
import com.project.popupmarket.dto.land.RentalLandRespTO;
import com.project.popupmarket.dto.land.RentalLandTO;
import com.project.popupmarket.enums.ActivateStatus;
import com.project.popupmarket.enums.ReservationStatus;
import com.project.popupmarket.service.land.RentalLandService;
import com.project.popupmarket.service.receipts.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private RentalLandService rentalLandService;
    @Autowired
    private PaymentService paymentService;


    @GetMapping("/lands")
    @Operation(summary = "조건에 해당하는 임대지 리스트")
    public ResponseEntity<Page<RentalLandTO>> getLandsByFilter(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) ActivateStatus status,
            @RequestParam(required = false) String sorting,
            @RequestParam(required = false) String title,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<RentalLandTO> rentalLandTO = rentalLandService.findLandAdminByFilter(address, status, title, sorting, pageable);
        return ResponseEntity.ok(rentalLandTO);
    }
}
