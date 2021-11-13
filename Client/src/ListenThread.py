import json
from threading import Thread
from Routes import Routes

class ListenThread(Thread):
    def __init__(self, socket, widget, gameWindow):
        self.socket = socket
        self.widget = widget
        self.gameWindow = gameWindow
        super().__init__()

    def run(self):
        routes = Routes()
        while True:
            response = self.socket.listen()
            print(response)
            header, payloadJson, rest = response.split("\r\n", 2)
            payload = json.loads(payloadJson)
            if header == routes.login:
                opponentSum = payload['opponentSum']
                opponentName = payload['opponentName']
                isFirstPlayer = payload['isFirstPlayer']
                initialDices = payload['initialDices']
                self.gameWindow.setInitialSettings(int(opponentSum), opponentName, isFirstPlayer, initialDices)
                self.widget.setCurrentIndex(self.widget.currentIndex() + 1)
            elif header == routes.startRound:
                loseDice = payload['loseDice']
                sum = payload['sum']
                self.gameWindow.startPlayerRound(loseDice, sum)
            elif header == routes.guess:
                loseDice = payload['loseDice']
                if loseDice:
                    self.gameWindow.selectDiceToDelete()
                else:
                    self.gameWindow.msgLabel.setText(
                        "Você acertou mais da metade dos dados do oponente.\nAguarde a vez do outro jogador.")
            elif header == routes.finish:
                win = payload['winner']
                self.gameWindow.endMatch(win)
