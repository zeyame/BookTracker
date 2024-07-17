from flask import Flask, jsonify, request
from flask_cors import CORS # type: ignore
import requests

app = Flask(__name__)
CORS(app)

SEARCH_URL = "https://openlibrary.org/search.json"
SUBJECTS_URL = "http://openlibrary.org/subjects"        # add /subject_name.json  to retrieve data about books of a specific genre

@app.route('/default-books', methods=['GET'])
def defaultBooks():
    limit = request.args.get('limit', 20)
    
    response = requests.get(f"{SUBJECTS_URL}/love.json?limit={limit}")
    
    if response.status_code != 200:
        return jsonify({'error': 'An error occurred requesting books with subject: love'}), response.status_code
    
    return jsonify(response.json())


    
    

    

