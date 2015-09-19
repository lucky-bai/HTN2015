import json
from flask import Flask, request

from data_model import Users, Timestamps

app = Flask(__name__)

@app.route('/')
def hello_world():
    return 'Hello World!'


@app.route('/user', methods=['POST'])
def create_user():
    user_info = request.get_json(force=True)
    status, message = Users.create(user_info)
    # TODO Return different response based on status and message
    return ''


@app.route('/user/<username>/timestamps')
def upload_timestamps(username):
    pass


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=80, debug=True)
