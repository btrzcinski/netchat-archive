import sys

from echolib import *

HOST = 'agammemnon.csl.tjhsst.edu'
PORT = 45287

def main():
	c = Connection(HOST, PORT)
	var = c.connect()
	if var <= 0:
		if var is UNKNOWN_HOST:
			print 'Host \'%s\' not found.' % HOST
		elif var is CONNECTION_REFUSED:
			print 'Connection refused on port %d by host \'%s\'.' % (PORT, HOST)
		exit()

	print 'Connection successful.'

	while True:
		msg = raw_input()
		if msg == '.quit':
			break
		c.send(msg)
		print c.recv()

	print 'Cleaning up.'
	print 'Closing connection.'
	c.close()
	exit()

def exit():
	print 'Exiting.'
	sys.exit()

if __name__ == '__main__':
	main()
