package com.example.library.rental.service;

import com.example.library.book.entity.LibraryBookInfo;
import com.example.library.book.entity.LibraryBookInventory;
import com.example.library.book.repository.LibraryBookInfoRepository;
import com.example.library.book.repository.LibraryBookInventoryRepository;
import com.example.library.common.exception.BusinessException;
import com.example.library.common.exception.ErrorCode;
import com.example.library.common.util.UserCode7Util;
import com.example.library.rental.dto.CurrentRentalItemDto;
import com.example.library.rental.dto.RentalCheckRequestDto;
import com.example.library.rental.dto.RentalCheckResponseDto;
import com.example.library.rental.dto.RentalRequestDto;
import com.example.library.rental.dto.RentalResponseDto;
import com.example.library.rental.entity.LibraryRentRecord;
import com.example.library.rental.repository.LibraryRentRecordRepository;
import com.example.library.user.entity.LibraryUser;
import com.example.library.user.repository.LibraryUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RentalService {

    private final LibraryUserRepository userRepository;
    private final LibraryRentRecordRepository rentRecordRepository;
    private final LibraryBookInfoRepository bookInfoRepository;
    private final LibraryBookInventoryRepository inventoryRepository;

    public RentalService(LibraryUserRepository userRepository,
                         LibraryRentRecordRepository rentRecordRepository,
                         LibraryBookInfoRepository bookInfoRepository,
                         LibraryBookInventoryRepository inventoryRepository) {
        this.userRepository = userRepository;
        this.rentRecordRepository = rentRecordRepository;
        this.bookInfoRepository = bookInfoRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public RentalCheckResponseDto checkRental(RentalCheckRequestDto request) {
        String userCode7 = request.getUserCode7();
        String userName = request.getUserName();

        Optional<LibraryUser> userOpt = userRepository.findById(userCode7);

        if (userOpt.isPresent()) {
            LibraryUser user = userOpt.get();
            if (!user.getUserName().equals(userName)) {
                throw BusinessException.of(ErrorCode.EX_015);
            }

            List<LibraryRentRecord> currentRentals = rentRecordRepository.findCurrentRentalsByUser(user);
            int rentCount = currentRentals.size();
            boolean canRent = !user.isBlacklisted() && rentCount < 2;

            List<CurrentRentalItemDto> rentalItems = currentRentals.stream()
                    .map(r -> new CurrentRentalItemDto(
                            r.getRentId(),
                            r.getBookInfo().getIsbn(),
                            r.getBookInfo().getTitle(),
                            r.getRentDate(),
                            r.getDueDate()
                    )).toList();

            return new RentalCheckResponseDto(
                    user.getUserName(),
                    UserCode7Util.mask(userCode7),
                    user.isBlacklisted(),
                    rentCount,
                    canRent,
                    rentalItems
            );
        }

        return new RentalCheckResponseDto(userName, UserCode7Util.mask(userCode7), false, 0, true, Collections.emptyList());
    }

    @Transactional
    public RentalResponseDto rent(RentalRequestDto request) {
        String userCode7 = request.getUserCode7();
        String userName = request.getUserName();
        String isbn = request.getIsbn();

        LibraryUser user = userRepository.findById(userCode7).orElseGet(() -> {
            LibraryUser newUser = new LibraryUser(userCode7, userName);
            return userRepository.save(newUser);
        });

        if (!user.getUserName().equals(userName)) {
            throw BusinessException.of(ErrorCode.EX_015);
        }

        LibraryBookInfo bookInfo = bookInfoRepository.findById(isbn)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() -> BusinessException.of(ErrorCode.EX_005));

        if (user.isBlacklisted()) {
            throw BusinessException.of(ErrorCode.EX_002);
        }

        long currentRentCount = rentRecordRepository.countCurrentRentalsByUser(user);
        if (currentRentCount >= 2) {
            throw BusinessException.of(ErrorCode.EX_001);
        }

        LibraryBookInventory inventory = inventoryRepository.findFirstAvailableWithLock(bookInfo)
                .orElseThrow(() -> BusinessException.of(ErrorCode.EX_003));

        LocalDate rentDate = LocalDate.now();
        LocalDate dueDate = rentDate.plusDays(21);

        inventory.markRented(rentDate);
        bookInfo.incrementRentCount();

        LibraryRentRecord record = new LibraryRentRecord(
                inventory, bookInfo, user, userName, rentDate, dueDate, null, false
        );
        LibraryRentRecord saved = rentRecordRepository.save(record);

        return new RentalResponseDto(
                saved.getRentId(),
                inventory.getInventoryId(),
                bookInfo.getIsbn(),
                bookInfo.getTitle(),
                rentDate,
                dueDate
        );
    }
}
