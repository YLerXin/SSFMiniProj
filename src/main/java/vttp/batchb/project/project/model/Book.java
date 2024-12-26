package vttp.batchb.project.project.model;

public class Book {
    private Integer id;
    private String title;
    private String coverImageUrl;
    private String textUrl;
    
    private boolean read;
    
    public Book() {}

    public Book(Integer id, String title, String coverImageUrl, String textUrl) {
        this.id = id;
        this.title = title;
        this.coverImageUrl = coverImageUrl;
        this.textUrl = textUrl;
    }
        
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getTextUrl() { return textUrl; }
    public void setTextUrl(String textUrl) { this.textUrl = textUrl; }

    // NEW: read status
    public boolean isRead() { 
        return read; 
    }
    public void setRead(boolean read) {
        this.read = read; 
    }
}
