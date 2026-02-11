import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class Testing {

    @Test
    @DisplayName("Book string, list constructor")
    public void testBookStringList() {
        Book book = new Book("Title", List.of("Author 1", "Author 2"), new Scanner("Content"));

        // Tests that getTitle returns "Title"
        assertEquals("Title", book.getTitle());

        // Tests that getArtists returns a list containing "Author 1" and "Author 2"
        assertEquals(List.of("Author 1", "Author 2"), book.getArtists()); 

        // Tests that toString returns the correctly
        //       String representation
        assertEquals("Title by [Author 1, Author 2]", book.toString());

        // Tests that getContent returns a list containing "Content"
        assertEquals(List.of("Content"), book.getContent());
    }

    @Test
    @DisplayName("getNumRatings")
    public void testNumRatings() {
        Book book = new Book("Title", List.of("Author"), new Scanner("Content"));
        // This Tests that getNumRatings returns 0
        assertEquals(0, book.getNumRatings());
        // This shows that adding a rating works
        book.addRating(1);
        assertEquals(1, book.getNumRatings());

        // This also shows that adding a second rating works
        book.addRating(1);
        assertEquals(2, book.getNumRatings());

        
    }

    @Test
    @DisplayName("getAvgRating")
    public void testAvgRatings() {
        Book book = new Book("Title", List.of("Author"), new Scanner("Content"));
        // Tests that getAverageRating returns 0
        assertEquals(0.0, book.getAverageRating());
        
        // Tests that adding a rating of 4 works
        book.addRating(4);
        assertEquals(4.0, book.getAverageRating());

        // Tests that adding a second rating of 5 works and that the averagre that it returns is thecorrect one
        book.addRating(5);
        assertEquals(4.5, book.getAverageRating());

        
    }

    @Test
    @DisplayName("createIndex tests")
    public void testInvertedIndex() {
        Book mistborn = new Book("Mistborn", List.of("Brandon Sanderson"),
                                 new Scanner("Epic fantasy worldbuildling content"));
        Book farenheit = new Book("Farenheit 451", List.of("Ray Bradbury"),
                                  new Scanner("Realistic \"sci-fi\" content"));
        Book hobbit = new Book("The Hobbit", List.of("J.R.R. Tolkein"),
                               new Scanner("Epic fantasy quest content"));
        
        List<Media> books = List.of(mistborn, farenheit, hobbit);
        Map<String, Set<Media>> index = SearchClient.createIndex(books);
        
        // Tests that the quotes around sci-fi aren't ignored.
        assertFalse(index.containsKey("sci-fi"));
        assertTrue(index.containsKey("\"sci-fi\""));

        // This shows that we can search for common words
        Set<Book> expected = Set.of(mistborn, hobbit);
        assertEquals(expected, index.get("fantasy"));
    }
}
