class Session:
    def __init__(self, userName=None):
        self._userName = userName

    @property
    def userName(self):
        return self._userName

    @userName.setter
    def userName(self, name):
        if isinstance(name, str):
            self._userName = name
