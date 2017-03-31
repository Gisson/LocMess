import hashlib
import uuid
import logging
import datetime
import base64
import logging

class user:

    def __init__(self,username,password):
        m=hashlib.md5()
        m.update(password.encode('utf-8'))
        self.passhash=base64.b64encode(m.digest())
        self.username=username
        self.keys={"bla":"blabla"}

    def add_token(self):
        uid=uuid.uuid4()
        self.token=uid
        return uid

    def remove_token(self):
        self.token=False

    def isValidToken(self,token):
        logging.debug("Received="+token+" vs "+str(self.token))
        if str(token) == str(self.token):
            return True
        else:
            return False
    def getUsername(self):
        return self.username
    def getPassword(self):
        return self.passhash
    def isPasswordCorrect(self,password):
        m=hashlib.md5()
        m.update(password.encode('utf-8'))
        if(base64.b64encode(m.digest())==self.passhash):
            return True
        else:
            return False
    def addKey(self,key,value):
        if key not in self.keys:
            self.keys[key]=value

    def getKeys(self):
        return self.keys

    def __str__(self):
        return "Username: "+self.username
