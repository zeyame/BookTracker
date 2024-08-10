from flask import Flask, jsonify, request
from flask_cors import CORS # type: ignore
import requests
import uuid
import asyncio, aiohttp
from collections import defaultdict
from datetime import datetime

app = Flask(__name__)
CORS(app)

# External API endpoints
GOOGLE_URL = "https://www.googleapis.com/books/v1/volumes"
OPENLIBRARY_URL = "https://openlibrary.org/search.json"
WIKIPEDIA_URL = "https://en.wikipedia.org/w/api.php"
TASTEDIVE_URL = "https://tastedive.com/api/similar"

# API KEYS
GOOGLE_KEY = "AIzaSyBeNJgzSObopgk16PTMPShYOLGwDNt24Ec"
TASTEDIVE_KEY = "1033070-BookTrac-59A622F3"

# Hashmaps

# k = genre, v = books
cache = defaultdict(list)

# k = genre, v = current offset 
genre_offset = {'romance': 7, 'fiction': 7, 'thriller': 7, 'action': 7, 'mystery': 7, 'history': 7, 'horror': 7, 'fantasy': 7}


# SETTING UP ENDPOINTS 

# fetches a specified number of books to be displayed on the application's default search page
@app.route('/<genre>-books', methods=['GET'])
def getBooksByGenre(genre):
    limit = request.args.get('limit', 9)
    
    try:
        limit = int(limit)
    except:
        return jsonify({'error': f'Limit parameter {limit} sent to endpoint "/<genre>-books" is not a valid integer that can be parsed.'})
    
    # requesting the API for a certain number of books for the specified genre
    response = requests.get(f"{GOOGLE_URL}?q=subject:{genre}&maxResults={limit}&fields=items(id,volumeInfo/title,volumeInfo/authors,volumeInfo/publisher,volumeInfo/publishedDate,volumeInfo/description,volumeInfo/pageCount,volumeInfo/categories,volumeInfo/imageLinks/thumbnail, volumeInfo/language)&key={GOOGLE_KEY }")
    
    # we send back an error if request failed
    if response.status_code != 200:
        return jsonify({'error': f'An error occurred requesting books with subject: {genre}'}), response.status_code
    
    data = response.json()      # an object that has items array 
    
    # gets an array of objects and each object has properties: id, volumeInfo object with properties
    books = data.get('items', [])       
    books = formatBooks(books, limit)       # array of objects with each object representing a book in the requested genre
    return jsonify({'books': books})


# helper function that formats a given list of books into a specified format
def formatBooks(books, limit):
    result = []
    if books:
        books = books[:int(limit)] if limit < len(books) else books
        for book in books:
            book_id = book.get('id', '')
            book_title = book.get('volumeInfo', {}).get('title', '')
            book_authors = book.get('volumeInfo', {}).get('authors', [])
            book_series = book.get('volumeInfo', {}).get('series', [{}])[0]
            book_publisher = book.get('volumeInfo', {}).get('publisher', '')
            book_published_date = book.get('volumeInfo', {}).get('publishedDate', '')
            book_description = book.get('volumeInfo', {}).get('description', '')
            book_page_count = book.get('volumeInfo', {}).get('pageCount', 0)
            book_categories = book.get('volumeInfo', {}).get('categories', [])
            book_cover = book.get('volumeInfo', {}).get('imageLinks', {}).get('thumbnail', 'https://via.placeholder.com/200x300.png?text=No+Cover')
            book_language = book.get('volumeInfo', {}).get('language', '')
            
            if book_published_date:
                book_published_date = format_date(book_published_date)
            
            # format
            book_data = {
                'id': book_id,
                'title': book_title,
                'authors': book_authors,
                'series': book_series,
                'publisher': book_publisher,
                'description': book_description,
                'pageCount': book_page_count,
                'categories': book_categories,
                'image_url': book_cover,
                'language': book_language
            }
            
            if book_published_date:
                book_data['publishedDate'] = book_published_date
            
            result.append(book_data)
        
    return result

# helper function that returns dates in specified format
def format_date(date_str, format_year_only=False):
    # Parse the input date string
    try:
        date_obj = datetime.strptime(date_str, '%y/%m/%d')
    except ValueError:
        try:
            date_obj = datetime.strptime(date_str, '%Y/%m/%d')
        except ValueError:
            return None

    # Format the date into the desired format based on the flag
    if format_year_only:
        formatted_date = date_obj.strftime('%Y')
    else:
        formatted_date = date_obj.strftime('%B %d, %Y')
    
    return formatted_date
    
    
