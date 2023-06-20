package com.bookshop.catalogservice.web;

import com.bookshop.catalogservice.config.BookshopProperties;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class HomeController {

    private final BookshopProperties bookshopProperties;

    @GetMapping("/")
    public String greeting() {
        return bookshopProperties.getGreeting();
    }
}
