from twisted.internet import reactor

class Parser:
    def __init__(self, term):
        self.term = term
        cmanager = term.cmanager
        self.config = cmanager.getServiceProxy('config')
        self.commander = cmanager.getServiceProxy('commands')
        self.factory = cmanager.getServiceProxy('factory')

    def open(self):
        self.term.should_prompt = True
        self.prompt()

    def use(self, line, showprompt=True):
        self.term.should_prompt = False
        go = self.commander._execute(line, self.term, self.factory.xmlcomm)

        if go != 'noprompt' and showprompt:
            reactor.callLater(0.01, self.prompt)

        self.term.should_prompt = True

    def prompt(self):
        self.term.message(self.term.prompt)
