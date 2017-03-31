


class message:

    def __init__(self,user,location,content="",id=0):
        self.author=user
        self.location=location
        self.content=content
        self.id=id

    def get_content(self):
        return self.content

    def get_author(self):
        return self.author

    def get_location(self):
        return self.location

    def get_id(self):
        return self.id

    def getJson(self):
        return {'author':self.author.getUsername(),'location':self.location.getName(),'content':self.content}

    def __str__(self):
        return "Author: "+str(self.author)+" Content: "+self.content+" Id="+self.id
