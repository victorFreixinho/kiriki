import json


class Routes:
    def __init__(self):
        self.login = '/login'

    def formatLogin(self, userName: str):
        payload = {'userName': userName}
        jsonPayload= json.dumps(payload, indent=4)
        print(self.login+'\n'+jsonPayload)
        return self.login+'\n'+jsonPayload
