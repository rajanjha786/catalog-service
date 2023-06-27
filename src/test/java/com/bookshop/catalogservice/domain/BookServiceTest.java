package com.bookshop.catalogservice.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

class BookServiceTest {

    private BookRepository bookRepository;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = Mockito.mock(BookRepository.class);
        bookService = new BookService(bookRepository);
    }

    @Test
    void testGetAllBooks() {
        var expected = List.of(new Book(1L, "1234567891", "ABC", "XYZ", 10.5, "PUB1", 1, null, null),
                new Book(2L, "1234567892", "PQR", "XYZ", 11.5, "PUB1", 2, null, null));
        BDDMockito.given(bookRepository.findAll()).willReturn(expected);

        var actual = bookService.viewBookList();

        Assertions.assertEquals(expected, actual);

        Mockito.verify(bookRepository, Mockito.times(1)).findAll();
    }

    @Test
    void testViewBookDetailsByIsbnWhenBookExists() {
        var isbn = "1234567891";
        var expected = new Book(1L, isbn, "ABC", "XYZ", 10.5, "PUB1", 1, null, null);
        BDDMockito.given(bookRepository.findByIsbn(isbn)).willReturn(Optional.of(expected));

        var actual = bookService.viewBookDetails(isbn);

        Assertions.assertEquals(expected, actual);
        Mockito.verify(bookRepository, Mockito.times(1)).findByIsbn(isbn);
    }

    @Test
    void testViewBookDetailsByIsbnWhenBookDoesNotExits() {
        var isbn = "1234567891";
        BDDMockito.given(bookRepository.findByIsbn(isbn)).willReturn(Optional.empty());

        Assertions.assertThrows(BookNotFoundException.class, () -> bookService.viewBookDetails(isbn));

        Mockito.verify(bookRepository, Mockito.times(1)).findByIsbn(isbn);
    }

    @Test
    void testAddBookToCatalogWhenBookDoesNotExists() {
        var isbn = "1234567891";
        var expected = new Book(1L, isbn, "ABC", "XYZ", 10.5, "PUB1", 1, null, null);
        BDDMockito.given(bookRepository.existsByIsbn(isbn)).willReturn(false);
        BDDMockito.given(bookRepository.save(expected)).willReturn(expected);

        var actual = bookService.addBookToCatalog(expected);

        Assertions.assertEquals(expected, actual);
        Mockito.verify(bookRepository, Mockito.times(1)).existsByIsbn(isbn);
        Mockito.verify(bookRepository, Mockito.times(1)).save(expected);
    }

    @Test
    void testAddBookToCatalogWhenBookExists() {
        var isbn = "1234567891";
        var expected = new Book(1L, isbn, "ABC", "XYZ", 10.5, "PUB1", 1, null, null);
        BDDMockito.given(bookRepository.existsByIsbn(isbn)).willReturn(true);

        Assertions.assertThrows(BookAlreadyExistsException.class, () -> bookService.addBookToCatalog(expected));
        Mockito.verify(bookRepository, Mockito.times(1)).existsByIsbn(isbn);
        Mockito.verify(bookRepository, Mockito.never()).save(Mockito.any(Book.class));
    }


    @Test
    void testRemoveBookFromCatalog() {
        var isbn = "1234567891";
        Mockito.doNothing().when(bookRepository).deleteByIsbn(isbn);

        bookService.removeBookFromCatalog(isbn);

        Mockito.verify(bookRepository, Mockito.times(1)).deleteByIsbn(isbn);
    }

    @Test
    void testUpdateBookInCatalogWhenBookExists() {
        var isbn = "1234567891";
        var updateBookRequest = Book.of(isbn, "ABC", "XYZ", 10.5, "PUB1");
        var existingBook = new Book(1L, isbn, "ABC", "XYZ", 11.5, "PUB1", 1, null, null);
        var expected = new Book(1L, isbn, "ABC", "XYZ", 10.5, "PUB1", 1, null, null);
        BDDMockito.given(bookRepository.findByIsbn(isbn)).willReturn(Optional.of(existingBook));
        BDDMockito.given(bookRepository.save(expected)).willReturn(expected);

        var actual = bookService.editBookDetails(isbn, updateBookRequest);
        Assertions.assertEquals(expected, actual);
        Mockito.verify(bookRepository, Mockito.times(1)).findByIsbn(isbn);
        Mockito.verify(bookRepository, Mockito.times(1)).save(expected);

    }

    @Test
    void testUpdateBookInCatalogWhenBookDoesNotExists() {
        var isbn = "1234567891";
        var updateBookRequest = Book.of(isbn, "ABC", "XYZ", 10.5, "PUB1");
        var expected = new Book(1L, isbn, "ABC", "XYZ", 10.5, "PUB1", 1, null, null);
        BDDMockito.given(bookRepository.findByIsbn(isbn)).willReturn(Optional.empty());
        BDDMockito.given(bookRepository.save(updateBookRequest)).willReturn(expected);

        var actual = bookService.editBookDetails(isbn, updateBookRequest);
        Assertions.assertEquals(expected, actual);
        Mockito.verify(bookRepository, Mockito.times(1)).findByIsbn(isbn);
        Mockito.verify(bookRepository, Mockito.times(1)).save(updateBookRequest);

    }
}