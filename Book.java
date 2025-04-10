public class Book {
    private long iSBN;
    private String title;
    private String author;
    private boolean isIssued;

    public Book(long iSBN, String title, String author) {
        this.iSBN = iSBN;
        this.title = title;
        this.author = author;
        this.isIssued = false; // By default, the book is not issued
    }

    public Book(long iSBN, String title, String author, boolean isIssued) {
        this.iSBN = iSBN;
        this.title = title;
        this.author = author;
        this.isIssued = isIssued;
    }

    public long getId() {
        return iSBN;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isIssued() {
        return isIssued;
    }

    public void setIssued(boolean issued) {
        isIssued = issued;
    }

    @Override
    public String toString() {
        return "Book ID: " + iSBN + ", Title: " + title + ", Author: " + author + ", Issued: "
                + (isIssued ? "Yes" : "No");
    }
}