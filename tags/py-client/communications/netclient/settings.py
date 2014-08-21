import os

# -- Connection Settings --

HOST = 'localhost'
PORT = 45287

# -- Paths --

DATA = 'data'
NETCLIENT_CONF = os.path.join(DATA, 'netclient.conf')

SSL = 'ssl'
CERTIFICATE = os.path.join(SSL, 'cacert.pem')
PRIVKEY = os.path.join(SSL, 'privkey.pem')

# -- Misc Settings --

PROMPT = 'Input cmd: '

OPTS = {
    ('-h', '--help'): 'Displays this help',
    ('-d', '--debug'): 'Enables debug commands',
    ('-v', '--verbose'): 'Enables verbose mode',
    '--ssl': 'Forces an SSL connection',
    '--host=': 'Overrides the default host',
    '--port=': 'Overrides the default port',
}

HELP_BUFFER = 35
