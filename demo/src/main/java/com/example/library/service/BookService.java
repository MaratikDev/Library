package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.library.config.KafkaConfig.TOPIC;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public BookService(BookRepository repo, KafkaTemplate<String, String> kafkaTemplate) {
        this.bookRepository = repo;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<Book> listAvailableBooks() {
        return bookRepository.findByAvailableTrue();
    }

    public Book addBook(String title, String author, Integer year) {
        Book book = new Book(title, author, year);
        return bookRepository.save(book);
    }

    @Transactional
    public Book takeBook(Long bookId, String userToken) {
        Book book = bookRepository.findByIdForUpdate(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        if (!book.isAvailable()) {
            throw new RuntimeException("Book is already borrowed");
        }
        book.setAvailable(false);
        book.setBorrowedBy(userToken);
        Book saved = bookRepository.save(book);

        // отправка события в Kafka
        String event = String.format("{\"eventType\":\"BOOK_TAKEN\",\"bookId\":%d,\"userId\":%d}", bookId, userToken);
        kafkaTemplate.send(TOPIC, event);

        return saved;
    }

    @Transactional
    public Book returnBook(Long bookId, Long userId) {
        Book book = bookRepository.findByIdForUpdate(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        if (book.isAvailable() || !book.getBorrowedBy().equals(userId)) {
            throw new RuntimeException("Book is not borrowed by this user");
        }
        book.setAvailable(true);
        book.setBorrowedBy(null);
        Book saved = bookRepository.save(book);

        // отправка события в Kafka
        String event = String.format("{\"eventType\":\"BOOK_RETURNED\",\"bookId\":%d,\"userId\":%d}", bookId, userId);
        kafkaTemplate.send(TOPIC, event);

        return saved;
    }
}
