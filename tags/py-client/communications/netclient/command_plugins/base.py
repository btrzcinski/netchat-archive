#from netclient.messages.data import Data

name = 'Base Commands'
depends = ()

def init(commander, context):
    pass

def do_exit(context, event):
    context.factory.term.drop()
    context.factory.xmlcomm.drop(event.args if event.args else 'Client quit.')

def do_reload(context, event):
    if not event.args:
        event.term.sendLine('Reload what?')
        return
    args = event.args.lower()
    if context.cmanager.hasComponent(args):
        event.term.sendLine('Affirmative.')
        context.cmanager.refreshComponent(args)
    else:
        event.term.sendLine('Negative, component fails to exist.')

def do_rcommands(context, event):
    if not event.args:
        event.term.sendLine('Reload which command plugin?')
        return
    args = event.args.lower()
    c = context.command
    a = c.loadModule(args)
    if a:
        event.term.sendLine('Affirmative.')
        c.update_list()
    else:
        event.term.sendLine('Unable to reload module.')

def do_acommand(context, event):
    if not event.args:
        event.term.sendLine('Add which module?')
        return
    args = event.args.lower()
    c = context.command
    a = c.loadModule(args)
    if a:
        event.term.sendLine('Affirmative.')
        c.add_module(args)
        c.update_list()
    else:
        event.term.sendLine('Unable to load module.')
