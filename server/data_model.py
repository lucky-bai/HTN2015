from pymongo import MongoClient

client = MongoClient()
db = client.htn2015
users_collection = db.users
timestamps_collection = db.timestamps


class Users(object):

    @staticmethod
    def create(profile):
        """
        Create a user.
        :param profile: {
            username: string,
            full_name: string
        }
        :returns A tuple of a boolean and a string. The boolean is True if user is created, False otherwise.
        The string contains the message.
        """
        for key in ('username', 'full_name'):
            if key not in profile:
                return False, '{} must be specified.'.format(key)

        user = users_collection.find_one({'username': profile['username']})
        if user:
            return False, 'username {} already exists.'.format(profile['username'])

        users_collection.insert_one(profile)
        return True, ''

    @staticmethod
    def get(username):
        """
        Get a user by username.
        :param username: username.
        :return: A dictionary containing the user information. None if not found.
        """
        user = users_collection.find_one({'username': username})
        if user:
            user.pop('_id')
        return user


class Timestamps(object):

    @staticmethod
    def upload(username, timestamps):
        """
        Upload timestamps for a user.
        :param username: username.
        :param timestamps: A list of dictionaries, where each dictionary is a timestamp.
        {
            'timestamp': unix timestamp as integer,
            'source': 'chrome' or 'android',
            'event_type': e.g. 'foregound' or 'background' for 'android',
            'subject': webpage url for chrome events, class name for android events
        }
        :return: A tuple of a boolean and a string. The boolean is True if timestamps are created, False otherwise.
        The string contains the message.
        """
        user = users_collection.find_one({'username': username})
        if not user:
            return False, 'username {} does not exists.'.format(username)

        for timestamp in timestamps:
            timestamp['username'] = username

        timestamps_collection.insert_many(timestamps)

        return True, ''

    @staticmethod
    def get_timestamps(username):
        """
        Get timestamps for a user.
        :param username: username.
        :return: A list of dictionaries, denoting timestamps.
        """
        user = users_collection.find_one({'username': username})
        if not user:
            return None

        timestamps = timestamps_collection.find({'username': username})
        timestamps = list(timestamps)
        for timestamp in timestamps:
            timestamp.pop('_id')

        return timestamps
