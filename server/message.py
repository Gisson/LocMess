import datetime
from ExpiredMessageError import ExpiredMessageError

class message:

    def __init__(self,user,location,title,deliveryMode,topics,endTime,content="",id=0):
        self.author=user
        self.location=location
        self.content=content
        self.id=id
        self.title=title
        self.deliveryMode=deliveryMode
        self.topics=topics
        self.endTime=endTime
        self.expired=False

    def getId(self):
            if self.isExpired():
                raise ExpiredMessageError
            else:
                return self.id
    def getTitle(self):
        if self.isExpired():
            raise ExpiredMessageError
        else:
            return self.title
    def getAuthor(self):
        if self.isExpired():
            raise ExpiredMessageError
        else:
            return self.author
    def getContent(self):
        if self.isExpired():
            raise ExpiredMessageError
        else:
            return self.content
    def getDeliveryMode(self):
        if self.isExpired():
            raise ExpiredMessageError
        else:
            return self.deliveryMode
    def getTopics(self):
        if self.isExpired():
            raise ExpiredMessageError
        else:
            return self.topics
    def isExpired(self):
        if self.expired:
            return True
        elif datetime.datetime.now()>self.endTime:
            self.expired=True
            return True
        else:
            return False

    def getLocation(self):
        return self.location
    # def get_content(self):
    #     return self.content

    # def get_author(self):
    #     return self.author

    # def get_location(self):
    #     return self.location

    # def get_id(self):
    #     return self.id

    # def getJson(self):
    #     return {'author':self.author.getUsername(),'location':self.location.getName(),'content':self.content}

    def __str__(self):
        return " Id: "+str(self.id)+"Author: "+self.author.getUsername()+" Title: "+self.title+ "Content: "+self.content+ "Location: "+self.location.getName()
    def getJson(self):
        return {"Id":str(self.id),"Author":self.author.getUsername(),"Title":self.title,"Content":self.content,"Location":self.location.getName()}