# fetches a specific number of books from the Google Books API based on a user's search 
@app.route('/book', methods=['GET'])
def getBook():
    search = request.args.get('search')
    limit = request.args.get('limit', 5)
    
    if not search:
        return jsonify({'error': 'Server did not receive a search parameter when contacted at endpoint "/book".'}), 400
    
    try:
        limit = int(limit)
    except ValueError:
        return jsonify({'error': f'Limit parameter {limit} sent to endpoint "/book" is not a valid integer that can be parsed.'}), 400
    
    if isISBN(search):
        book_response = requests.get(f"{GOOGLE_URL}?q=isbn:{search}&fields=items(id,volumeInfo/title,volumeInfo/authors,volumeInfo/publisher,volumeInfo/publishedDate,volumeInfo/description,volumeInfo/pageCount,volumeInfo/categories,volumeInfo/imageLinks/thumbnail, volumeInfo/language)&key={GOOGLE_KEY }")
    else:
        book_response = requests.get(f"{GOOGLE_URL}?q={search}&maxResults={limit}&fields=items(id,volumeInfo/title,volumeInfo/authors,volumeInfo/publisher,volumeInfo/publishedDate,volumeInfo/description,volumeInfo/pageCount,volumeInfo/categories,volumeInfo/imageLinks/thumbnail, volumeInfo/language)&key={GOOGLE_KEY }")
        
    if book_response.status_code != 200:
        return jsonify({'error': f'Failed response from the Google Books API with search {search}'}), book_response.status_code
    
    books = book_response.json().get('items', [])
    books = formatBooks(books, limit)
    
    # respond with an array of book(s)
    return jsonify({'books': books})


# helper function that checks if a user search has a valid format for an ISBN 
def isISBN(search):
    if len(search) != 10 and len(search) != 13:
        return False
    
    # checks if any char is not a num
    if not search.isnumeric():
        return False
    
    return True


# sets up the in-memory cache with a specified number of books for each genre 
@app.route('/cache', methods=['GET'])
async def setup_cache():
    limit = request.args.get('limit', 7)
    try:
        limit = int(limit)
    except ValueError:
        return jsonify({'error': f'Limit parameter {limit} sent to endpoint "/cache" is not a valid integer that can be parsed.'}), 404
    
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
                return jsonify({'error': f'Failed to cache books for {genre} genre due to a failed response from the Open Library API.'}), 400
            
    return jsonify({'message': f"Cache has been successfully set up with {limit} books in every genre."}), 200


# fetches a specified number of books starting at a specific index from the Google Books API
async def fetchBooks(session, genre, limit, offset):
    url = requests.get(f"{GOOGLE_URL}?q=subject:{genre}&maxResults={limit}&startIndex={offset}&fields=items(id,volumeInfo/title,volumeInfo/authors,volumeInfo/publisher,volumeInfo/publishedDate,volumeInfo/description,volumeInfo/pageCount,volumeInfo/categories,volumeInfo/imageLinks/thumbnail, volumeInfo/language)&key={GOOGLE_KEY }")
    async with session.get(url) as response:
        if response.status == 200:
            return await response.json()
        else:
            print(f"Failed to fetch books for {genre} genre to be stored in cache.")
            return None
        

# retrieves a specified number of books for a single genre stored in memory cache 
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
    
    return jsonify({'cachedBooks': cached_books})
        

# fetches 7 more books to be stored in memory cache for a single genre 
@app.route('/update-<genre>-cache', methods=['GET'])
def updateGenreCache(genre):
    if genre not in cache:
        return jsonify({'error': f'{genre} genre does not exist in the cache'}), 404
    
    limit = int(request.args.get('limit', 7))
    
    response = requests.get(f"{GOOGLE_URL}?q=subject:{genre}&maxResults={limit}&startIndex={genre_offset[genre]}&fields=items(id,volumeInfo/title,volumeInfo/authors,volumeInfo/publisher,volumeInfo/publishedDate,volumeInfo/description,volumeInfo/pageCount,volumeInfo/categories,volumeInfo/imageLinks/thumbnail, volumeInfo/language)&key={GOOGLE_KEY }")
    
    # we send back an error if request failed
    if response.status_code != 200:
        return jsonify({'error': f'An error occurred requesting books with subject: {genre}'}), response.status_code
    
    data = response.json()     
    books = data.get('items', [])
    
    if not books:
        return jsonify({'message': f'No new books for the {genre} genre when updating its cache.'}), 200
    
    genre_offset[genre] += limit     
    
    books = formatBooks(books, limit)
    cache[genre].extend(books)
    
    return jsonify({'message': f'Successfully updated the cache for {genre} genre with {limit} more books.'}), 200


