from message import message

class location:

    def __init__(self,name,bssids=[],ssids=[],latitude=0,longitude=0,radius=0):
        self.name=name
        self.bssids=bssids
        self.ssids=ssids
        self.latitude=latitude
        self.longitude=longitude
        self.radius=radius
        self.messages={}
        self.messageIds=0

    def getName(self):
        return self.name

    def getBssids(self):
        if not self.bssids:
            raise ValueError
        else:
            return self.bssids

    def getSsids(self):
        if not self.ssids:
            raise ValueError
        else:
            return self.ssids
    def getCoordinates(self):
        if self.latitude!=0 or self.longitude!=0:
            raise ValueError
        else:
            return (self.latitude,self.longitude,self.radius)
    def getMessages(self):
        if not self.messages:
            raise ValueError
        else:
            return self.messages
    #TODO
    def unpostMessage(self,fullId):
        del self.messages[fullId]
    def getName(self):
        return self.name

    def postMessage(self,author,content):
        mId=author.getUsername()+"-"+self.name+"-"+str(self.messageIds)
        self.messages[mId]=message(author,self,content)
        self.messageIds+=1
        return mId

    def getMessage(self,messageId):
        return self.messages[messageId]

    def __cmp__(self,location):
        return True if self.name==location.name else False

    def __str__(self):
        rep="Location name: "+self.name+"\n"
        for key,message in self.messages.items():
            rep+="Message: "+str(message)+" with id="+key+"\n"
        return rep
