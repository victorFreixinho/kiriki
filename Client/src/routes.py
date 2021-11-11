import json


class Routes:
    def __init__(self):
        self.login = '/login'
        self.guess = '/guess'
        self.excludeDice = '/excludeDice'

    def formatLogin(self, userName: str):
        payload = {'userName': userName}
        jsonPayload= json.dumps(payload)
        return self.login+'\n'+jsonPayload

    def formatGuessing(self, guessInputs:list):
        payload = {'guess': guessInputs}
        jsonPayload = json.dumps(payload)
        return self.guess+'\n'+jsonPayload

    def formatDiceDeletion(self, deletedValue:int):
        payload = {'value': deletedValue}
        jsonPayload = json.dumps(payload)
        return self.excludeDice+'\n'+jsonPayload