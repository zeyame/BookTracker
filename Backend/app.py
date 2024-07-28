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

GOOGLE_URL = "https://www.googleapis.com/books/v1/volumes"
API_KEY = "AIzaSyA_ZGJLkmhuHrFfmQaGBCu-Ug8O9SrpbHI"

# Hashmaps

# k = genre, v = books
cache = defaultdict(list)

# k = genre, v = current offset 
genre_offset = {'romance': 7, 'fiction': 7, 'thriller': 7, 'action': 7, 'mystery': 7, 'history': 7, 'horror': 7, 'fantasy': 7}


# SETTING UP THE ENDPOINTS FOR THE BACKEND

# Route handles fetching a set of books belonging to a specific genre from the Open Library API when application runs
@app.route('/<genre>-books', methods=['GET'])
def getBooksByGenre(genre):
    limit = int(request.args.get('limit', 7))
    
    # requesting the API for a certain number of books for the specified genre
    response = requests.get(f"{GOOGLE_URL}?q=subject:{genre}&maxResults={limit}&fields=items(volumeInfo/title,volumeInfo/authors,volumeInfo/imageLinks/thumbnail)&key={API_KEY}")
    
    # we send back an error if request failed
    if response.status_code != 200:
        return jsonify({'error': f'An error occurred requesting books with subject: {genre}'}), response.status_code
    
    data = response.json()      # an object that has items array 
    
    # gets an array of objects and each object has properties: id, volumeInfo object with properties: title, authors[], imageLinks object with property thumbnail
    books = data.get('items', [])       
    
    if not books:
        return jsonify({'error': f'No books were return for the {genre} genre from the API.'}), 404
    
    books = formatBooks(books, limit)       # array of objects with each object representing a book in the requested genre with its name, author, image url 
    return jsonify(books)

def formatBooks(books, limit):
    result = []
    books = books[:int(limit)]
    for book in books:
        book_title = book.get('volumeInfo', {}).get('title', 'Uknown')
        book_authors = book.get('volumeInfo', {}).get('authors', [])
        book_cover = book.get('volumeInfo', {}).get('imageLinks', {}).get('thumbnail', 'https://via.placeholder.com/200x300.png?text=No+Cover')
            
        result.append({
            'id': uuid.uuid4(),
            'title': book_title,
            'authors': book_authors,
            'image_url': book_cover
        })
        
    return result
    
    
# Route handles fetching a book by title, author, or ISBN from the Open Library API when users inputs a search
@app.route('/book', methods=['GET'])
def getBook():
    search = request.args.get('search')
    limit = int(request.args.get('limit', 5))

    if isISBN(search):
        book_response = requests.get(f"{GOOGLE_URL}?q=isbn:{search}&fields=items(volumeInfo/title,volumeInfo/authors,volumeInfo/imageLinks/thumbnail)&key={API_KEY}")
    else:
        book_response = requests.get(f"{GOOGLE_URL}?q={search}&fields=items(volumeInfo/title,volumeInfo/authors,volumeInfo/imageLinks/thumbnail)&maxResults={limit}&key={API_KEY}")
        
    if book_response.status_code != 200:
        return jsonify({'error': f'Failed response from the Google Books API with search {search}'}), book_response.status_code
    
    books = book_response.json().get('items', [])
    
    if not books:
        return jsonify({'error': f'No books were return for the search {search} from the Google Books API.'}), 404
    
    books = formatBooks(books, limit)
    
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


@app.route('/cache', methods=['GET'])
async def setup_cache():
    limit = int(request.args.get('limit', 7))
    async with aiohttp.ClientSession() as session:
        tasks = [fetchBooks(session, genre, limit, genre_offset[genre]) for genre in genre_offset.keys()]        # coroutine objects
        responses_json = await asyncio.gather(*tasks)       # array of json objects - each object represents the search result for books in one genre
        
        for genre, response in zip(genre_offset.keys(), responses_json):
            if response:
                formatted_books = formatBooks(response.get('items', []), limit)
                if formatted_books:
                    genre_offset[genre] += limit
                    cache[genre] = formatted_books
                else:
                    print(f"No books were found in the returned json response for {genre} genre when caching.")
            
            else:
                return jsonify({'error': f'Failed to cache books for {genre} genre due to a failed response from the Open Library API.'})
            
    return jsonify({'Message': f"Cache has been successfully set up with {limit} books in every genre."}), 200

        
async def fetchBooks(session, genre, limit, offset):
    url = f"{GOOGLE_URL}?q=subject:{genre}&maxResults={limit}&startIndex={offset}&fields=items(volumeInfo/title,volumeInfo/authors,volumeInfo/imageLinks/thumbnail)&key={API_KEY}"
    
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
           
    cached_books = cache[genre][-7:]        # get the last 7 elements to avoid any shifting for better performance
    del cache[genre][-7:]                   # delete the last 7 elements - O(1)        
    
    return jsonify(cached_books)
        
        
@app.route('/update-<genre>-cache', methods=['GET'])
def updateGenreCache(genre):
    if genre not in cache:
        return jsonify({'error': f'{genre} genre does not exist in the cache'}), 404
    
    limit = int(request.args.get('limit', 7))
    
    response = requests.get(f"{GOOGLE_URL}?q=subject:{genre}&maxResults={limit}&startIndex={genre_offset[genre]}&fields=items(volumeInfo/title,volumeInfo/authors,volumeInfo/imageLinks/thumbnail)&key={API_KEY}")
    
    # we send back an error if request failed
    if response.status_code != 200:
        return jsonify({'error': f'An error occurred requesting books with subject: {genre}'}), response.status_code
    
    data = response.json()     
    books = data.get('items', [])
    
    if not books:
        return jsonify({'error': f'Could not fetch new books for the {genre} genre when updating its cache.'}), 404
    
    genre_offset[genre] += limit     
    
    books = formatBooks(books, limit)
    cache[genre].extend(books)
    
    return jsonify({'Message': f'Successfully updated the cache for {genre} genre with {limit} more books.'}), 200
    
        
if __name__ == '__main__':
    app.run(debug=True)
    
