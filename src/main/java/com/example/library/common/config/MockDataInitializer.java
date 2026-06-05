package com.example.library.common.config;

import com.example.library.admin.entity.LibraryAdmin;
import com.example.library.admin.repository.LibraryAdminRepository;
import com.example.library.book.entity.LibraryBookInfo;
import com.example.library.book.entity.LibraryBookInventory;
import com.example.library.book.repository.LibraryBookInfoRepository;
import com.example.library.book.repository.LibraryBookInventoryRepository;
import com.example.library.rental.entity.LibraryRentRecord;
import com.example.library.rental.repository.LibraryRentRecordRepository;
import com.example.library.user.entity.LibraryBlacklistReason;
import com.example.library.user.entity.LibraryUser;
import com.example.library.user.repository.LibraryBlacklistReasonRepository;
import com.example.library.user.repository.LibraryUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MockDataInitializer implements CommandLineRunner {

    private final LibraryBookInfoRepository bookInfoRepository;
    private final LibraryBookInventoryRepository inventoryRepository;
    private final LibraryUserRepository userRepository;
    private final LibraryBlacklistReasonRepository blacklistReasonRepository;
    private final LibraryRentRecordRepository rentRecordRepository;
    private final LibraryAdminRepository adminRepository;

    public MockDataInitializer(
            LibraryBookInfoRepository bookInfoRepository,
            LibraryBookInventoryRepository inventoryRepository,
            LibraryUserRepository userRepository,
            LibraryBlacklistReasonRepository blacklistReasonRepository,
            LibraryRentRecordRepository rentRecordRepository,
            LibraryAdminRepository adminRepository
    ) {
        this.bookInfoRepository = bookInfoRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
        this.blacklistReasonRepository = blacklistReasonRepository;
        this.rentRecordRepository = rentRecordRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (bookInfoRepository.count() > 0) {
            return;
        }

        seedBlacklistReasons();
        seedBooksAndInventory();
        List<LibraryUser> users = seedUsers();
        seedAdmin();
        seedRentRecords(users);
    }

    private void seedBlacklistReasons() {
        List<LibraryBlacklistReason> reasons = List.of(
                new LibraryBlacklistReason("OVERDUE_REPEAT", "반복 연체", "3회 이상 반납 기한 초과 이력"),
                new LibraryBlacklistReason("OVERDUE_LONG", "장기 연체", "반납 기한 30일 이상 초과"),
                new LibraryBlacklistReason("LOST", "도서 분실", "대여 도서 분실 처리"),
                new LibraryBlacklistReason("DAMAGE", "도서 훼손", "도서 심각 훼손"),
                new LibraryBlacklistReason("FRAUD", "부정 대여", "타인 명의 도용 등 부정 대여 시도"),
                new LibraryBlacklistReason("ETC", "기타", "기타 사유")
        );
        blacklistReasonRepository.saveAll(reasons);
    }

    private List<LibraryBookInfo> seedBooksAndInventory() {
        String[] categories = {"프로그래밍", "데이터", "소설", "역사", "자기계발", "디자인", "과학", "경제"};
        List<LibraryBookInfo> books = new ArrayList<>();
        List<LibraryBookInventory> inventory = new ArrayList<>();

        for (int i = 0; i < 40; i++) {
            String isbn = "9780000000" + String.format("%03d", i);
            LibraryBookInfo book = new LibraryBookInfo(
                    isbn,
                    "샘플 도서 " + (i + 1),
                    "저자 " + (i % 10 + 1),
                    "출판사 " + (i % 5 + 1),
                    categories[i % categories.length],
                    10000 + (i * 500),
                    (i % 18) + 1
            );
            books.add(book);
        }
        bookInfoRepository.saveAll(books);

        for (int i = 0; i < books.size(); i++) {
            int count = (i % 5) + 1;
            for (int j = 0; j < count; j++) {
                inventory.add(new LibraryBookInventory(books.get(i), true, null));
            }
        }
        inventoryRepository.saveAll(inventory);
        return books;
    }

    private List<LibraryUser> seedUsers() {
        LibraryUser user1 = new LibraryUser("9001151", "홍길동");
        LibraryUser user2 = new LibraryUser("0102033", "김민수");
        LibraryUser user3 = new LibraryUser("9507072", "이영희");
        LibraryUser user4 = new LibraryUser("8812121", "박서준");
        LibraryUser user5 = new LibraryUser("0203044", "최유진");
        LibraryUser user6 = new LibraryUser("9901012", "정하늘");

        LibraryBlacklistReason overdueLong = blacklistReasonRepository.findById("OVERDUE_LONG")
                .orElseThrow();
        user3.markBlacklisted(overdueLong, "장기 연체 이력", LocalDateTime.now().minusDays(5));

        List<LibraryUser> users = List.of(user1, user2, user3, user4, user5, user6);
        userRepository.saveAll(users);
        return users;
    }

    private void seedAdmin() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        LibraryAdmin admin = new LibraryAdmin(
                "admin",
                encoder.encode("admin1234"),
                "관리자",
                LocalDateTime.now()
        );
        adminRepository.save(admin);
    }

    private void seedRentRecords(List<LibraryUser> users) {
        List<LibraryBookInventory> inventory = inventoryRepository.findAll();
        List<LibraryRentRecord> records = new ArrayList<>();
        int cursor = 0;

        // 김민수: 현재 2권 대여
        records.add(createCurrentRental(inventory.get(cursor++), users.get(1), 4));
        records.add(createCurrentRental(inventory.get(cursor++), users.get(1), 2));

        // 최유진: 현재 1권 대여
        records.add(createCurrentRental(inventory.get(cursor++), users.get(4), 7));

        // 홍길동: 현재 0권, 과거 이력 2건
        records.add(createReturnedRental(inventory.get(cursor++), users.get(0), 35, 10));
        records.add(createReturnedRental(inventory.get(cursor++), users.get(0), 60, 19));

        // 박서준: 연체 이력 포함
        records.add(createReturnedRental(inventory.get(cursor++), users.get(3), 50, 35));
        records.add(createReturnedRental(inventory.get(cursor++), users.get(3), 90, 25));

        // 추가 이력 생성 (총 15건 이상)
        for (int i = 0; i < 8; i++) {
            LibraryUser user = users.get(i % 5);
            if (i % 2 == 0) {
                records.add(createReturnedRental(inventory.get(cursor++), user, 20 + i, 5));
            } else {
                records.add(createCurrentRental(inventory.get(cursor++), user, 1 + i));
            }
        }

        rentRecordRepository.saveAll(records);
    }

    private LibraryRentRecord createCurrentRental(
            LibraryBookInventory inventory,
            LibraryUser user,
            int daysAgo
    ) {
        LocalDate rentDate = LocalDate.now().minusDays(daysAgo);
        inventory.markRented(rentDate);
        return new LibraryRentRecord(
                inventory,
                inventory.getBookInfo(),
                user,
                user.getUserName(),
                rentDate,
                rentDate.plusDays(21),
                null,
                false
        );
    }

    private LibraryRentRecord createReturnedRental(
            LibraryBookInventory inventory,
            LibraryUser user,
            int daysAgo,
            int duration
    ) {
        LocalDate rentDate = LocalDate.now().minusDays(daysAgo);
        LocalDate dueDate = rentDate.plusDays(21);
        LocalDate returnDate = rentDate.plusDays(duration);
        LibraryRentRecord record = new LibraryRentRecord(
                inventory,
                inventory.getBookInfo(),
                user,
                user.getUserName(),
                rentDate,
                dueDate,
                null,
                false
        );
        record.markReturned(returnDate);
        return record;
    }
}
