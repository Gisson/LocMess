#!/usr/bin/python3

import tornado.ioloop
import tornado.web
import sys
from user import user
from location import location
import logging
from LoginError import *

PORT=31000


class RegisterHandler(tornado.web.RequestHandler):
    def get(self):
        global users
        newuser=user(self.get_argument("username"),self.get_argument("password"))
        users+=[newuser,]
        logging.debug("New user username="+newuser.getUsername()\
        +" and password="+str(newuser.getPassword()))

class LoginHandler(tornado.web.RequestHandler):
    def get(self):
        global users
        #self.get_argument("username"),self.get_argument("password")
        for u in users:
            logging.debug("Testing username="+u.getUsername()\
            +" vs "+self.get_argument("username")+" and password="+str(u.getPassword())\
            +" vs "+self.get_argument("password"))
            if(u.getUsername()==self.get_argument("username") and \
            u.isPasswordCorrect(self.get_argument("password"))):
                self.write(str(u.add_token()))
                break
            else:
                self.write("Nope")

class LogoutHandler(tornado.web.RequestHandler):
    def get(self):
        for u in users:
            if(u.isValidToken(self.get_argument("token"))):
                u.remove_token()
                self.write("Successful logout")
                break
            else:
                self.write("Nope")


class ListLocationsHandler(tornado.web.RequestHandler):
    def get(self):
        try:
            getUserFromToken(self.get_argument("token"))
            global locations
            for l in locations:
                self.write(l.getName()+"\n")
        except LoginError:
            self.write("Nope")
        #global locations

class AddLocationHandler(tornado.web.RequestHandler):
    def get(self):
        try:
            getUserFromToken(self.get_argument("token"))
            global locations
            name=self.get_argument("name")
            try:
                lat=self.get_argument("latitude")
                log=self.get_argument("longitude")
                rad=self.get_argument("radius")
                locations+=[location(name=name,latitude=lat,longitude=log,radius=rad),]
            except tornado.web.MissingArgumentError:
                try:
                    bssids=parseIds(self.get_argument("bssids"))
                    ssids=self.get_argument("ssid")
                    locations+=[location(name=name,bssids=bssids,ssids=ssids),]
                except tornado.web.MissingArgumentError:
                    ssids=self.get_argument("ssid")
                    locations+=[location(name=name,ssids=ssids),]

        except LoginError:
            self.write("Nope")

class PostMessageHandler(tornado.web.RequestHandler):
    def get(self):
        try:
            author=getUserFromToken(self.get_argument("token"))
            global locations
            for l in locations:
                if self.get_argument("location")==l.getName():
                    l.postMessage(author,self.get_argument("message"))
        except LoginError:
            self.write("Nope")
        #global locations


class ListMessagesHandler(tornado.web.RequestHandler):
    def get(self):
        try:
            getUserFromToken(self.get_argument("token"))
            global locations
            for l in locations:
                self.write(l.__str__())
        except LoginError:
            self.write("Nope")
        #global locations

def parseIDs(ids):
    return ids.split(",")

def getUserFromToken(token):
    global users
    for u in users:
        if u.isValidToken(token):
            return u
    raise LoginError

def make_app():
    return tornado.web.Application([
    (r"/registerUser",RegisterHandler),
    (r"/loginUser", LoginHandler),
    (r"/logoutUser", LogoutHandler),
    (r"/listLocations", ListLocationsHandler),
    (r"/addLocation", AddLocationHandler),
#TODO    (r"/removeLocation", RemoveLocationHandler),
    (r"/postMessage", PostMessageHandler),
#TODO    (r"/unpostMessage", UnpostMessageHandler),
#TODO    (r"/readMessage", ListKeysHandler),
    (r"/listMessages", ListMessagesHandler),
#TODO    (r"/addKey", AddKeyHandler),
#TODO    (r"/listKeys", ListKeysHandler),
    ])

if __name__=="__main__":
    app=make_app()
    logging.basicConfig(level=logging.DEBUG)
    logging.debug("Starting server...")
    global users
    users=[user("bla","bla")]
    global locations
    locations=[location("RNL")]
    if len(sys.argv)>1 :
        PORT=int(sys.argv[1])
    app.listen(PORT)
    tornado.ioloop.IOLoop.current().start()
