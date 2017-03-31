#!/usr/bin/python3

import tornado.ioloop
import tornado.web
import sys
from user import user
from location import location
import logging
from LoginError import *
import simplejson, json

PORT=31000


class RegisterHandler(tornado.web.RequestHandler):
    def get(self):
        global users
        newuser=user(self.get_argument("username"),self.get_argument("password"))
        users+=[newuser,]
        self.write(json.dumps({'type': 'register','response': 'success'}\
        ,indent=4,separators=(',', ': ')))
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
                self.write(json.dumps({'type': 'login','response': 'success','token': str(u.add_token())}\
                ,indent=4,separators=(',', ': ')))
                #self.write(str(u.add_token()))
                return;
        self.write("Nope")

class LogoutHandler(tornado.web.RequestHandler):
    def get(self):
        for u in users:
            if(u.isValidToken(self.get_argument("token"))):
                u.remove_token()
                self.write(json.dumps({'type': 'logout','response': 'success'}\
                ,indent=4,separators=(',', ': ')))
                break
            else:
                self.write("Nope")


class ListLocationsHandler(tornado.web.RequestHandler):
    def get(self):
        try:
            getUserFromToken(self.get_argument("token"))
            global locations
            finalJson={'type':'listLocations','response':'success'}
            locationsDict={}
            for l in locations:
                locationsDict[l.getName()]=l.getJson()
                #self.write(l.getName()+"\n")
            finalJson['locations']=locationsDict
            self.write(json.dumps(finalJson ,indent=4,separators=(',', ': ')))
        except LoginError:
            self.write("Nope")

class RemoveLocationHandler(tornado.web.RequestHandler):
    def get(self):
        try:
            getUserFromToken(self.get_argument("token"))
            global locations
            locationName=getUserFromToken(self.get_argument("location"))
            for i in range(len(locations)):
                if locations[i].name==locationName:
                    if i==len(locations)-1:
                        locations.pop()
                    else:
                        locations=locations[0:i-1]+locations[i+1:len(locations)]
                    break
            self.write(json.dumps({'type': 'removeLocation','response': 'success'}\
            ,indent=4,separators=(',', ': ')))
        except LoginError:
            self.write("Nope")

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
                    self.write(json.dumps({'type': 'postMessage','response': 'success'}\
                    ,indent=4,separators=(',', ': ')))
                    break
        except LoginError:
            self.write("Nope")

class UnpostMessageHandler(tornado.web.RequestHandler):
    def get(self):
        try:
            author=getUserFromToken(self.get_argument("token"))
            global locations
            for l in locations:
                if self.get_argument("location")==l.getName():
                    l.unpostMessage(author,self.get_argument("messageId"))
                    self.write(json.dumps({'type': 'unpostMessage','response': 'success'}\
                    ,indent=4,separators=(',', ': ')))
                    break
        except LoginError:
            self.write("Nope")



class ListMessagesHandler(tornado.web.RequestHandler):
    def get(self):
        try:
            getUserFromToken(self.get_argument("token"))
            global locations
            finalJson={'type':'listMessages','response':'success'}
            locationName=self.get_argument("location")
            for l in locations:
                if l.getName()==locationName:
                    messages={}
                    for m in l.getMessages().values():
                        messages[m.get_id()]=m.getJson()
                    finalJson['messages']=messages
                    break
            self.write(json.dumps(finalJson ,indent=4,separators=(',', ': ')))
        except LoginError:
            self.write("Nope")

##################################################
class readMessageHandler(tornado.web.RequestHandler):
    def get(self):
        try:
            user=getUserFromToken(self.get_argument("token"))
            global locations
            for l in locations:
                if self.get_argument("location")==l.getName():
                    self.write(json.dumps({'type': 'readMessage','response': 'success',\
                    'message':l.getMessage(self.get_argument("messageId")).getJson()}\
                    ,indent=4,separators=(',', ': ')))
            # The server must explicitly say which\
            #message is required
        except LoginError:
            self.write("Nope")

class AddKeyHandler(tornado.web.RequestHandler):
    def get(self):
        try:
            user=getUserFromToken(self.get_argument("token"))
            user.addKey(self.get_argument("key"),self.get_argument("value"))
        except LoginError:
            self.write("Nope")

class ListKeysHandler(tornado.web.RequestHandler):
    def get(self):
        try:
            user=getUserFromToken(self.get_argument("token"))
            finalJson={'type': 'listKeys','response': 'success'}
            keys=[]
            for key,value in user.getKeys():
                keys+=[{'key':key,'value':value},]
            finalJson['keys']=keys
            self.write(json.dumps(finalJson,indent=4,separators=(',', ': ')))
        except LoginError:
            self.write("Nope")

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
    (r"/removeLocation", RemoveLocationHandler),
    (r"/postMessage", PostMessageHandler),
    (r"/unpostMessage", UnpostMessageHandler),
    (r"/readMessage", readMessageHandler),
    (r"/listMessages", ListMessagesHandler),
    (r"/addKey", AddKeyHandler),
    (r"/listKeys", ListKeysHandler),
    ])

if __name__=="__main__":
    app=make_app()
    logging.basicConfig(level=logging.DEBUG)
    logging.debug("Starting server...")
    global users
    newuser=user("bla","bla")
    users=[newuser,]
    #logging.debug(str(newuser.add_token()))
    global locations
    locations=[location("RNL")]
    if len(sys.argv)>1 :
        PORT=int(sys.argv[1])
    app.listen(PORT)
    tornado.ioloop.IOLoop.current().start()