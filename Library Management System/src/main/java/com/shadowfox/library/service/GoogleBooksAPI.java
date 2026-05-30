package com.shadowfox.library.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shadowfox.library.model.Author;
import com.shadowfox.library.model.Book;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GoogleBooksAPI {

    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";
    private final HttpClient httpClient;

    public GoogleBooksAPI() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public Book fetchBookByIsbn(String isbn) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(API_URL + isbn))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseJsonResponse(response.body(), isbn);
            } else {
                System.err.println("Failed to fetch book from API. Status Code: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Error fetching book from Google Books API: " + e.getMessage());
        }
        return null;
    }

    private Book parseJsonResponse(String json, String isbn) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        
        if (jsonObject.has("totalItems") && jsonObject.get("totalItems").getAsInt() > 0) {
            JsonArray items = jsonObject.getAsJsonArray("items");
            JsonObject volumeInfo = items.get(0).getAsJsonObject().getAsJsonObject("volumeInfo");

            String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Unknown Title";
            
            String authorName = "Unknown Author";
            if (volumeInfo.has("authors")) {
                authorName = volumeInfo.getAsJsonArray("authors").get(0).getAsString();
            }

            Integer publishedYear = null;
            if (volumeInfo.has("publishedDate")) {
                String date = volumeInfo.get("publishedDate").getAsString();
                if (date.length() >= 4) {
                    publishedYear = Integer.parseInt(date.substring(0, 4));
                }
            }

            Book book = new Book();
            book.setIsbn(isbn);
            book.setTitle(title);
            book.setPublishedYear(publishedYear);
            book.setAuthor(new Author(authorName));
            return book;
        }
        return null;
    }
}
