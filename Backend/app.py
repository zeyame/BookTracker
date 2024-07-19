from flask import Flask, jsonify, request
from flask_cors import CORS # type: ignore
import requests

app = Flask(__name__)
CORS(app)

SEARCH_URL = "https://openlibrary.org/search.json"
SUBJECTS_URL = "http://openlibrary.org/subjects"        # add /subject_name.json  to retrieve data about books of a specific genre
COVERS_URL = "https://covers.openlibrary.org/b"      # add the cover id, the size and then .jpg to retrieve the image url for a book's cover
ISBN_URL = "https://openlibrary.org/isbn"


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
    
    works = formattedWorks(works)       # array of objects with each object representing a book in the requested genre with its name, author, cover 
    
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
            book_cover = f'{COVERS_URL}/id/{book_cover_id}-S.jpg' if book_cover_id else ''
                
            result.append({
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
    
    # user searching with ISBN
    if isISBN(search):
        book_response = requests.get(f"{ISBN_URL}/{search}.json")
        if book_response.status_code != 200:
            return jsonify({'error': f'Failed response from the Open Library API when fetching book with ISBN {search}'}), book_response.status_code
        
        data = book_response.json()
        if not data:
            return jsonify({'error': f'No book with the isbn {search} was found'}), 404
                
        # formatting the response to be sent to client 
        book_name = data.get('title')
        
        author_data = data.get('authors', [])
        author_key = author_data[0].get('key') if author_data else ''
        
        author_response = requests.get(f"https://openlibrary.org{author_key}.json")
        if author_response.status_code != 200:
            return jsonify({'error': f'Failed response from the Open Library API when fetching author with key {author_key}'}), author_response.status_code
        
        author_name = author_response.json().get('name')
        
        book_cover = f"{COVERS_URL}/isbn/{search}-S.jpg"
        
        return jsonify([{
            'name': book_name,
            'author': author_name,
            'image_url': book_cover
        }])
            
    # title or author name
    else:
        book_response = requests.get(f"{SEARCH_URL}?q={search}&limit={limit}")
        if book_response.status_code != 200:
            return jsonify({'error': f'Failed response from the Open Library API to fetch book by title or author name of {search}'}), book_response.status_code
        
        books = book_response.json().get('docs', [])
        
        if not books:
            return jsonify({'error': f'No books with the search {search} could be found.'}), 404
        
        books = formattedBooks(books, limit)        # array of objects with each object representing a book with its name, title, cover
        
        return jsonify(books)
    

# helper function that checks if a user search has a valid format for an ISBN 
def isISBN(search):
    if len(search) != 10 and len(search) != 13:
        return False
    
    # checks if any char is not a num
    if not search.isnumeric():
        return False
    
    return True


# helper function that formats the docs property of a json object returned by a book search from the Open Library API
def formattedBooks(books, limit):
    result = []
    books = books[:int(limit)]       # extra safety so we dont end up looping over hundreds of books in case of error in limit parameter
    for book in books:
        book_name = book.get('title', '')
        book_author = book.get('author_name')[0] if book.get('author_name') else 'Uknown'
        
        book_cover_id = book.get('cover_i', '')
        book_cover = f"{COVERS_URL}/id/{book_cover_id}-S.jpg" if book_cover_id else ''
        
        result.append({
            'name': book_name,
            'author': book_author,
            'image_url': book_cover
        })
            
    return result
        
if __name__ == '__main__':
    app.run(debug=True)
    
