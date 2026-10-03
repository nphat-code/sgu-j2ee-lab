package com.example.demo.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.demo.model.Book;
import com.example.demo.repository.BookGenerator;

import jakarta.annotation.PostConstruct;

@Service
public class InMemoryBookService implements BookService {
    private final Map<String, Book> books = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        BookGenerator.all().forEach(this::create);
    }

    @Override
    public Iterable<Book> findAll() {
        return books.values();
    }

    @Override
    public Book create(Book book) {
        books.put(book.isbn(), book);
        return book;
    }

    @Override
    public Optional<Book> find(String isbn) {
        return Optional.ofNullable(books.get(isbn));
    }
}
