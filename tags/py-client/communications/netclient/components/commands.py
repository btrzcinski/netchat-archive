componentclass = 'Commander'
depends = ('config',)

import sys
from netclient.lib.comparison import bin_search
from netclient.cmanager import dynamicLoad

PLUGIN_BASE = 'command_plugins'

class CommandEvent(object):
    __slots__ = ('command', 'args', 'term', 'pipe')

    def __init__(self, command, args, term, pipe):
        self.command = command
        self.args = args
        self.term = term
        self.pipe = pipe

class CommandContext:
    def __init__(self, cmanager, commander):
        self.cmanager = cmanager
        self.factory = cmanager.getServiceProxy('factory')
        self.command = commander

    def dispatch(self, command, *args):
        return self.commander.dispatch(command, str.joni(' ', args))

class Commander:
    def __init__(self, cmanager):
        self.modules = {}
        self.underlying_modules = []
        self.commands = {}
        self.cmanager = cmanager
        self.context = CommandContext(cmanager, self)
        self.string_list = []
        self.refreshConfig()

    def refreshConfig(self):
        self.commands.clear()
        self.modules.clear()
        dmodules = self.cmanager.getService('config').find('commands', 'plugins')
        if dmodules:
            map(self.loadModule, dmodules)
        if self.underlying_modules:
            map(self.loadModule, self.underlying_modules)
        
        self.update_list()

    def add_module(self, name):
        self.underlying_modules.append(name)

    def update_list(self):
        self.string_list = list(self.commands.iterkeys())
        self.string_list.sort()
        self.num = len(self.string_list)

    def loadModule(self, name):
        module = dynamicLoad(PLUGIN_BASE, name)
        reload(module)
        self.modules[name] = module

        self.autoRegister(module)

        try:
            module.init(self, self.context)
            return True
        except:
            return False

    def autoRegister(self, plugin):
        for attribute in dir(plugin):
            obj = getattr(plugin, attribute)
            if attribute.startswith('do_') and callable(obj):
                self.registerCommand(attribute[3:], obj)

    def registerCommand(self, name, func):
        self.commands[name.lower()] = func

    def _execute(self, command, term, pipe):
        fractured = command.split(' ', 1)
        command = fractured[0]
        if not command:
            return
        args = len(fractured) == 2 and fractured[1] or ''

        fullcmd = self._locatecmd(command)
        if not fullcmd:
            term.sendLine('Invalid command.')
            return
        return self.dispatch(fullcmd, args, term, pipe)

    def dispatch(self, command, args, term, pipe):
        func = self.commands[command]
        event = CommandEvent(command, args, term, pipe)
        return func(self.context, event)

    def _locatecmd(self, command):
        origcom = command
        ind = bin_search(self.string_list, command)
        if command == origcom:
            if ind == -1:
                return
            word = self.string_list[ind]
        else:
            word = command
        return word
