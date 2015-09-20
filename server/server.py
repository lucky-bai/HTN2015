from flask import Flask, request, jsonify, render_template, \
    send_from_directory

from data_model import Users, Timestamps
from cross_origin import crossdomain
from sleep_heuristics import compute_sleep_time

app = Flask(__name__)


@app.route('/user', methods=['POST'])
@crossdomain(origin='*')
def create_user():
    user_info = request.get_json(force=True)
    status, message = Users.create(user_info)
    return_dict = {
        'created': status
    }
    if status:
        http_status = 200
        return_dict['username'] = user_info['username']
        return_dict['full_name'] = user_info['full_name']
    else:
        http_status = 400

    return jsonify(return_dict), http_status


@app.route('/user/<username>', methods=['GET'])
@crossdomain(origin='*')
def get_user(username):
    user_info = Users.get(username)
    http_status = 200 if user_info else 404
    if user_info is None:
        user_info = {}
    return jsonify(user_info), http_status


@app.route('/user/<username>/timestamps', methods=['POST'])
@crossdomain(origin='*')
def upload_timestamps(username):
    timestamps = request.get_json(force=True)
    status, message = Timestamps.upload(username, timestamps)
    http_status = 200 if status else 400
    return message, http_status


@app.route('/user/<username>/timestamps', methods=['GET'])
@crossdomain(origin='*')
def get_timestamps(username):
    timestamps = Timestamps.get_timestamps(username)
    http_status = 200 if timestamps else 404
    if timestamps is None:
        timestamps = []
    return jsonify({'timestamps': timestamps}), http_status

@app.route('/user/<username>/sleep', methods=['GET'])
@crossdomain(origin='*')
def get_sleep(username):
    timestamps = Timestamps.get_timestamps(username)
    http_status = 200 if timestamps else 404
    if timestamps is None:
        return jsonify({'timestamps': []}), http_status

    timestamps_list = [ts['timestamp'] for ts in timestamps]
    sleep = compute_sleep_time(timestamps_list)

    return jsonify({'sleep': sleep}), http_status

@app.route('/js/<path:path>')
@crossdomain(origin='*')
def send_js(path):
    return send_from_directory('templates/js', path)


@app.route('/css/<path:path>')
@crossdomain(origin='*')
def send_css(path):
    return send_from_directory('templates/css', path)


@app.route('/img/<path:path>')
@crossdomain(origin='*')
def send_img(path):
    return send_from_directory('templates/img', path)


@app.route('/')
@crossdomain(origin='*')
def root():
    return render_template('index.html')

@app.route('/graphs')
@crossdomain(origin='*')
def render_graphs():
    return render_template('graphs.html')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=80, debug=True)
