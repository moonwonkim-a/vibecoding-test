package com.example.library.rental.service;

import com.example.library.common.exception.BusinessException;
import com.example.library.common.exception.ErrorCode;
import com.example.library.common.util.UserCode7Util;
import com.example.library.rental.dto.ReturnCandidateDto;
import com.example.library.rental.dto.ReturnRequestDto;
import com.example.library.rental.dto.ReturnResponseDto;
import com.example.library.rental.entity.LibraryRentRecord;
import com.example.library.rental.repository.LibraryRentRecordRepository;
import com.example.library.user.entity.LibraryUser;
import com.example.library.user.repository.LibraryUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReturnService {

    private final LibraryUserRepository userRepository;
    private final LibraryRentRecordRepository rentRecordRepository;

    public ReturnService(LibraryUserRepository userRepository,
                         LibraryRentRecordRepository rentRecordRepository) {
        this.userRepository = userRepository;
        this.rentRecordRepository = rentRecordRepository;
    }

    @Transactional(readOnly = true)
    public List<ReturnCandidateDto> getCandidates(String userName, String userCode7) {
        UserCode7Util.validate(userCode7);

        LibraryUser user = userRepository.findById(userCode7)
                .orElseThrow(() -> BusinessException.of(ErrorCode.EX_005));

        if (!user.getUserName().equals(userName)) {
            throw BusinessException.of(ErrorCode.EX_015);
        }

        List<LibraryRentRecord> currentRentals = rentRecordRepository.findCurrentRentalsByUser(user);
        LocalDate today = LocalDate.now();

        return currentRentals.stream().map(r -> {
            boolean isOverdue = today.isAfter(r.getDueDate());
            int overdueDays = isOverdue ? (int) (today.toEpochDay() - r.getDueDate().toEpochDay()) : 0;
            return new ReturnCandidateDto(
                    r.getRentId(),
                    r.getInventory().getInventoryId(),
                    r.getBookInfo().getIsbn(),
                    r.getBookInfo().getTitle(),
                    r.getRentDate(),
                    r.getDueDate(),
                    isOverdue,
                    overdueDays
            );
        }).toList();
    }

    @Transactional
    public ReturnResponseDto returnBook(ReturnRequestDto request) {
        LibraryUser user = userRepository.findById(request.getUserCode7())
                .orElseThrow(() -> BusinessException.of(ErrorCode.EX_005));

        if (!user.getUserName().equals(request.getUserName())) {
            throw BusinessException.of(ErrorCode.EX_015);
        }

        LibraryRentRecord record = rentRecordRepository.findById(request.getRentId())
                .orElseThrow(() -> BusinessException.of(ErrorCode.EX_005));

        if (!record.getUser().getUserCode7().equals(request.getUserCode7())) {
            throw BusinessException.of(ErrorCode.EX_005);
        }

        if (record.getReturnDate() != null) {
            throw BusinessException.of(ErrorCode.EX_005);
        }

        LocalDate returnDate = LocalDate.now();
        record.markReturned(returnDate);
        record.getInventory().markReturned();

        return new ReturnResponseDto(
                record.getRentId(),
                record.getBookInfo().getIsbn(),
                record.getBookInfo().getTitle(),
                returnDate,
                record.isOverdue(),
                record.getOverdueDays()
        );
    }

    public String getReturnSuccessMessage(ReturnResponseDto response) {
        if (response.isOverdue()) {
            return ErrorCode.EX_004.getMessage();
        }
        return "반납이 완료되었습니다. 감사합니다.";
    }
}
