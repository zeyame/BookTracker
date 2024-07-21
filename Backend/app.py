from flask import Flask, jsonify, request
from flask_cors import CORS # type: ignore
import requests
import uuid

app = Flask(__name__)
CORS(app)

SEARCH_URL = "https://openlibrary.org/search.json"
SUBJECTS_URL = "http://openlibrary.org/subjects"        # add /subject_name.json  to retrieve data about books of a specific genre
COVERS_URL = "https://covers.openlibrary.org/b"      # add the cover id, the size and then .jpg to retrieve the image url for a book's cover

# SETTING UP THE ENDPOINTS FOR THE BACKEND

# Route handles fetching a given number of books of a specific genre from the Open Library API
@app.route('/<genre>-books', methods=['GET'])
def getBooksByGenre(genre):
    limit = request.args.get('limit', 20)
    
    # requesting the API for twenty romance books
    response = requests.get(f"{SUBJECTS_URL}/{genre}.json?limit={limit}")
    
    # we send back an error if request failed
    if response.status_code != 200:
        return jsonify({'error': f'An error occurred requesting books with subject: {genre}'}), response.status_code
    
    data = response.json()      # a python dictionary of 5 key value pairs
    works = data.get('works', [])       # gets a python list of dictionaries of the 20 books, each dictionary storing metadata about a specific book
    
    works = formattedWorks(works)       # array of objects with each object representing a book in the requested genre with its name, author, image url 
    
    return jsonify(works)


# helper function to format response for fetching the initial set of books displayed on client's search page
def formattedWorks(works):
    result = []
    if works:
        for work in works:
            # extracting the book name, cover id and author from metadata dictionary of each book
            book_name = work.get('title', '')
            authors = work.get('authors', [])
            author = authors[0].get('name') if authors else 'Unknown'
            
            # we get a jpg image url for each book
            book_cover_id = work.get('cover_id', '')
            book_cover = f'{COVERS_URL}/id/{book_cover_id}-S.jpg' if book_cover_id else 'https://via.placeholder.com/200x300.png?text=No+Cover'
                
            result.append({
                'id': uuid.uuid4(),
                'name': book_name, 
                'author': author,
                'image_url': book_cover
            })
            
    return result
    
    
# Route handles fetching a book by title, author, or ISBN from the Open Library API
@app.route('/book', methods=['GET'])
def getBook():
    search = request.args.get('search')
    limit = request.args.get('limit', 5)

    book_response = requests.get(f"{SEARCH_URL}?q={search}&limit={limit}")
    if book_response.status_code != 200:
        return jsonify({'error': f'Failed response from the Open Library API with search {search}'}), book_response.status_code
    
    books = book_response.json().get('docs', [])
    
    if not books:
        return jsonify({'error': f'No books with the search {search} could be found.'}), 404
    
    if isISBN(search):
        books = formattedBooks(books, limit, isbn=True)
    else:
        books = formattedBooks(books, limit)
    
    # respond with an array of book(s)
    return jsonify(books)
    

# # helper function that checks if a user search has a valid format for an ISBN 
def isISBN(search):
    if len(search) != 10 and len(search) != 13:
        return False
    
    # checks if any char is not a num
    if not search.isnumeric():
        return False
    
    return True


# helper function that formats the docs property of a json object returned by a book search from the Open Library API
def formattedBooks(books, limit, isbn=False):
    # if it was an isbn search
    if isbn:
        book_name = books[0].get('title', 'Unknown')
        book_author = books[0].get('author_name', ['Unknown'])[0]
        
        book_cover_id = books[0].get('cover_i', '')
        book_cover = f'{COVERS_URL}/id/{book_cover_id}-S.jpg' if book_cover_id else 'https://via.placeholder.com/200x300.png?text=No+Cover'

        return jsonify([{
            'id': uuid.uuid4(),
            'name': book_name,
            'author': book_author,
            'image_url': book_cover
        }])
        
    # else we will make sure only the unique books of the 5 are returned
    seen_books = set()
    result = []
    books = books[:int(limit)]       # extra safety so we dont end up looping over hundreds of books in case of error in limit parameter
    for book in books:
        book_isbn_list = book.get('isbn', [])
        book_isbn = book_isbn_list[0] if book_isbn_list else ''
        
        if book_isbn and book_isbn not in seen_books:
            seen_books.add(book_isbn)
            
            book_name = book.get('title', 'Unknown')
            book_author = book.get('author_name', ['Unknown'])[0]
            
            book_cover_id = book.get('cover_i', '')
            book_cover = f'{COVERS_URL}/id/{book_cover_id}-S.jpg' if book_cover_id else 'https://via.placeholder.com/200x300.png?text=No+Cover'

            
            result.append({
                'id': uuid.uuid4(),
                'name': book_name,
                'author': book_author,
                'image_url': book_cover
            })
            
    return result
        
if __name__ == '__main__':
    app.run(debug=True)
    
