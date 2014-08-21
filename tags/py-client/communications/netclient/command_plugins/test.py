from netclient.messages.data import Message

name = 'Test Commands'
depends = ()

def init(commander, context):
    pass

def do_txt(context, event):
    event.pipe.sendLine(event.args)

def do_xml(context, event):
    data = Message('echo', event.args)
    event.pipe.sendLine(data.to_xml())
