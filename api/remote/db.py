# connect and access database information
import base64

from sqlalchemy import create_engine, Column, Integer, String, LargeBinary, Boolean
from sqlalchemy.orm import relationship, declarative_base, sessionmaker

DATABASE_URL = "mysql+pymysql://root:FS7tWkFMukn2boGK4POHqpP2@aberama.iran.liara.ir:32734/laughing_tharp"

engine = create_engine(DATABASE_URL, echo=False)
Base = declarative_base()
Session = sessionmaker(bind=engine)
session = Session()


class User(Base):
    __tablename__ = "Users"

    id = Column(Integer, primary_key=True)

    username = Column(String)
    password = Column(String)
    securityCode = Column(String)
    phoneNumber = Column(String)

    imagePath = Column(String)
    loginDateTime = Column(String)

    isLocked = Column(Boolean)
    createDate = Column(String)

    def to_dict(self):
        return {
            "id": self.id,
            "username": self.username,
            "password": self.password,
            "securityCode": self.securityCode,
            "phoneNumber": self.phoneNumber,
            "imagePath": self.imagePath,
            "loginDateTime": self.loginDateTime,
            "isLocked": self.isLocked,
            "createDate": self.createDate
        }


class UserRepository:
    @staticmethod
    def add(user: User):
        session.add(user)
        session.commit()

        return True

    @staticmethod
    def get(username):
        user = session.query(User).filter_by(username=username).first()
        return user

    @staticmethod
    def get_all():
        return session.query(User).all()

    @staticmethod
    def update(user: User):
        existing = session.get(User, user.id)

        if not existing:
            return None

        # Update fields one by one
        existing.username = user.username
        existing.password = user.password
        existing.securityCode = user.securityCode
        existing.phoneNumber = user.phoneNumber
        existing.imagePath = user.imagePath
        existing.loginDateTime = user.loginDateTime
        existing.isLocked = user.isLocked
        existing.createDate = user.createDate

        session.commit()
        session.refresh(existing)

        return existing


Base.metadata.create_all(engine)
