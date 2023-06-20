package com.bookshop.catalogservice.demo;

import com.bookshop.catalogservice.domain.Book;
import com.bookshop.catalogservice.domain.BookRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "bookshop.testdata.enabled", havingValue = "true")
@AllArgsConstructor
@Slf4j
public class BookDataLoader {

    private BookRepository bookRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void loadBookTestData() {
      log.info("Loading Test Book Data");
      bookRepository.deleteAll();
        var book1 = Book.of("1234567891", "Northern Lights",
                "Lyra Silverstar", 9.90, "ABC");
        var book2 = Book.of("1234567892", "Polar Journey",
                "Iorek Polarson", 12.90, "XYZ");
        bookRepository.saveAll(List.of(book1, book2));
        log.info("Loaded Test Book Data");
    }
}
