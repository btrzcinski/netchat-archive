componentclass = 'Configuration'
depends = ()

from netclient.lib.luaparser import parseLua, generateLua
from netclient.settings import NETCLIENT_CONF

class Configuration(object):
    def __init__(self, cmanager):
        self.configs = {}
        self.cfile = ''
        self.loadConfig(NETCLIENT_CONF)

    def __getitem__(self, k):
        return self.configs.__getitem__(k)

    def get(self, *args):
        return self.configs.get(*args)

    def find(self, *args):
        base = self.configs
        for p in args:
            if base is None:
                break
            base = base.get(p, None)
        return base

    def loadConfig(self, filename):
        self.cfile = filename
        fp = open(filename, 'r')
        code = ''
        for line in fp:
            if not line.lstrip().startswith('#'):
                code += line
        fp.close()
        lua = parseLua(code)
        if len(lua) != 3:
            raise Exception, '?' #FIXME
        if lua[0] != 'config':
            #FIXME
            raise Exception, 'mal-formed config file: file must only have one element named config'
        self.configs.update(lua[2])

    def refreshConfig(self):
        pass
