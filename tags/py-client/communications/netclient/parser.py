from xml.etree.ElementTree import XML
from xml.parsers.expat import ExpatError

from netclient.messages.data import Data

def parse(packet, term, xmlc):
    opts = {
        'data': print_data
    }

    try:
        tree = XML(packet)
    except ExpatError:
        term.sendLine('Warning: Malformed XML. Interpreting as data.')
        tree = XML(Data(packet).to_xml())

    typ = tree.get('type', 'data')

    opts.get(typ, print_data)(tree, term, xmlc)

def print_data(tree, term, xmlc):
    term.sendLine('Incoming server data: %s' % tree.text)
