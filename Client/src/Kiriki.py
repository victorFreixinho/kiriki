import sys
import json
from mySocket import MySocket
from GUI.Login import *
from PyQt5.QtWidgets import QMainWindow, QApplication

class Kiriki(QMainWindow, Ui_MainWindow):
    def __init__(self, parent=None, socket=None):
        super().__init__(parent)
        super().setupUi(self)
        self.socket = socket
        self.playBtn.clicked.connect(self.play)

    def play(self):
        self.playBtn.setEnabled(False)
        self.mensageLabel.setText("Buscando outro jogador...")
        userName = {'userName': self.nameInput.text()}
        self.socket.dispatch(json.dumps(userName, indent=4))


if __name__ == '__main__':
    qt = QApplication(sys.argv)
    mySocket=MySocket()
    kiriki = Kiriki(None, mySocket)
    kiriki.show()
    qt.exec_()

