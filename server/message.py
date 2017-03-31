


class message:

    def __init__(self,user,location,content=""):
        self.author=user
        self.location=location
        self.content=content

    def get_content(self):
        return self.content

    def get_author(self):
        return self.author

    def get_location(self):
        return self.location

    def __str__(self):
        return "Author: "+str(self.author)+" Content: "+self.content
