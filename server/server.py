#!/usr/bin/python
import socket
import logging
import random
import sys
import argparse

MAXREQUESTS=1
PORT=31000


#Have a dictionary which shows something like:
# {name_of_location : [type_of_location(gps), (rad, long, lat)]}
# or {name_of_location : [type_of_location(ssid), (rad, ssid1, ssid2,...)]}
#Only works for gps coordinates
def parseLocations(data,locations):
	try:
		attrs=data.split("=>")
		logging.debug("Acquired data, parsed name="+attrs[0]+" and atributes="+attrs[1])
		attrs2=attrs[1].split("[")[1].split("]")[0].split(",")
		locations[attrs[0]]=[attrs2[0],(attrs2[1],float(attrs2[2]),float(attrs2[3]))]
	except:
		logging.ERROR("Fail in parsing arguments")





if __name__=="__main__":
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
	locations={}
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
				parseLocations(data,locations)
				logging.debug("Finished parsing. State of dictionary: "+str(locations))
				print(data)
	except socket.error:
		logging.critical("Port "+str(PORT)+" already in use. Try changing! ")
	finally:
		s.close()	
