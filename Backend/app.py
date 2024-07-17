from flask import Flask, jsonify, request
from flask_cors import CORS # type: ignore
import requests

app = Flask(__name__)
CORS(app)

SEARCH_URL = "https://openlibrary.org/search.json"
SUBJECTS_URL = "http://openlibrary.org/subjects"        # add /subject_name.json  to retrieve data about books of a specific genre
COVERS_URL = "https://covers.openlibrary.org/b/id"      # add the cover id, the size and then .jpg to retrieve the image url for a book's cover


# SETTING UP THE ENDPOINTS FOR THE BACKEND

@app.route('/romance-books', methods=['GET'])
def getRomanceBooks():
    return getBooksByGenre('romance')
    
@app.route('/fiction-books', methods=['GET'])
def getFictionBooks():
    return getBooksByGenre('fiction')

@app.route('/thriller-books', methods=['GET'])
def getThrillerBooks():
    return getBooksByGenre('thriller')

@app.route('/action-books', methods=['GET'])
def getActionBooks():
    return getBooksByGenre('action')

@app.route('/mystery-books', methods=['GET'])
def getMysteryBooks():
    return getBooksByGenre('mystery')

@app.route('/history-books', methods=['GET'])
def getHistoryBooks():
    return getBooksByGenre('history')

@app.route('/scifi-books', methods=['GET'])
def getScifiBooks():
    return getBooksByGenre('science_fiction')


# helper function
def getBooksByGenre(genre):
    limit = request.args.get('limit', 20)
    
    # requesting the API for twenty romance books
    response = requests.get(f"{SUBJECTS_URL}/{genre}.json?limit={limit}")
    
    # we send back an error if request failed
    if response.status_code != 200:
        return jsonify({'error': 'An error occurred requesting books with subject: love'}), response.status_code
    
    data = response.json()      # a python dictionary of 5 key value pairs
    works = data.get('works', [])       # gets a python list of dictionaries of the 20 books, each dictionary storing metadata about a specific book
    
    # list will store our response back to the client 
    # list contains the same objects stored in works but without the unneeded properties
    books = formattedBooks(works)
    
            
    return jsonify(books)

# helper function
def formattedBooks(works):
    result = []
    if works:
        for work in works:
            # extracting the book name and cover id from metadata dictionary of each book
            name, cover_id, author = work.get('title', ''), work.get('cover_id', ''), work.get('authors')[0].get('name')
            
            # we get a jpg image url for each book
            image_url = f'{COVERS_URL}/{cover_id}-S.jpg' if cover_id else None
                
            result.append(
                {
                'name': name, 
                'image_url': image_url,
                'author': author
                }
            )
            
    return result
    
    
    

    
