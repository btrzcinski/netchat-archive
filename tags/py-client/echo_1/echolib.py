import socket
from socket import socket as csocket

UNKNOWN_HOST = 0
CONNECTION_REFUSED = -1

BUFFER_SIZE = 4*1024

class Connection:
	def __init__(self, host, port):
		self.host = host
		self.port = port
		self.socket = csocket()

	def connect(self):
		try:
			self.socket.connect((self.host, self.port))
		except socket.gaierror:
			return UNKNOWN_HOST
		except socket.error:
			return CONNECTION_REFUSED
		return True

	def close(self):
		self.socket.close()

	def recv(self):
		return self.socket.recv(BUFFER_SIZE)

	def send(self, msg):
		self.socket.send(msg)
