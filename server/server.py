import json
from flask import Flask, request, jsonify

from data_model import Users, Timestamps

app = Flask(__name__)

@app.route('/')
def hello_world():
    return 'Hello World!'


@app.route('/user', methods=['POST'])
def create_user():
    user_info = request.get_json(force=True)
    status, message = Users.create(user_info)
    http_status = 200 if status else 400
    return message, http_status


@app.route('/user/<username>', methods=['GET'])
def get_user(username):
    user_info = Users.get(username)
    if user_info is None:
        user_info = {}
    http_status = 200 if user_info else 404
    return jsonify(user_info), http_status


@app.route('/user/<username>/timestamps')
def upload_timestamps(username):
    pass


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=80, debug=True)