# fetches brief description of a single author from Wikipedia API
@app.route('/author', methods=['GET'])
def getAuthorDetails():
    author_name = request.args.get('authorName', '')
    if not author_name:
        return jsonify({'error': 'Author name is required to get their description.'}), 400
    
    # Making a request to Wikipedia API to get the Wikipedia page title for this author
    title_response = requests.get(f"{WIKIPEDIA_URL}?action=query&list=search&srsearch={author_name}&format=json")
    if title_response.status_code != 200:
        return jsonify({"error": f"An unexpected error occurred when fetching page title for {author_name}."}), title_response.status_code
    
    search_results = title_response.json().get('query', {}).get('search', [])
    if not search_results:
        return jsonify({"message": f"No details found for the author {author_name}."}), 200
    
    # Getting the first relevant title from the search results
    page_title = search_results[0].get('title')
    
    # Fetching the introduction and image for the author using the Wikipedia title
    extract_url = f"{WIKIPEDIA_URL}?action=query&prop=extracts|pageimages&exintro=true&explaintext=true&titles={page_title}&pithumbsize=500&format=json"
    
    extract_response = requests.get(extract_url)
    if extract_response.status_code != 200:
        return jsonify({"error": f"An unexpected error occurred when fetching the details for author {author_name}."}), 400
    
    extract_data = extract_response.json()
    
    pages = extract_data.get('query', {}).get('pages', {})
    if not pages:
        return jsonify({'message': f'No pages found for the author {author_name}.'}), 200
    
    # Get the first (and only) page ID
    page_id = list(pages.keys())[0]
    page_data = pages[page_id]
    
    extract = page_data.get('extract', '')
    image_info = page_data.get('thumbnail', {})
    image_url = image_info.get('source', '')
    
    if not extract and not image_url:
        return jsonify({'message': f'No details could be found for the author {author_name}.'}), 200
    
    return jsonify({
        'description': extract,
        'image_url': image_url
    })


# fetches up to 5 similar books for a given book from Tastedive API
@app.route('/similar-books', methods=['POST'])
async def getSimilarBooks():
    book_title = request.json.get('title')
    search_type = request.json.get('type')
    if not book_title or not search_type:
        return jsonify({'error': 'Book title and search type are required to look for similar books.'}), 400
    
    limit = request.json.get('limit', '20')
    try:
        limit = int(limit)
    except ValueError:
        return jsonify({'error': 'Limit provided for similar books is not a valid integer.'}), 400
    
    similar_books_response = requests.get(f'{TASTEDIVE_URL}?q=book:{book_title}&type={search_type}&limit={limit}&k={TASTEDIVE_KEY}')
    if similar_books_response.status_code != 200:
        return jsonify({'error': f'Failed request to retrieve similar books to {book_title}.'}), 400
    
    similar_books = similar_books_response.json().get('similar', {}).get('results', [])
    if not similar_books:
        return jsonify({'message': f'No similar books were found for {book_title}.'}), 200
    
    book_names = [book.get('name') for book in similar_books if book.get('name')]
    
    async with aiohttp.ClientSession() as session:
        tasks = [fetchBook(session, book) for book in book_names]
        book_responses = await asyncio.gather(*tasks)
        
        errors = []
        similar_books = []
        for response in book_responses:
            if response:
                books = response.get('items', [])
                if not books:
                    print("No data could be found for a similar book.")
                    continue
                book = formatBooks(books, 1)     # returns an array of 1 book object
                book = book[0]
                similar_books.append(book)
            else:
                errors.append({'error': f'Unexpected error occurred when fetching details about a similar book to {book_title}'})
        
        return jsonify({'similarBooks': similar_books, 'errors': errors}), 200
            

# fetches details about one book from Google Books API
async def fetchBook(session, title):
    url = f"{GOOGLE_URL}?q=intitle:{title}&maxResults=1&fields=items(id,volumeInfo/title,volumeInfo/authors,volumeInfo/publisher,volumeInfo/publishedDate,volumeInfo/description,volumeInfo/pageCount,volumeInfo/categories,volumeInfo/imageLinks/thumbnail, volumeInfo/language)&key={GOOGLE_KEY}"    
    async with session.get(url) as book_response:
        if book_response.status == 200:
            return await book_response.json()
        else:
            print(f"Unexpected error occurred when fetching details about {title}.")
            return None

if __name__ == '__main__':
    app.run(debug=True)
    
