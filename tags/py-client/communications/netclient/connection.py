from sys import stdout

from twisted.internet.stdio import StandardIO
from twisted.protocols.basic import LineOnlyReceiver

from netclient import dialogs, parser, settings

class XMLComm(LineOnlyReceiver):
    delimiter = '\n'

    def __init__(self, addr):
        self.address = addr

    def lineLengthExceeded(self, line):
        LineOnlyReceiver.lineLengthExceeded(self, line)

    def lineReceived(self, line):
        parser.parse(line, self.factory.term, self)
    
    def sendLine(self, line):
        LineOnlyReceiver.sendLine(self, line)

    def connectionMade(self):
        self.factory.log('Connection successful.')
        self.factory.xmlcomm = self
        self.factory.term.dialog[-1].open()

    def drop(self, reason=''):
        if reason:
            self.factory.log(reason)
        self.transport.loseConnection()

class Terminal(LineOnlyReceiver):
    delimiter = '\n'

    def __init__(self, cmanager):
        self.cmanager = cmanager
        self.prompt = settings.PROMPT
        self.prompt_showing = False
        self.should_prompt = False

    def lineLengthExceeded(self, line):
        LineOnlyReceiver.lineLengthExceeded(self, line)

    def lineReceived(self, line, prompt=True):
        self.prompt_showing = False
        line = line.replace('\r', '')

        d = self.dialog[-1]

        if isinstance(d, dialogs.Parser):
            d.use(line, prompt)

    def message(self, text):
        self.transport.write(text)

    def sendLine(self, line):
        if not isinstance(line, str):
            line = str(line)
        LineOnlyReceiver.sendLine(self, line)

    def connectionMade(self):
        self.factory.log('Connection established to input stream.')
        self.factory.term = self
        self.dialog = [dialogs.Parser(self)]

    def drop(self, reason=''):
        self.transport.loseConnection()

class StdIOTransport(StandardIO):
    def __init__(self, protocol, factory):
        protocol.factory = factory
        StandardIO.__init__(self, protocol)
