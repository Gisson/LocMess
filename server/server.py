#!/usr/bin/python
import socket
import logging
import random
import sys
import argparse
from user import user
import hashlib

MAXREQUESTS=1
PORT=31000



#Have a dictionary which shows something like:
# {name_of_location : [type_of_location(gps), (rad, long, lat)]}
# or {name_of_location : [type_of_location(ssid), (rad, ssid1, ssid2,...)]}
#Only works for gps coordinates
def addLocation(data,locations):
	try:
		attrs=data.split("=>")
		logging.debug("Acquired data, parsed name="+attrs[0]+" and atributes="+attrs[1])
		attrs2=attrs[1].split("[")[1].split("]")[0].split(",")
		locations[attrs[0]]=[attrs2[0],(attrs2[1],float(attrs2[2]),float(attrs2[3]))]
	except:
		logging.ERROR("Fail in parsing arguments")



#def parseLogin(data,users):
def registerUser(data,users):

	username=data.split("[")[1].split(":")[0]
	password=data.split("]")[0].split(":")[1]
	newuser=user(username,password)
	users+=[newuser,]
	logging.debug("Created user with username "+username+"\nPassword: "+newuser.getPassword())


def loginUser(data,users):
	username=data.split("[")[1].split(":")[0]
	password=data.split("]")[0].split(":")[1]

	for u in users:
		if u.isPasswordCorrect(password) :
			return u.add_token()
	raise RuntimeError



if __name__=="__main__":
	users=[]
	locations={}
	if len(sys.argv)>1 :
		#TODO: Do this if you have time...
		#parser=argparse.ArgumentParser(description="-p for port\n-n for number of accepts")
		#parser.add_argument('p')
		#args=parser.parse_args()
		#print(args.p)
		try:
			PORT=int(sys.argv[1])
		except ValueError:
			print("2nd argument should be an int for the port number")
			sys.exit(-1)
	logging.basicConfig(level=logging.DEBUG)
	logging.debug("Starting server with max requests="+str(MAXREQUESTS))
	s=socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	try:
		s.bind(('127.0.0.1',PORT))
		s.listen(MAXREQUESTS)
		while True:
			(clientsocket, addr)=s.accept()
			logging.debug("Accepted connection from "+str(addr))
			while True:
				data=clientsocket.recv(4096)
				if not data: logging.debug("Connection broken!");break
				msgtype=data.split("Type:")
				logging.debug("Message type is "+msgtype[1])
				if(msgtype[1].find("register")!=-1):
					try:
						registerUser(msgtype[1],users)
					except RuntimeError :
						clientsocket.send("Error registering. User exists\n")
						logging.error("Error registering user")
						continue
				elif(msgtype[1].find("login")!=-1):
					try:
						clientsocket.send(str(loginUser(msgtype[1],users)))
						clientsocket.send("Login successful\n")
						continue
					except RuntimeError:
						clientsocket.send("Login failure\n")
						logging.error("Error logging in")
				elif(msgtype[1].find("addlocation")!=-1):
					addLocation(msgtype[1].split("[[")[1].split("]]")[0],locations)
				else:
					logging.warning("Undefined type of message")
				logging.debug("Finished parsing. State of locations dictionary: "+str(locations))
				logging.debug("Finished parsing. State of users dictionary: "+str(users))
				#print(data)
	except socket.error:
		logging.critical("Port "+str(PORT)+" already in use. Try changing! ")
	finally:
		s.close()
