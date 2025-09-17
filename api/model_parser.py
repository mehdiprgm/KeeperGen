
from db import User

def is_user_valid(data: dict):
    required_fields = [
        'username', 'password', 'securityCode',
        'phoneNumber', 'imagePath', 'loginDateTime', 'isLocked', 'createDate'
    ]

    for field in required_fields:
        if field not in data:
            return False

    return True

def create_user(data: dict):
    user = User(
        id=data['id'],
        username=data['username'],
        password=data['password'],
        securityCode=data['securityCode'],
        phoneNumber=data['phoneNumber'],
        imagePath=data['imagePath'],
        loginDateTime=data['loginDateTime'],
        isLocked=data['isLocked'],
        createDate=data['createDate']
    )

    return user