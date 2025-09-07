package com.example.library.controller;

import com.example.library.dto.BookDto;
import com.example.library.entity.Book;
import com.example.library.security.JwtUtil;
import com.example.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final JwtUtil jwtUtil;

    public BookController(BookService bookService, JwtUtil jwtUtil) {
        this.bookService = bookService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<Book> listAvailableBooks() {
        return bookService.listAvailableBooks();
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody BookDto dto) {
        return ResponseEntity.ok(bookService.addBook(dto.getTitle(), dto.getAuthor(), dto.getPublishedYear()));
    }

    @PostMapping("/take/{bookId}")
    public ResponseEntity<Book> takeBook(@PathVariable Long bookId, HttpServletRequest req) {
        String token = jwtUtil.extractUsername(req.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(bookService.takeBook(bookId, token));
    }

    @PostMapping("/return/{bookId}")
    public ResponseEntity<Book> returnBook(@PathVariable Long bookId, HttpServletRequest req) {
        String token = jwtUtil.extractUsername(req.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(bookService.takeBook(bookId, token));
    }
}

