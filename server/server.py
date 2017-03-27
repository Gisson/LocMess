#!/usr/bin/python
import socket
import logging

MAXREQUESTS=1



if __name__=="__main__":
	logging.basicConfig(level=logging.DEBUG)
	logging.debug("Starting server with max requests="+str(MAXREQUESTS))
	s=socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	s.bind(('127.0.0.1',31000))
	s.listen(MAXREQUESTS)
	while True:
		(clientsocket, addr)=s.accept()
		logging.debug("Accepted connection from "+str(addr))
		while True:
			data=clientsocket.recv(4096)
			if not data: logging.debug("Connection broken!");break
			print(data)
			
