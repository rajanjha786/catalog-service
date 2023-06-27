package com.bookshop.catalogservice.web;

import com.bookshop.catalogservice.domain.Book;
import com.bookshop.catalogservice.domain.BookAlreadyExistsException;
import com.bookshop.catalogservice.domain.BookNotFoundException;
import com.bookshop.catalogservice.domain.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    void whenGetBookNotExistingThenShouldReturn404() throws Exception {
        String isbn = "7878656789";
        given(bookService.viewBookDetails(isbn)).willThrow(BookNotFoundException.class);
        mockMvc.perform(get("/books/"+isbn)).andExpect(status().isNotFound());
    }

    @Test
    void whenGetAllBooksThenShouldReturn200() throws Exception {
        var book = List.of(Book.of("1234567891", "ABC", "XYZ", 9.0, "PUB" ));
        given(bookService.viewBookList()).willReturn(book);
        mockMvc.perform((get("/books"))).andExpect(status().isOk());
    }

    @Test
    void whenPutBooksThenShouldReturn200() throws Exception {
        var isbn = "1234567891";
        var book = Book.of(isbn, "ABC", "XYZ", 9.0, "PUB");
        var json = """
                {
                    "isbn": "1234567891",
                    "title": "ABC",
                    "author": "XYZ",
                    "price": 9.0,
                    "publisher": "PUB"
                }
                """;
        given(bookService.editBookDetails(anyString(), any(Book.class))).willReturn(book);
        mockMvc.perform((put("/books/"+isbn).contentType(MediaType.APPLICATION_JSON).content(json)))
                .andExpect(status().isOk());
    }

    @Test
    void whenDeleteBookThenShouldReturn201() throws Exception {
        var isbn = "1234567891";
        doNothing().when(bookService).removeBookFromCatalog(anyString());
        mockMvc.perform((delete("/books/"+isbn))).andExpect(status().isNoContent());
    }

    @Test
    void whenPostBooksThenShouldReturn422() throws Exception {
        var isbn = "1234567891";
        var json = """
                {
                    "isbn": "1234567891",
                    "title": "ABC",
                    "author": "XYZ",
                    "price": 9.0,
                    "publisher": "PUB"
                }
                """;
        given(bookService.addBookToCatalog(any(Book.class))).willThrow(BookAlreadyExistsException.class);
        mockMvc.perform((post("/books").contentType(MediaType.APPLICATION_JSON).content(json)))
                .andExpect(status().is(422));
    }

    @Test
    void whenPostInvalidBooksThenShouldReturn400() throws Exception {
        var isbn = "1234567891";
        var json = """
                {
                    "isbn": "123456789145666",
                    "title": "ABC",
                    "author": "XYZ",
                    "price": 9.0,
                    "publisher": "PUB"
                }
                """;
        mockMvc.perform((post("/books").contentType(MediaType.APPLICATION_JSON).content(json)))
                .andExpect(status().is(400));
    }
}
