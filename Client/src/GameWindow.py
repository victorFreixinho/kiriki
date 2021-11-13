from Routes import Routes
from GUI.Game import Ui_MainWindow as GameUiWindow
from PyQt5.QtWidgets import QMainWindow
from PyQt5.QtGui import QPixmap


class GameWindow(QMainWindow, GameUiWindow):
    def __init__(self, parent=None, socket=None, session=None):
        super().__init__(parent)
        super().setupUi(self)
        self.socket = socket
        self.session = session
        self.routes = Routes()
        self.sendBtn.setEnabled(False)
        self.sendBtn.clicked.connect(self.sendGuess)
        self.slotN.clicked.connect(lambda: self.deleteDice(self.slotN, self.slotNNumber))
        self.slotE.clicked.connect(lambda: self.deleteDice(self.slotE, self.slotENumber))
        self.slotS.clicked.connect(lambda: self.deleteDice(self.slotS, self.slotSNumber))
        self.slotW.clicked.connect(lambda: self.deleteDice(self.slotW, self.slotWNumber))
        self.slotC.clicked.connect(lambda: self.deleteDice(self.slotC, self.slotCNumber))
        self.inputs = [self.input0, self.input1, self.input2, self.input3, self.input4]

    def getDicePictureLocation(self, number: int):
        diceLocationByNumber = {
            1: ".\GUI\images\Dice1.png",
            2: ".\GUI\images\Dice2.png",
            3: ".\GUI\images\Dice3.png",
            4: ".\GUI\images\Dice4.png",
            5: ".\GUI\images\Dice5.png",
            6: ".\GUI\images\Dice6.png",
        }
        return diceLocationByNumber[number]

    def setInitialSettings(self, opponentSum: int, opponentName: str, isFirstPlayer: bool, initialDices: list):
        self.userNameLabel.setText(self.session.userName)
        self.opponentCurrentDiceNumberLabel.setText("5")
        print(str(opponentSum))
        self.sumValueLabel.setText(str(opponentSum))
        self.opponentNameLabel.setText(opponentName)
        dices = [self.slotCNumber, self.slotENumber, self.slotNNumber, self.slotSNumber, self.slotWNumber]
        for i in range(5):
            dices[i].setPixmap(QPixmap(self.getDicePictureLocation(initialDices[i])))
            dices[i].setProperty('value', str(initialDices[i]))
        if isFirstPlayer:
            self.startPlayerRound()
        else:
            self.msgLabel.setText("Aguarde a vez do outro jogador.")

    def startPlayerRound(self, loseDice=None, sum=None):
        self.msgLabel.setText("Agora é a sua vez de adivinhar.")
        self.sendBtn.setEnabled(True)
        if sum:
            self.sumValueLabel.setText(str(sum))
        if loseDice:
            newAmount = str(int(self.opponentCurrentDiceNumberLabel.text()) - 1)
            self.opponentCurrentDiceNumberLabel.setText(newAmount)
            self.inputs[int(newAmount)].setEnabled(False)
            self.inputs[int(newAmount)].setVisible(False)
            self.inputs.pop()

    def sendGuess(self):
        for el in self.inputs:
            if el.text() == '':
                self.msgLabel.setText("Prencha todas as caixas de entrada.")
                return

        guessInputs = [int(el.text()) if el else -1 for el in self.inputs]
        self.sendBtn.setEnabled(False)
        for i in range(len(guessInputs)):
            if guessInputs[i] == 0 or guessInputs[i] < 1 or guessInputs[i] > 6:
                self.msgLabel.setText("Digite apenas números entre 1 e 6")
                return

        self.msgLabel.setText("Aguarde a vez do outro jogador.")
        msg = self.routes.formatGuessing(guessInputs)
        self.socket.dispatch(msg)

    def selectDiceToDelete(self):
        self.slotN.setEnabled(True)
        self.slotE.setEnabled(True)
        self.slotS.setEnabled(True)
        self.slotW.setEnabled(True)
        self.slotC.setEnabled(True)
        self.msgLabel.setText("Você errou!\nSelecione um de seus dados para remover.")

    def deleteDice(self, btn, label):
        self.slotN.setEnabled(False)
        self.slotE.setEnabled(False)
        self.slotS.setEnabled(False)
        self.slotW.setEnabled(False)
        self.slotC.setEnabled(False)
        self.msgLabel.setText("Aguarde a vez do outro jogador.")
        label.setVisible(False)
        btn.setVisible(False)
        msg = self.routes.formatDiceDeletion(int(label.property('value')))
        self.socket.dispatch(msg)

    def endMatch(self, win: bool):
        if win:
            self.msgLabel.setText("Você Venceu!")
        else:
            self.msgLabel.setText("Você Perdeu!")

