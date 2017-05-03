#!/usr/bin/python3

import tornado.ioloop
import tornado.web
import sys
from user import user
from location import location
import logging
from LoginError import *
from NoMessagesError import *
import simplejson, json
from ExpiredMessageError import ExpiredMessageError
import traceback

PORT=31000


class RegisterHandler(tornado.web.RequestHandler):
    def get(self):
        global users
        for u in users:
            if u.username == self.get_argument("username"):
                self.write(json.dumps({'type': 'register','response': 'failure','reason':'user_exists'}\
                ,indent=4,separators=(',', ': ')))
                return ;
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
        self.write(json.dumps({'type': 'loginUser','response': 'failure','reason':'icorrect_username_password'}\
             ,indent=4,separators=(',', ': ')))

class LogoutHandler(tornado.web.RequestHandler):
    def get(self):
        for u in users:
            try:
                if(u.isValidToken(self.get_argument("token"))):
                    u.remove_token()
                    self.write(json.dumps({'type': 'logout','response': 'success'}\
                    ,indent=4,separators=(',', ': ')))
                    break
                else:
                    self.write(json.dumps({'type': 'logoutUser','response': 'failure'}\
                 ,indent=4,separators=(',', ': ')))
            except AttributeError:
                continue


class ListLocationsHandler(tornado.web.RequestHandler):
    def get(self):
        try:
            getUserFromToken(self.get_argument("token"))
            global locations
            finalJson={'type':'listLocations','response':'success'}
            locationsList=[]
            for l in locations:
                locationsList+=[{'name':l.getName(),'references':l.getJson()},]
                #self.write(l.getName()+"\n")
            finalJson['locations']=locationsList
            self.write(json.dumps(finalJson ,indent=4,separators=(',', ': ')))
        except LoginError:
            self.write(json.dumps({'type': 'listLocations','response': 'failure','reason':'invalid_token'}\
             ,indent=4,separators=(',', ': ')))
        except :
            self.write(json.dumps({'type': 'listLocations','response': 'failure'}\
             ,indent=4,separators=(',', ': ')))

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
                elif i==len(locations)-1:
                    self.write(json.dumps({'type': 'removeLocation','response': 'failure','reason':'no_such_location'}\
                    ,indent=4,separators=(',', ': ')))
                    return;
            self.write(json.dumps({'type': 'removeLocation','response': 'success'}\
            ,indent=4,separators=(',', ': ')))
        except LoginError:
            self.write(json.dumps({'type': 'removeLocation','response': 'failure','reason':'invalid_token'}\
             ,indent=4,separators=(',', ': ')))
        except: 
            self.write(json.dumps({'type': 'removeLocation','response': 'failure'}\
             ,indent=4,separators=(',', ': ')))
class AddLocationHandler(tornado.web.RequestHandler):
    def get(self):
        try:
            getUserFromToken(self.get_argument("token"))
            global locations
            name=self.get_argument("name")
            for l in locations:
                if l.name==name:
                    self.write(json.dumps({'type': 'addLocation','response': 'failure','reason':'location_exists'}\
                     ,indent=4,separators=(',', ': '))) 
                    return;
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
            self.write(json.dumps({'type': 'addLocation','response': 'failure','reason':'invalid_token'}\
             ,indent=4,separators=(',', ': ')))
        except :
            self.write(json.dumps({'type': 'addLocation','response': 'failure'}\
             ,indent=4,separators=(',', ': ')))

class PostMessageHandler(tornado.web.RequestHandler):
    def get(self):
        try:
            author=getUserFromToken(self.get_argument("token"))
            global locations
            for l in locations:
                if self.get_argument("location")==l.getName():
                    l.postMessage(author,self.get_argument("message"),self.get_argument("title"),self.get_argument("deliveryMode"),self.get_argument("topics"),self.get_argument("endTime"))
                    self.write(json.dumps({'type': 'postMessage','response': 'success'}\
                    ,indent=4,separators=(',', ': ')))
                    return;
            self.write(json.dumps({'type': 'postMessage','response': 'failure','reason':'no_such_location'}\
             ,indent=4,separators=(',', ': ')))
        except LoginError:
            self.write(json.dumps({'type': 'postMessage','response': 'failure','reason':'invalid_token'}\
             ,indent=4,separators=(',', ': ')))
        except Exception as e:
            traceback.print_exc()
            self.write(json.dumps({'type': 'postMessage','response': 'failure'}\
             ,indent=4,separators=(',', ': ')))

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
                    return;
            self.write(json.dumps({'type': 'unpostMessage','response': 'failure','reason':'no_such_location'}\
             ,indent=4,separators=(',', ': ')))       
        except LoginError:
            self.write(json.dumps({'type': 'unpostMessage','response': 'failure','reason':'invalid_token'}\
             ,indent=4,separators=(',', ': ')))
        except :
            self.write(json.dumps({'type': 'unpostMessage','response': 'failure'}\
             ,indent=4,separators=(',', ': ')))



