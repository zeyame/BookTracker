from flask import Flask, jsonify, request
from flask_cors import CORS # type: ignore
import requests
import uuid
import asyncio, aiohttp
from collections import defaultdict

app = Flask(__name__)
CORS(app)

SEARCH_URL = "https://openlibrary.org/search.json"
SUBJECTS_URL = "http://openlibrary.org/subjects"        # add /subject_name.json  to retrieve data about books of a specific genre
COVERS_URL = "https://covers.openlibrary.org/b"      # add the cover id, the size and then .jpg to retrieve the image url for a book's cover

# Hashmaps

# k = genre, v = books
cache = defaultdict(list)

# k = genre, v = current offset 
genre_offset = {'romance': 0, 'fiction': 0, 'thriller': 0, 'action': 0, 'mystery': 0, 'history': 0, 'scifi': 0, 'horror': 0, 'fantasy': 0}


# SETTING UP THE ENDPOINTS FOR THE BACKEND

# Route handles fetching a set of books belonging to a specific genre from the Open Library API when application runs
@app.route('/<genre>-books', methods=['GET'])
def getBooksByGenre(genre):
    limit = request.args.get('limit', 7)
    offset = genre_offset.get(genre, 0)
    
    # requesting the API for twenty romance books
    response = requests.get(f"{SEARCH_URL}?subject={genre}&limit={limit}&offset={offset}")
    
    # we send back an error if request failed
    if response.status_code != 200:
        return jsonify({'error': f'An error occurred requesting books with subject: {genre}'}), response.status_code
    
    data = response.json()      # a python dictionary of 5 key value pairs
    books = data.get('docs', [])       # gets a python list of dictionaries of the 20 books, each dictionary storing metadata about a specific book
    
    if not books:
        return jsonify({'error': f'Could not find any books in the {genre} genre.'}), 404
    
    genre_offset[genre] += 7
    books = formatDefaultBooks(books, limit)       # array of objects with each object representing a book in the requested genre with its name, author, image url 
    return jsonify(books)

def formatDefaultBooks(books, limit):
    result = []
    books = books[:int(limit)]
    for book in books:
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
    
    
# Route handles fetching a book by title, author, or ISBN from the Open Library API when users inputs a search
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
        books = formatSearchedBooks(books, limit, isbn=True)
    else:
        books = formatSearchedBooks(books, limit)
    
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
def formatSearchedBooks(books, limit, isbn=False):
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
        
    # else we will make sure only the unique books of the 5 are returned - useful for typed user searches which may return repeated results
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
        
@app.route('/cache', methods=['GET'])
async def setup_cache():
    
    async with aiohttp.ClientSession() as session:
        tasks = [fetchBooks(session, genre, genre_offset.get(genre)) for genre in genre_offset.keys()]        # coroutine objects
        responses_json = await asyncio.gather(*tasks)       # array of json objects - each object represents the search result for books in one genre
        
        for genre, response in zip(genre_offset.keys(), responses_json):
            if response:
                formatted_books = formatDefaultBooks(response.get('docs', []), 14)
                if formatted_books:
                    genre_offset[genre] += 14
                    cache[genre] = formatted_books
                else:
                    print(f"No books were found in the returned json response for {genre} genre when caching.")
            
            else:
                return jsonify({'error': f'Failed to cache books for {genre} genre due to a failed response from the Open Library API.'})
            
    return jsonify({'Cache': cache}), 200

        
async def fetchBooks(session, genre, offset):
    url = f"{SEARCH_URL}?subject={genre}&limit=14&offset={offset}"
    
    async with session.get(url) as response:
        if response.status == 200:
            return await response.json()
        else:
            print(f"Failed to fetch books for {genre} genre to be stored in cache.")
            return None
        

@app.route('/cached-<genre>', methods=['GET'])
def getCachedBooks(genre):
    books = cache.get(genre, [])
    if genre not in cache or not books:
        return jsonify({'error': f'No cached books for {genre} genre on the server.'}), 404
        
    if len(books) < 7:
        cached_books = books 
        del cache[genre]
        return jsonify(cached_books)
           
    cached_books = cache[genre][-7:]        # get the last 7 elements to avoid any shifting
    del cache[genre][-7:]                   # delete the last 7 elements
    
    return jsonify(cached_books)
        
        
        
if __name__ == '__main__':
    app.run(debug=True)
    
