from xml.etree.ElementTree import XML
from xml.parsers.expat import ExpatError

class XMLDecoder:
	def __init__(self, msg):
		self.tree = XML(msg)

	def parse(self): # XXX
		return 'Type: %s\nContents: %s' % (self.tree.tag, self.tree.text)
