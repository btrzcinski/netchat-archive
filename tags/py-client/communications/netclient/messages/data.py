from xml.etree.ElementTree import Element, SubElement, tostring

def to_xml(obj):
    return Element('message', type=obj.type)

class Message:
    def __init__(self, typ, contents):
        self.type = typ
        self.contents = contents

    def to_xml(self):
        tree = to_xml(self)
        tree.text = self.contents
        return tostring(tree)

class Data:
    type = 'data'

    def __init__(self, contents):
        self.contents = contents

    def to_xml(self):
        tree = to_xml(self)
        tree.text = self.contents
        return tostring(tree)
