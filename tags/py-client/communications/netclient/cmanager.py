def dynamicLoad(basepath, name):
    basemod = __import__(basepath, globals(), globals(), [name])
    module = getattr(basemod, name, None)
    return module

class ServiceProxy(object):
    _service = None
    def __init__(self, s):
        self._service = s

    def __replaceService(self, s):
        self._service = s

    def __getattr__(self, attr):
        return getattr(self._service, attr)

    def __setattr__(self, attr, val):
        if attr == '_service':
            return object.__setattr__(self, attr, val)
        else:
            return setattr(self.__service, attr, val)

class ComponentManager(object):
    def __init__(self, basename='base', baseobj=None):
        self.services = {}
        self.proxies = {}
        self.basename = basename
        self.base = (True if baseobj else False)
        if baseobj:
            self.addService(basename, baseobj)


    def getService(self, name):
        assert self.services.has_key(name), '%s component not found' % name
        return self.services[name]

    def getServiceProxy(self, name):
        p = self.proxies.has_key(name)
        s = self.services.has_key(name)
        assert p or s, '%s component not found' % name
        if p:
            return self.proxies[name]
        else:
            self.proxies[name] = ServiceProxy(self.services[name])
            return self.proxies[name]

    def addService(self, name, service):
        self.services[name] = service

    def loadComponent(self, name):
        if self.services.has_key(name):
            return True
        component = dynamicLoad('components', name)
        if component is not None:
            reload(component)
            cclass = getattr(component, component.componentclass, None)
            for dependency in getattr(component, 'depends', ()):
                loaded = self.loadComponent(dependency)
                if not loaded:
                    raise Exception, 'Component %s could not be loaded.' % name
            cinstance = cclass(self)
            self.addService(name, cinstance)
            return True
        else:
            return False

    def hasComponent(self, name):
        return self.services.has_key(name)

    def loadComponents(self, names):
        return map(self.loadComponent, names)

    def refreshComponent(self, name):
        if self.base:
            self.getService(self.basename).log('Reloading component \'%s\'.' % name)
        self.getService(name).refreshConfig()

    def shutdown(self):
        for service in self.services.iterkeys():
            service.shutdown()
        del self.services