class ListMessagesHandler(tornado.web.RequestHandler):
    def get(self):
        try:
            try:

                u=getUserFromToken(self.get_argument("token"))
            except LoginError:
                self.write(json.dumps({'type': 'listMessages','response': 'failure','reason':'incorrect_token'}\
                 ,indent=4,separators=(',', ': ')))
            global locations
            finalJson={'type':'listMessages','response':'success'}
            try:
                locationName=self.get_argument("location")
            except tornado.web.MissingArgumentError:
                    for l in locations:
                        try:
                            messages=[]
                            for m in l.getMessages().values():
                                try:
                                    if m.getAuthor() == u:
                                    
                                        messages+=[m.getJson(),]
                                except ExpiredMessageError:
                                    continue;
                                finalJson['messages']=messages
                        except NoMessagesError:
                            continue;
                    if messages =={}:
                        self.write(json.dumps({'type': 'listMessages','response': 'failure','reason':'no_messages_found'}\
                    ,indent=4,separators=(',', ': ')))
                        return;
                    self.write(json.dumps(finalJson ,indent=4,separators=(',', ': ')))
                    return;

            try:
                for l in locations:
                    if l.getName()==locationName:
                        messages=[]
                        for m in l.getMessages().values():
                            try:
                                messages+=[m.getJson(),]
                            except ExpiredMessageError:
                                continue;
                        finalJson['messages']=messages
                        break
                self.write(json.dumps(finalJson ,indent=4,separators=(',', ': ')))
            except LoginError:
                self.write(json.dumps({'type': 'listMessages','response': 'failure','reason':'incorrect_token'}\
                 ,indent=4,separators=(',', ': ')))
            except NoMessagesError:
                self.write(json.dumps({'type': 'listMessages','response': 'failure','reason':'no_messages_found'}\
                 ,indent=4,separators=(',', ': ')))
        except :
            traceback.print_exc()
            self.write(json.dumps({'type': 'listMessages','response': 'failure'}\
             ,indent=4,separators=(',', ': ')))


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
                    return;
            # The server must explicitly say which\
            #message is required
            self.write(json.dumps({'type': 'readMessage','response': 'failure','reason':'no_such_location'}\
             ,indent=4,separators=(',', ': ')))   
        except KeyError:
            self.write(json.dumps({'type': 'readMessage','response': 'failure','reason':'no_such_message'}\
             ,indent=4,separators=(',', ': ')))
        except LoginError:
            self.write(json.dumps({'type': 'readMessage','response': 'failure','reason':'incorrect_token'}\
             ,indent=4,separators=(',', ': ')))
        except :
            self.write(json.dumps({'type': 'readMessage','response': 'failure'}\
             ,indent=4,separators=(',', ': ')))

class AddKeyHandler(tornado.web.RequestHandler):
    def get(self):
        try:
            user=getUserFromToken(self.get_argument("token"))
            user.addKey(self.get_argument("key"),self.get_argument("value"))
            self.write(json.dumps({'type': 'addKey','response': 'success'}\
             ,indent=4,separators=(',', ': ')))
        except LoginError:
            self.write(json.dumps({'type': 'addKey','response': 'failure','reason':'incorrect_token'}\
             ,indent=4,separators=(',', ': ')))
        except :
            self.write(json.dumps({'type': 'addKey','response': 'failure'}\
             ,indent=4,separators=(',', ': ')))

class ListKeysHandler(tornado.web.RequestHandler):
    def get(self):
        try:
            user=getUserFromToken(self.get_argument("token"))
            finalJson={'type': 'listKeys','response': 'success'}
            keys=[]
            for key,value in user.getKeys().items():
                keys+=[{'key':key,'value':value},]
            finalJson['keys']=keys
            self.write(json.dumps(finalJson,indent=4,separators=(',', ': ')))
        except LoginError:
            self.write(json.dumps({'type': 'listKeys','response': 'failure','reason':'incorrect_token'}\
             ,indent=4,separators=(',', ': ')))
        except :
            self.write(json.dumps({'type': 'listKeys','response': 'failure'}\
             ,indent=4,separators=(',', ': ')))

def parseIDs(ids):
    return ids.split(",")

def getUserFromToken(token):
    global users
    for u in users:
        try:
            if u.isValidToken(token):
                return u
        except AttributeError:
            continue
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
    global messageId
    messageId=0
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
