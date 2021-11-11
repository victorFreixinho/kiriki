import sys
import json
from threading import Thread
from mySocket import MySocket
from routes import Routes
from GUI.Login import Ui_MainWindow as LoginUiWindow
from GUI.Game import Ui_MainWindow as GameUiWindow
from PyQt5.QtWidgets import QMainWindow, QApplication,QStackedWidget
from PyQt5.QtGui import QPixmap


class ListenThread(Thread):
    def __init__(self, socket):
        self.socket = socket
        super().__init__()

    def run(self):
        while True:
            response = self.socket.listen()
            print(response)
            header, payloadJson, _ = response.split("\r\n")
            payload = json.loads(payloadJson)
            if header == "/login":
                opponentSum = payload['opponentSum']
                opponentName = payload['opponentName']
                isFirstPlayer = payload['isFirstPlayer']
                initialDices = payload['initialDices']
                gameWindow.setInitialSettings(opponentSum, opponentName, isFirstPlayer, initialDices)
                widget.setCurrentIndex(widget.currentIndex()+1)
            elif header == "/startRound":
                loseDice=payload['loseDice']
                sum=payload['sum']
                gameWindow.startPlayerRound( loseDice, sum)
            elif header == "/guess":
                loseDice = payload['loseDice']
                if loseDice:
                    gameWindow.selectDiceToDelete()
            elif header == "/finish":
                win=payload['winner']
                gameWindow.endMatch(win)






class LoginWindow(QMainWindow, LoginUiWindow):
    def __init__(self, parent=None, socket=None):
        super().__init__(parent)
        super().setupUi(self)
        self.socket = socket
        self.playBtn.clicked.connect(self.play)
        listenThread = ListenThread(self.socket)
        listenThread.start()

    def play(self):
        routes = Routes()
        self.playBtn.setEnabled(False)
        self.mensageLabel.setText("Buscando outro jogador...")
        userName=self.nameInput.text()
        msg = routes.formatLogin(userName)
        self.socket.dispatch(msg)
        gameWindow.setUserName(userName)



class GameWindow(QMainWindow, GameUiWindow):
    def __init__(self, parent=None, socket=None,):
        super().__init__(parent)
        super().setupUi(self)
        self.socket = socket
        self.sendBtn.setEnabled(False)
        self.sendBtn.clicked.connect(self.sendGuess)
        self.slotN.clicked.connect(self.deleteDice(self.slotN, self.slotNNumber))
        self.slotE.clicked.connect(self.deleteDice(self.slotE, self.slotENumber))
        self.slotS.clicked.connect(self.deleteDice(self.slotS, self.slotSNumber))
        self.slotW.clicked.connect(self.deleteDice(self.slotW, self.slotWNumber))
        self.slotC.clicked.connect(self.deleteDice(self.slotC, self.slotCNumber))


    def getDicePictureLocation(self, number: int):
        diceLocationByNumber = {
            1:".\src\GUI\images\Dice1.png",
            2:".\src\GUI\images\Dice2.png",
            3:".\src\GUI\images\Dice3.png",
            4:".\src\GUI\images\Dice4.png",
            5:".\src\GUI\images\Dice5.png",
            6:".\src\GUI\images\Dice6.png",
        }
        return diceLocationByNumber[number]

    def setUserName(self,userName):
        self.userNameLabel.setText(userName)

    def setInitialSettings(self, opponentSum:int, opponentName:str, isFirstPlayer:bool, initialDices:list):
        self.opponentCurrentDiceNumberLabel.setText("5")
        self.sumValueLabel.setText(str(opponentSum))
        self.opponentNameLabel.setText(opponentName)
        dices = [self.slotCNumber, self.slotENumber, self.slotNNumber, self.slotSNumber, self.slotWNumber]
        for i in range(5):
            dices[i].setPixmap(QPixmap(self.getDicePictureLocation(initialDices[i])))
            dices[i].value = initialDices[i]
        if isFirstPlayer:
            self.startPlayerRound()


    def startPlayerRound(self,**kwargs):
        self.sendBtn.setEnabled(True)
        self.sumValueLabel.setText(str(kwargs.get('sum')))
        if kwargs.get('loseDice'):
            newAmount=int(self.opponentCurrentDiceNumberLabel.text())-1
            self.opponentCurrentDiceNumberLabel.setText(newAmount)


    def sendGuess(self):
        guessInputs=[
            self.input0.text(),
            self.input1.text(),
            self.input2.text(),
            self.input3.text(),
            self.input4.text(),
        ]
        for i in range(5):
            if(len(guessInputs[i])==0 or int(guessInputs[i])<1 or int(guessInputs[i])>6):
                self.msgLabel.setText("Digite apenas números entre 1 e 6")
            else:
                routes = Routes()
                msg = routes.formatGuessing(guessInputs)
                self.socket.dispatch(msg)

    def selectDiceToDelete(self):
        self.slotN.setEnabled(True)
        self.slotE.setEnabled(True)
        self.slotS.setEnabled(True)
        self.slotW.setEnabled(True)
        self.slotC.setEnabled(True)
        self.msgLabel.setText("Selecione um dado para remover.")

    def deleteDice(self, btn, label):
        self.slotN.setEnabled(False)
        self.slotE.setEnabled(False)
        self.slotS.setEnabled(False)
        self.slotW.setEnabled(False)
        self.slotC.setEnabled(False)
        routes = Routes()
        label.setVisible(False)
        btn.setVisible(False)
        msg = routes.formatDiceDeletion(label.value)
        self.socket.dispatch(msg)

    def endMatch(self, win:bool):
        if(win):
            self.msgLabel.setText("Voce Venceu!")
        else:
            self.msgLabel.setText("Você Perdeu!")


if __name__ == '__main__':
    qt = QApplication(sys.argv)
    mySocket = MySocket()
    widget = QStackedWidget()
    loginWindow = LoginWindow(None, mySocket)
    gameWindow = GameWindow(None, mySocket)
    widget.addWidget(loginWindow)
    widget.addWidget(gameWindow)
    widget.setFixedHeight(500)
    widget.setFixedWidth(900)
    widget.show()
    qt.exec_()





