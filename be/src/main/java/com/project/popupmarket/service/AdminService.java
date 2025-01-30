package com.project.popupmarket.service;

import com.project.popupmarket.dto.admin.AdminReceiptsDTO;
import com.project.popupmarket.dto.land.RentalLandTO;
import com.project.popupmarket.dto.payment.ReceiptsManageTO;
import com.project.popupmarket.dto.user.UserDto;
import com.project.popupmarket.entity.User;
import com.project.popupmarket.enums.ReservationStatus;
import com.project.popupmarket.repository.AdminRepository;
import com.project.popupmarket.repository.UserRepository;
import com.project.popupmarket.service.land.RentalLandService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final RentalLandService rentalLandService;

    public UserDto findReceiptsAdminById(Long userId) {
        ModelMapper modelMapper = new ModelMapper();
        User user = userRepository.findUserInfoById(userId);
        return modelMapper.map(user, UserDto.class);
    }

    public Page<AdminReceiptsDTO> getAdminReceiptsInfo (ReservationStatus reservation_status, String sorting, Pageable pageable) {
        return adminRepository.findReceiptsByFilter(reservation_status, sorting, pageable)
                .map(receipt -> {
                    ReceiptsManageTO receiptsManageTO = ReceiptsManageTO.builder()
                            .paymentKey(receipt.getPaymentKey())
                            .amount(receipt.getAmount())
                            .reservationStatus(receipt.getReservationStatus())
                            .reservedAt(receipt.getReservedAt())
                            .build();

                    UserDto user = findReceiptsAdminById(receipt.getCustomerId());
                    RentalLandTO rentalLandTO = rentalLandService.findById(receipt.getRentalLandId());
                    return new AdminReceiptsDTO(receiptsManageTO, rentalLandTO, user);
                });
    }
}
