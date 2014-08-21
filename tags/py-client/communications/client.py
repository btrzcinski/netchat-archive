#! /usr/local/bin/python

import getopt
import sys
import os.path

from twisted.internet import reactor
from twisted.internet.protocol import Protocol, ClientFactory
from twisted.internet.stdio import StandardIO

from netclient.connection import StdIOTransport, XMLComm, Terminal
from netclient.cmanager import ComponentManager
from netclient.settings import HOST, PORT, OPTS, HELP_BUFFER

class Client(ClientFactory):
    protocol = XMLComm

    def __init__(self, verbose):
        self.verbose = verbose
        self.cmanager = cmanager = ComponentManager('factory', self)
        cmanager.loadComponent('config')
        self.config = config = cmanager.getServiceProxy('config')
        cmanager.loadComponents(config.find('components', 'default_components'))

    def log(self, msg):
        if not self.verbose:
            return
        print '[Log]: %s' % msg

    def startedConnecting(self, connector):
        h, p = connector.host, connector.port
        self.log('Attemping to connect to port %d at host \'%s\'.' % (p, h))

    def buildProtocol(self, addr):
        self.log('Establishing connection...')
        p = self.protocol(addr)
        p.factory = self
        return p

    def clientConnectionLost(self, connector, reason):
        print 'Connection lost. Shutting down.'
        reactor.callLater(0.01, self.exit, 'Lost connection.')

    def clientConnectionFailed(self, connector, reason):
        print 'Connection failed. Check your network connection or try again later.'
        self.exit('Failed connection.')

    def exit(self, reason='', delay=False):
        self.log('Received stop event.%s' % (' Reason: %s' % reason if reason else reason))
        reactor.stop()

def main():
    options = {
        'ssl': False,
        'test': False,
        'verbose': False,
        'host': HOST,
        'port': PORT,
    }

    short = ''
    long = []
    helps = []

    for opts, help in OPTS.iteritems():
        if isinstance(opts, tuple):
            short = '%s%s' % (short, opts[0][1])
            long.append(opts[1][2:])
            line = '%s, %s' % (opts[0], opts[1])
            num = HELP_BUFFER - len(line)
            line = '%s%s%s' % (line, ' ' * num, help)
            helps.append(line)
        else:
            if opts.startswith('--'):
                long.append(opts[2:])
                line = '    %s' % opts
                num = HELP_BUFFER - len(line)
                line = '%s%s%s' % (line, ' ' * num, help)
                helps.append(line)
            else:
                short = '%s%s' % (short, opts)
                num = HELP_BUFFER - len(opts)
                line = '%s%s%s' % (opts, ' ' * num, help)
                helps.append(line)

    helps.sort()

    try:
        opt, args = getopt.getopt(sys.argv[1:], short, long)
    except getopt.error, msg:
        print msg
        print 'For help, please use --help or -h.'
        sys.exit()

    for o, v in opt:
        if o == '--ssl':
            options['ssl'] = True
        elif o == '--host':
            options['host'] = v
        elif o == '--port':
            options['port'] = int(v)
        elif o in ['-d', '--debug']:
            options['test'] = True
        elif o in ['-v', '--verbose']:
            options['verbose'] = True
        elif o in ['-h', '--help']:
            sp = os.path.split(sys.argv[0])
            path = sp[1] if len(sp) else sp[0]
            print 'usage: %s [options]' % path
            print '   %s' % '\r\n   '.join(helps)
            sys.exit(0)

    factory = Client(options['verbose'])
    sysin = Terminal(factory.cmanager)
    StdIOTransport(sysin, factory)

    if options['ssl']:
        from twisted.internet.ssl import DefaultOpenSSLContextFactory
        from netclient.settings import PRIVKEY, CERTIFICATE

        ssl = DefaultOpenSSLContextFactory(PRIVKEY, CERTIFICATE)
        reactor.connectSSL(options['host'], options['port'], factory, ssl)
    else:
        reactor.connectTCP(options['host'], options['port'], factory)
    
    if options['test']:
        factory.log('Loading test command suite.')
        factory.cmanager.getService('commands').add_module('test')
        factory.cmanager.refreshComponent('commands')

    reactor.run()

if __name__ == '__main__':
    main()
