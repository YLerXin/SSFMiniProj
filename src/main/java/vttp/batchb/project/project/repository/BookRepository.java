package vttp.batchb.project.project.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import vttp.batchb.project.project.model.Book;

@Repository
public class BookRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public BookRepository(@Qualifier("redis-string") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveBook(String userEmail, Book book) {
        String key = "book:" + userEmail + ":" + book.getId();
        redisTemplate.opsForHash().put(key, "title", book.getTitle());
        redisTemplate.opsForHash().put(key, "coverImageUrl", book.getCoverImageUrl());
        redisTemplate.opsForHash().put(key, "textUrl", book.getTextUrl());
    }

    public Book getBookById(String userEmail, Integer id) {
        String key = "book:" + userEmail + ":" + id;
        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
        if (data == null || data.isEmpty()) {
            return null;
        }
        Book b = new Book();
        b.setId(id);
        b.setTitle((String) data.get("title"));
        b.setCoverImageUrl((String) data.get("coverImageUrl"));
        b.setTextUrl((String) data.get("textUrl"));
        return b;
    }

    public List<Book> getAllBooks(String userEmail) {
        var keys = redisTemplate.keys("book:" + userEmail + ":*");
        List<Book> books = new ArrayList<>();
        if (keys != null) {
            for (String key : keys) {
                Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
                if (!data.isEmpty()) {
                    Book b = new Book();
                    String[] parts = key.split(":");
                    Integer id = Integer.valueOf(parts[2]);
                    b.setId(id);
                    b.setTitle((String) data.get("title"));
                    b.setCoverImageUrl((String) data.get("coverImageUrl"));
                    b.setTextUrl((String) data.get("textUrl"));
                    books.add(b);
                }
            }
        }
        return books;
    }

    public void deleteBook(String userEmail, Integer id) {
        String key = "book:" + userEmail + ":" + id;
        redisTemplate.delete(key);
    }

    public void markBookAsRead(String userEmail, Integer id) {
        String key = "bookread:" + userEmail + ":" + id;
        redisTemplate.opsForValue().set(key, "true");
    }

    public boolean isBookRead(String userEmail, Integer id) {
        String key = "bookread:" + userEmail + ":" + id;
        String val = redisTemplate.opsForValue().get(key);
        return val != null && val.equals("true");
    }

    public void setBookFontSize(String userEmail, Integer id, String fontSize) {
        String key = "bookfontsize:" + userEmail + ":" + id;
        redisTemplate.opsForValue().set(key, fontSize);
    }

    public String getBookFontSize(String userEmail, Integer id) {
        String key = "bookfontsize:" + userEmail + ":" + id;
        String result = redisTemplate.opsForValue().get(key);
        return (result != null) ? result : "medium";
    }
}
