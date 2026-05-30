package com.shadowfox.library.model;

public class Book {
    private int id;
    private String isbn;
    private String title;
    private int authorId;
    private Integer publishedYear;
    private Author author; // For mapping

    public Book() {}

    public Book(int id, String isbn, String title, int authorId, Integer publishedYear) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.authorId = authorId;
        this.publishedYear = publishedYear;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }
    public Integer getPublishedYear() { return publishedYear; }
    public void setPublishedYear(Integer publishedYear) { this.publishedYear = publishedYear; }
    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }

    @Override
    public String toString() {
        String authorName = author != null ? author.getName() : "ID: " + authorId;
        return "Book [ID=" + id + ", ISBN=" + isbn + ", Title='" + title + "', Author=" + authorName + ", Year=" + (publishedYear != null ? publishedYear : "N/A") + "]";
    }
}
