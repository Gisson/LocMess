

class location:

    def __init__(self,name,bssids=[],ssids=[],latitude=0,longitude=0,radius=0):
        self.name=name
        self.bssids=bssids
        self.ssids=ssids
        self.latitude=latitude
        self.longitude=longitude
        self.radius=radius
        self.messages=[]

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

    def postMessage(self,author,message):
        self.messages+=[(author,message),]

    #TODO
    def unpostMessage(self):
        raise ValueError
    def getName(self):
        return self.name

    def getMessages(self):
        finalValue=self.getName()
        for m in messages:
            m+="author: "+m[0]+" mesage: "+m[1]+"\n"
        return finalValue
