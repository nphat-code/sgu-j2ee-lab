package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Book;
import com.example.demo.service.BookService;

@Controller
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Trang chu index.html
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Trang liet ke sach + list.html
    @GetMapping("/books.html")
    public String all(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books/list";
    }

    // Trang xem thong tin chi tiet sach + details.html
    @GetMapping(value = "/books.html", params = "isbn")
    public String get(@RequestParam("isbn") String isbn, Model model) {
        bookService.find(isbn)
                .ifPresent(book -> model.addAttribute("book", book));
        return "books/details";
    }

    @PostMapping("/books")
    @org.springframework.web.bind.annotation.ResponseBody
    public Book create(@ModelAttribute Book book) {
        return bookService.create(book);
    }
}
