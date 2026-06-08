package com.example.library.admin.service;

import com.example.library.admin.dto.AdminBookAddRequestDto;
import com.example.library.admin.dto.AdminBookAddResponseDto;
import com.example.library.admin.dto.AdminBookDto;
import com.example.library.admin.dto.AdminCurrentRentalDto;
import com.example.library.admin.dto.AdminLoginRequestDto;
import com.example.library.admin.dto.AdminLoginResponseDto;
import com.example.library.admin.dto.BlacklistReasonDto;
import com.example.library.admin.dto.BlacklistReleaseRequestDto;
import com.example.library.admin.dto.BlacklistReleaseResponseDto;
import com.example.library.admin.dto.BlacklistRequestDto;
import com.example.library.admin.dto.BlacklistResponseDto;
import com.example.library.admin.dto.BlacklistUserDto;
import com.example.library.admin.dto.UserHistoryItemDto;
import com.example.library.admin.dto.UserHistoryResponseDto;
import com.example.library.admin.entity.LibraryAdmin;
import com.example.library.admin.repository.LibraryAdminRepository;
import com.example.library.book.entity.LibraryBookInfo;
import com.example.library.book.entity.LibraryBookInventory;
import com.example.library.book.repository.LibraryBookInfoRepository;
import com.example.library.book.repository.LibraryBookInventoryRepository;
import com.example.library.common.exception.BusinessException;
import com.example.library.common.exception.ErrorCode;
import com.example.library.rental.entity.LibraryRentRecord;
import com.example.library.rental.repository.LibraryRentRecordRepository;
import com.example.library.user.entity.LibraryBlacklistReason;
import com.example.library.user.entity.LibraryUser;
import com.example.library.user.repository.LibraryBlacklistReasonRepository;
import com.example.library.user.repository.LibraryUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AdminService {

    private final LibraryAdminRepository adminRepository;
    private final LibraryBookInfoRepository bookInfoRepository;
    private final LibraryBookInventoryRepository inventoryRepository;
    private final LibraryRentRecordRepository rentRecordRepository;
    private final LibraryUserRepository userRepository;
    private final LibraryBlacklistReasonRepository blacklistReasonRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    public AdminService(LibraryAdminRepository adminRepository,
                        LibraryBookInfoRepository bookInfoRepository,
                        LibraryBookInventoryRepository inventoryRepository,
                        LibraryRentRecordRepository rentRecordRepository,
                        LibraryUserRepository userRepository,
                        LibraryBlacklistReasonRepository blacklistReasonRepository,
                        PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.bookInfoRepository = bookInfoRepository;
        this.inventoryRepository = inventoryRepository;
        this.rentRecordRepository = rentRecordRepository;
        this.userRepository = userRepository;
        this.blacklistReasonRepository = blacklistReasonRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public AdminLoginResponseDto login(AdminLoginRequestDto request) {
        LibraryAdmin admin = adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> BusinessException.of(ErrorCode.EX_008));
        if (!passwordEncoder.matches(request.getAdminPw(), admin.getAdminPw())) {
            throw BusinessException.of(ErrorCode.EX_008);
        }
        return new AdminLoginResponseDto(admin.getAdminId(), admin.getAdminName());
    }

    @Transactional(readOnly = true)
    public List<AdminBookDto> getAllBooks() {
        String sql = """
                SELECT b.isbn, b.title, b.author, b.category, b.publisher, b.price,
                       COUNT(i.inventory_id) AS total_count,
                       SUM(CASE WHEN i.available = true THEN 1 ELSE 0 END) AS available_count,
                       b.rent_count
                FROM library_book_info b
                LEFT JOIN library_book_inventory i ON b.isbn = i.isbn AND i.del_yn = 'N'
                WHERE b.del_yn = 'N'
                GROUP BY b.isbn, b.title, b.author, b.category, b.publisher, b.price, b.rent_count
                ORDER BY b.title ASC
                """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery(sql).getResultList();
        return rows.stream().map(row -> new AdminBookDto(
                (String) row[0], (String) row[1], (String) row[2],
                (String) row[3], (String) row[4], ((Number) row[5]).intValue(),
                ((Number) row[6]).longValue(), ((Number) row[7]).longValue(),
                ((Number) row[8]).intValue()
        )).toList();
    }

    @Transactional
    public AdminBookAddResponseDto addBook(AdminBookAddRequestDto request) {
        String isbn = request.getIsbn();
        LibraryBookInfo bookInfo = bookInfoRepository.findById(isbn).orElseGet(() -> {
            LibraryBookInfo newBook = new LibraryBookInfo(
                    isbn, request.getTitle(), request.getAuthor(),
                    request.getPublisher(), request.getCategory(), request.getPrice(), 0
            );
            return bookInfoRepository.save(newBook);
        });

        int quantity = request.getQuantity();
        List<LibraryBookInventory> inventories = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            inventories.add(new LibraryBookInventory(bookInfo, true, null));
        }
        inventoryRepository.saveAll(inventories);

        return new AdminBookAddResponseDto(isbn, quantity);
    }

    @Transactional
    public void deleteBook(String isbn) {
        LibraryBookInfo bookInfo = bookInfoRepository.findById(isbn)
                .orElseThrow(() -> BusinessException.of(ErrorCode.EX_005));

        long rentedCount = inventoryRepository.countRentedByBook(bookInfo);
        if (rentedCount > 0) {
            throw BusinessException.of(ErrorCode.EX_007);
        }

        inventoryRepository.softDeleteAllByIsbn(isbn);
        bookInfoRepository.softDeleteByIsbn(isbn);
    }

    @Transactional
    public void deleteInventory(Long inventoryId) {
        LibraryBookInventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> BusinessException.of(ErrorCode.EX_005));
        if (inventory.isDeleted()) {
            throw BusinessException.of(ErrorCode.EX_005);
        }
        if (!inventory.isAvailable()) {
            throw BusinessException.of(ErrorCode.EX_007);
        }
        inventoryRepository.softDeleteById(inventoryId);
    }

    @Transactional(readOnly = true)
    public List<AdminCurrentRentalDto> getCurrentRentals(String sort, String direction) {
        List<LibraryRentRecord> records = rentRecordRepository.findAllCurrentRentals();
        LocalDate today = LocalDate.now();

        List<AdminCurrentRentalDto> result = records.stream().map(r -> {
            boolean isOverdue = today.isAfter(r.getDueDate());
            int overdueDays = isOverdue ? (int) (today.toEpochDay() - r.getDueDate().toEpochDay()) : 0;
            return new AdminCurrentRentalDto(
                    r.getRentId(),
                    r.getInventory().getInventoryId(),
                    r.getBookInfo().getIsbn(),
                    r.getBookInfo().getTitle(),
                    r.getUserName(),
                    maskUserCode7(r.getUser().getUserCode7()),
                    r.getRentDate(),
                    r.getDueDate(),
                    isOverdue,
                    overdueDays
            );
        }).collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        if ("dueDate".equalsIgnoreCase(sort)) {
            Comparator<AdminCurrentRentalDto> comparator = Comparator.comparing(AdminCurrentRentalDto::getDueDate);
            if ("desc".equalsIgnoreCase(direction)) comparator = comparator.reversed();
            result.sort(comparator);
        } else if ("rentDate".equalsIgnoreCase(sort)) {
            Comparator<AdminCurrentRentalDto> comparator = Comparator.comparing(AdminCurrentRentalDto::getRentDate);
            if ("desc".equalsIgnoreCase(direction)) comparator = comparator.reversed();
            result.sort(comparator);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public UserHistoryResponseDto getUserHistory(String userName, String userCode7) {
        LibraryUser user = userRepository.findById(userCode7)
                .orElseThrow(() -> BusinessException.of(ErrorCode.EX_005));

        if (!user.getUserName().equals(userName)) {
            throw BusinessException.of(ErrorCode.EX_015);
        }

        List<LibraryRentRecord> records = rentRecordRepository.findAllByUser(user);
        LocalDate today = LocalDate.now();

        List<UserHistoryItemDto> histories = records.stream().map(r -> {
            boolean isCurrentlyOverdue;
            int calculatedOverdueDays;
            String status;

            if (r.getReturnDate() == null) {
                isCurrentlyOverdue = today.isAfter(r.getDueDate());
                calculatedOverdueDays = isCurrentlyOverdue ? (int) (today.toEpochDay() - r.getDueDate().toEpochDay()) : 0;
                status = "RENTING";
            } else {
                isCurrentlyOverdue = r.isOverdue();
                calculatedOverdueDays = r.getOverdueDays();
                status = "RETURNED";
            }

            return new UserHistoryItemDto(
                    r.getRentId(),
                    r.getBookInfo().getIsbn(),
                    r.getBookInfo().getTitle(),
                    r.getRentDate(),
                    r.getDueDate(),
                    r.getReturnDate(),
                    status,
                    isCurrentlyOverdue,
                    calculatedOverdueDays
            );
        }).toList();

        long overdueCount = histories.stream().filter(UserHistoryItemDto::isOverdue).count();

        String reasonCode = user.getBlacklistReason() != null ? user.getBlacklistReason().getReasonCode() : null;

        return new UserHistoryResponseDto(
                user.getUserName(),
                maskUserCode7(userCode7),
                user.isBlacklisted(),
                reasonCode,
                records.size(),
                (int) overdueCount,
                histories,
                !user.isBlacklisted()
        );
    }

    @Transactional(readOnly = true)
    public List<BlacklistReasonDto> getBlacklistReasons() {
        return blacklistReasonRepository.findAll().stream()
                .map(r -> new BlacklistReasonDto(r.getReasonCode(), r.getReasonLabel(), r.getDescription()))
                .toList();
    }

    @Transactional
    public BlacklistResponseDto blacklist(BlacklistRequestDto request) {
        LibraryUser user = userRepository.findById(request.getUserCode7())
                .orElseThrow(() -> BusinessException.of(ErrorCode.EX_005));

        if (!user.getUserName().equals(request.getUserName())) {
            throw BusinessException.of(ErrorCode.EX_015);
        }
        if (user.isBlacklisted()) {
            throw BusinessException.of(ErrorCode.EX_011);
        }

        LibraryBlacklistReason reason = blacklistReasonRepository.findById(request.getReasonCode())
                .orElseThrow(() -> BusinessException.of(ErrorCode.EX_012));

        LocalDateTime now = LocalDateTime.now();
        user.markBlacklisted(reason, request.getMemo(), now);

        return new BlacklistResponseDto(
                user.getUserName(),
                maskUserCode7(user.getUserCode7()),
                true,
                reason.getReasonCode(),
                now
        );
    }

    @Transactional(readOnly = true)
    public List<BlacklistUserDto> getBlacklistUsers() {
        return userRepository.findAllBlacklisted().stream()
                .map(u -> new BlacklistUserDto(
                        u.getUserName(),
                        maskUserCode7(u.getUserCode7()),
                        u.getBlacklistReason() != null ? u.getBlacklistReason().getReasonCode() : null,
                        u.getBlacklistReason() != null ? u.getBlacklistReason().getReasonLabel() : null,
                        u.getBlacklistedAt()
                )).toList();
    }

    @Transactional
    public BlacklistReleaseResponseDto releaseBlacklist(BlacklistReleaseRequestDto request) {
        LibraryUser user = userRepository.findById(request.getUserCode7())
                .orElseThrow(() -> BusinessException.of(ErrorCode.EX_005));

        if (!user.getUserName().equals(request.getUserName())) {
            throw BusinessException.of(ErrorCode.EX_015);
        }

        user.releaseBlacklist();

        return new BlacklistReleaseResponseDto(
                user.getUserName(),
                maskUserCode7(user.getUserCode7()),
                false
        );
    }

    private String maskUserCode7(String code) {
        return code.substring(0, 4) + "***";
    }
}
