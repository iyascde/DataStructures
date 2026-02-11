import java.util.*;
import java.text.DecimalFormat;

// Author: Iyas Abdel Rahman
// Date: 9/30/2025 <- Old date. New date: 10/10/2025
// TA: Lawrence

/**
 * Represents a Book that can be searched, rated, and compared.
 * Implements the Media interface and Comparable<Book>.
 */
public class Book implements Media, Comparable<Book> {

    // these are the fields
    private final String title;
    private final List<String> authors;
    private final List<String> content;
    private int numRatings;
    private int totalRatings;

    /**
     * Constructs a new Book with the given title, authors, and content from the scanner.
     * param title   The title of the book.
     * param authors A list of authors of the book.
     * param sc      A Scanner to read the book's content.
     */
    public Book(String title, List<String> authors, Scanner sc) {
        this.title = title;
        this.authors = new ArrayList<>(authors);
        this.content = new ArrayList<>();
        this.numRatings = 0;
        this.totalRatings = 0;

        // Load all tokens from the scanner as content
        while (sc.hasNext()) {

            this.content.add(sc.next());
        }
    }

    // Returns the title of the book
    public String getTitle() {

        return this.title;
    }

    // Returns a list of authors of the book
    public List<String> getArtists() {

        return new ArrayList<>(this.authors);
    }

    // Adds a rating to the book
    // param score The rating score to add (must be non-negative)
    // it throws IllegalArgumentException if score is negative
    public void addRating(int score) {

        if (score < 0) {

            throw new IllegalArgumentException("Rating must be non-negative");
        }
        this.totalRatings += score;
        this.numRatings++;
    }

    

    // Returns the number of ratings the book has received
    public int getNumRatings() {

        return this.numRatings;
    }

    // Returns the average rating of the book
    // If the book has no ratings, it returns 0.0
    public double getAverageRating() {
        if (this.numRatings == 0) {
            return 0.0;
        }
        return (double) this.totalRatings / this.numRatings;
    }

    // Returns the content of the book as a list of strings
    public List<String> getContent() {
        return new ArrayList<>(this.content);
    }

    // Returns a string representation of the book
    public String toString() {
    String authorsStr = "";
    for (int i = 0; i < authors.size(); i++) {
        authorsStr += authors.get(i);
        if (i < authors.size() - 1) {
            authorsStr += ", ";
        }
    }

    if (numRatings == 0) {
        return title + " by [" + authorsStr + "]";
    } else {
        DecimalFormat df = new DecimalFormat("#.##");
        return title + " by [" + authorsStr + "]: "
                + df.format(getAverageRating())
                + " (" + numRatings + " ratings)";
    }
}

    // Compares this book to another book for ordering
    // First by average rating (higher first), then alphabetically by title
    // param other The other book to compare to
    // return A negative integer, zero, or a positive integer as this book
    // if positive, the other book is rated higher; if negative, this book is rated higher;
    // if zero, they are equal in rating and compared by title alphabetically
    public int compareTo(Book other) {
        // Compare by average rating (higher first)
        double diff = other.getAverageRating() - this.getAverageRating();
        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        } else {
            // break any tie alphabetically
            return this.title.compareToIgnoreCase(other.title);
        }
    }
}