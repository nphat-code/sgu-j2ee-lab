package com.example.demo.service;

import java.util.Optional;

import com.example.demo.model.Book;

public interface BookService {
    Iterable<Book> findAll();

    Book create(Book book);

    Optional<Book> find(String isbn);
}
