package com.example.booktracker.book.mapper;

import com.example.booktracker.book.dto.BookDTO;
import com.example.booktracker.book.model.Book;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class BookMapper {

    /**
     * Maps a given {@link JsonNode} representing a book's details to a {@link BookDTO} object.
     * It extracts relevant fields such as title, authors, publisher, description, page count, categories,
     * image URL, and language from the provided JSON data. If the "volumeInfo" node is missing or null,
     * the method returns {@code null}.
     *
     * @param bookItem the {@link JsonNode} representing a book's details
     * @return a {@link BookDTO} containing the mapped data, or {@code null} if no "volumeInfo" is present
     */
    public Optional<BookDTO> mapToBookDTO(JsonNode bookItem) {
        String bookId = getTextOrEmpty(bookItem, "id");

        JsonNode volumeInfo = bookItem.get("volumeInfo");
        if (volumeInfo != null) {
            String title = getTextOrEmpty(volumeInfo, "title");
            List<String> authors = getAuthors(volumeInfo);
            String publisher = getTextOrEmpty(volumeInfo, "publisher");
            String description = getTextOrEmpty(volumeInfo, "description");
            int pageCount = getPageCount(volumeInfo);
            List<String> categories = getCategories(volumeInfo);
            String imageUrl = getImageUrl(volumeInfo);
            String language = getTextOrEmpty(volumeInfo, "language");

            return Optional.of(new BookDTO(bookId, title, authors, publisher, description, pageCount, categories, imageUrl, language));
        }

        return Optional.empty();
    }

    public BookDTO mapToBookDTO(Book book) {
        return new BookDTO(book.getId(), book.getTitle(), book.getAuthors(), book.getPublisher(),
                book.getDescription(), book.getPageCount(), book.getGenres(), book.getImageUrl(), book.getLanguage()
        );
    }

    public Book fromDTO(BookDTO bookDTO) {
        return new Book(bookDTO.getId(), bookDTO.getTitle(), bookDTO.getAuthors(), bookDTO.getPublisher(),
                bookDTO.getDescription(), bookDTO.getPageCount(), bookDTO.getCategories(),
                bookDTO.getImageUrl(), bookDTO.getLanguage());
    }


    /**
     * Retrieves the list of authors from the given {@link JsonNode}.
     * Extracts and returns the "authors" field from the "volumeInfo" node.
     * If no authors are found or the field is not an array, an empty list is returned.
     *
     * @param volumeInfo the {@link JsonNode} representing the book's volume information
     * @return a {@link List} of authors, or an empty list if no authors are found
     */
    private List<String> getAuthors(JsonNode volumeInfo) {
        JsonNode authorsNode = volumeInfo.get("authors");
        if (authorsNode != null && authorsNode.isArray()) {
            List<String> authors = new ArrayList<>();
            for (JsonNode author: authorsNode) {
                authors.add(author.asText());
            }

            return authors;
        }
        return Collections.emptyList();
    }


    /**
     * Retrieves the list of categories from the given {@link JsonNode}.
     * Extracts and returns the "categories" field from the "volumeInfo" node.
     * If no categories are found or the field is not an array, an empty list is returned.
     *
     * @param volumeInfo the {@link JsonNode} representing the book's volume information
     * @return a {@link List} of categories, or an empty list if no categories are found
     */
    private List<String> getCategories(JsonNode volumeInfo) {
        JsonNode categoriesNode = volumeInfo.get("categories");
        if (categoriesNode != null && categoriesNode.isArray()) {
            List<String> categories = new ArrayList<>();
            for (JsonNode category: categoriesNode) {
                categories.add(category.asText());
            }

            return categories;
        }

        return Collections.emptyList();
    }


    /**
     * Retrieves the thumbnail image URL for the book from the given {@link JsonNode}.
     * Extracts and returns the "thumbnail" field from the "imageLinks" node.
     * If no image link is found, an empty string is returned.
     *
     * @param volumeInfo the {@link JsonNode} representing the book's volume information
     * @return the thumbnail URL as a {@link String}, or an empty string if no image link is found
     */
    private String getImageUrl(JsonNode volumeInfo) {
        JsonNode imageLinks = volumeInfo.get("imageLinks");
        return (imageLinks != null) ? getTextOrEmpty(imageLinks, "thumbnail") : "https://via.placeholder.com/150x220?text=No+Cover+Available";
    }


    /**
     * Retrieves the text value of a specified field from the given {@link JsonNode}.
     * Returns the field's text value if it exists, otherwise returns an empty string.
     *
     * @param item      the {@link JsonNode} from which to extract the field
     * @param fieldName the name of the field to retrieve
     * @return the field's text value as a {@link String}, or an empty string if the field is not found
     */
    private String getTextOrEmpty(JsonNode item, String fieldName) {
        JsonNode fieldValue = item.get(fieldName);
        return (fieldValue != null) ? fieldValue.asText() : "";
    }


    /**
     * Retrieves the page count from the given {@link JsonNode}.
     * Extracts and returns the "pageCount" field as an integer. If the field is not found
     * or is not a number, returns 0 as the default value.
     *
     * @param volumeInfo the {@link JsonNode} representing the book's volume information
     * @return the page count as an {@code int}, or 0 if the field is not found or is not a number
     */
    private int getPageCount(JsonNode volumeInfo) {
        JsonNode pageCountNode = volumeInfo.get("pageCount");
        return (pageCountNode != null && pageCountNode.isNumber()) ? pageCountNode.asInt(0) : 0;
    }
}
