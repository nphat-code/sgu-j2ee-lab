package com.example.demo.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.springframework.core.io.ClassPathResource;

import com.example.demo.model.Book;

public final class BookGenerator {

    public static List<Book> all() {
        try {
            var books = new ClassPathResource("books.csv").getInputStream();
            try (var lines = new BufferedReader(new InputStreamReader(books)).lines()) {
                return lines
                        .skip(1)
                        .map(BookGenerator::parseLine)
                        .filter(Objects::nonNull)
                        .toList();
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Error loading 'books.csv'!", ex);
        }
    }

    private static Book parseLine(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        if (line.contains("|")) {
            String[] row = line.split("\\|");
            return new Book(row[0].trim(), row[1].trim(), List.of(row[2].split(",")));
        } else {
            // Định dạng CSV chuẩn dấu phẩy, hỗ trợ chuỗi tác giả nằm trong dấu nháy kép
            String[] row = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 3);
            if (row.length < 3) {
                return null;
            }
            String authorsStr = row[2].replace("\"", "").trim();
            List<String> authors = Arrays.stream(authorsStr.split(","))
                    .map(String::trim)
                    .toList();
            return new Book(row[0].trim(), row[1].trim(), authors);
        }
    }

    public static List<Book> random(int size) {
        var books = all();
        if (size >= books.size()) {
            return books;
        }
        return IntStream.range(0, size)
                .mapToObj((x) -> randomBook(books))
                .toList();
    }

    public static Book random() {
        return randomBook(all());
    }

    private static Book randomBook(List<Book> books) {
        var idx = ThreadLocalRandom.current().nextInt(books.size());
        return books.get(idx);
    }
}
