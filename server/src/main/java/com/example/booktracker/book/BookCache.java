package com.example.booktracker.book;


import com.example.booktracker.book.exception.GenreNotInCacheException;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BookCache {

    private Map<String, List<BookDTO>> cache = new HashMap<>();         // stores the current books in the cache
    private Map<String, Integer> genreOffset = new HashMap<>();         // keeps track of the offset for each genre in the cache
    public BookCache() {
        // Initialize offsets to 0 for all genres
        Arrays.asList("romance", "fiction", "thriller", "action", "mystery", "history", "horror", "fantasy")
                .forEach(genre -> genreOffset.put(genre, 9));
    }


    /**
     * Updates the in-memory cache with a new cache map containing books for each genre.
     * The method replaces the current cache with the provided one and updates the genre offset map,
     * which tracks the number of books stored for each genre. The offset is incremented by the size
     * of the books list for each genre in the new cache.
     *
     * @param newCache A map where the key is the genre (String) and the value is a list of books (List<BookDTO>)
     *                 fetched for that genre. This replaces the current cache.
     */
    public void setUpCache(Map<String, List<BookDTO>> newCache) {
        this.cache = newCache;
        newCache.forEach((genre, books) -> genreOffset.put(genre, genreOffset.get(genre) + books.size()));
    }


    /**
     * Fetches the stored cache
     *
     * @return cache The current in-memory cache for all the genres
     */
    public Map<String, List<BookDTO>> getCache() {
        return cache;
    }


    /**
     * Fetches genreOffset map
     *
     * @return genreOffset The hashmap containing how many books have been fetched for each genre so far (offset)
     */
    public Map<String, Integer> getGenreOffset() {
        return genreOffset;
    }


    /**
     * Retrieves a specified number of books from the cache for a given genre.
     * If the genre is not present in the cache or if there are no books available for the genre,
     * it throws a {@link GenreNotInCacheException}. The method returns up to the specified limit
     * of books from the cache and removes the returned books from the cache afterward.
     *
     * @param genre The genre (as a String) for which cached books are requested.
     * @param limit The number of books to return. If the limit is greater than the number of cached books,
     *              it returns the available number of books.
     * @return A list of {@link BookDTO} objects, containing the books from the specified genre.
     * @throws GenreNotInCacheException If the genre does not exist in the cache or if the genre has no cached books.
     */
    public List<BookDTO> getCachedBooksByGenre(String genre, int limit) {
        List<BookDTO> books = cache.get(genre);
        if (books == null || books.isEmpty()) {
            throw new GenreNotInCacheException(genre + " is not an existing genre in the cache or is empty");
        }

        int booksToReturn = Math.min(limit, books.size());
        List<BookDTO> returnBooks = new ArrayList<>(books.subList(books.size() - booksToReturn, books.size()));

        // Remove the returned books from the cache
        books.subList(books.size() - booksToReturn, books.size()).clear();

        return returnBooks;
    }


    /**
     * Updates the cache with new books for a given genre. If the genre is not present in the cache,
     * it throws a {@link GenreNotInCacheException}. The method appends the new books to the existing
     * list of books for the genre and updates the cache accordingly. It also updates the genre offset
     * based on the number of new books added.
     *
     * @param genre The genre (as a String) for which the cached books should be updated.
     * @param newBooks A list of {@link BookDTO} objects representing the new books to be added to the cache.
     * @throws GenreNotInCacheException If the genre does not exist in the cache.
     */
    public void updateCachedBooksByGenre(String genre, List<BookDTO> newBooks) {
        List<BookDTO> existingBooks = cache.get(genre);
        if (existingBooks == null) {
            throw new GenreNotInCacheException(genre + " is not an existing genre in the cache.");
        }
        existingBooks.addAll(newBooks);
        cache.put(genre, existingBooks);
        updateOffsetForGenre(genre, newBooks.size());
    }

    public int getOffsetForGenre(String genre) throws GenreNotInCacheException {
        Integer offset = genreOffset.get(genre);
        if (offset == null) {
            throw new GenreNotInCacheException(genre + " is not an existing genre in the cache.");
        }
        return offset;
    }

    public List<BookDTO> viewBooksInAGenre(String genre) {
        return cache.get(genre);
    }

    private void updateOffsetForGenre(String genre, int value) {
        genreOffset.put(genre, genreOffset.get(genre) + value);
    }

}
