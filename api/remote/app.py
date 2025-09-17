# routes for client requests and send responses

OK = 200
NOT_FOUND = 404
INVALID_REQUEST = 400
CONFLICT = 409
SERVER_ERROR = 500

# 200 -> OK
# 404 -> Not found
# 400 -> Request invalid
# 409 -> Conflict
# 500 -> Server error

from flask import Flask, request, jsonify
from db import User, UserRepository
from model_parser import *

app = Flask(__name__)
app.config['SEND_FILE_MAX_AGE_DEFAULT'] = 0


@app.route('/')
def home():
    return "This is Keepergen API"


@app.post("/users/add")
def add_user():
    data = request.json

    if is_user_valid(data):
        username = data['username']
        user = UserRepository.get(username)

        # no user exists in the database
        if user is None:
            new_user = create_user(data)

            if UserRepository.add(new_user):
                return jsonify({"message": "User added successfully"}), OK

            return jsonify({"message": "User could not be added"}), SERVER_ERROR
        else:
            return jsonify({"message": "User already exists"}), CONFLICT

    return jsonify({"message": "The request body is not valid."}), INVALID_REQUEST


@app.get("/users/get/<string:username>")
def get_user(username):
    user = UserRepository.get(username)
    if user is None:
        return jsonify({"message": "User could not be found"}), NOT_FOUND

    return jsonify(user.to_dict()), OK


@app.get("/users")
def get_users():
    users = UserRepository.get_all()
    users_dict = [user.to_dict() for user in users]

    return jsonify(users_dict)


@app.put("/users/update")
def update_user():
    data = request.json

    if is_user_valid(data):
        user = create_user(data)
        updated_user = UserRepository.update(user)

        if updated_user:
            return jsonify({"message": "User updated successfully"}), OK
        else:
            return jsonify({"message": "User could not be updated"}), NOT_FOUND

    return jsonify({"message": "The request body is not valid."}), INVALID_REQUEST


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
