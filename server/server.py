from flask import Flask, request, jsonify, render_template, \
    send_from_directory

from data_model import Users, Timestamps

app = Flask(__name__)


@app.route('/user', methods=['POST'])
def create_user():
    user_info = request.get_json(force=True)
    status, message = Users.create(user_info)
    http_status = 200 if status else 400
    return message, http_status


@app.route('/user/<username>', methods=['GET'])
def get_user(username):
    user_info = Users.get(username)
    http_status = 200 if user_info else 404
    if user_info is None:
        user_info = {}
    return jsonify(user_info), http_status


@app.route('/user/<username>/timestamps', methods=['POST'])
def upload_timestamps(username):
    timestamps = request.get_json(force=True)
    status, message = Timestamps.upload(username, timestamps)
    http_status = 200 if status else 400
    return message, http_status


@app.route('/user/<username>/timestamps', methods=['GET'])
def get_timestamps(username):
    timestamps = Timestamps.get_timestamps(username)
    http_status = 200 if timestamps else 404
    if timestamps is None:
        timestamps = []
    return jsonify({'timestamps': timestamps}), http_status

@app.route('/js/<path:path>')
def send_js(path):
    return send_from_directory('templates/js', path)

@app.route('/css/<path:path>')
def send_css(path):
    return send_from_directory('templates/css', path)

@app.route('/img/<path:path>')
def send_img(path):
    return send_from_directory('templates/img', path)


@app.route('/')
def root():
    return render_template('index.html')

@app.route('/graphs')
def render_graphs():
    #timestamps = Timestamps.get_timestamps('')
    #timestamps_list = [ts['timestamp'] for ts in timestamps]
    return render_template('graphs.html')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=80, debug=True)
