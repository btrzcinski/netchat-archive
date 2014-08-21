import sys

from connection import *
from lib.parser import XMLDecoder
from messages.data import Data

HOST = 'page'
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
		try:
			msg = raw_input('Input data: ')
		except KeyboardInterrupt:
			print 'Caught KeyboardInterrupt.'
			break
		if msg == '.quit':
			break
		data = Data(msg)
		c.send(data.to_xml())
		inc = c.recv()
		if not inc:
			print 'Connection broken prematurely.'
			break
		d = XMLDecoder(inc)
		print 'Incoming data...\n\n%s\n' % d.parse()

	print 'Cleaning up.'
	print 'Severing connections.'
	c.close()
	exit()

def exit():
	print 'Exiting.'
	sys.exit()

if __name__ == '__main__':
	main()
