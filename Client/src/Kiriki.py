import sys
from threading import Thread, Lock
from mySocket import MySocket
from routes import Routes
from GUI.Login import Ui_MainWindow as LoginUiWindow
from GUI.Game import Ui_MainWindow as GameUiWindow
from PyQt5.QtWidgets import QMainWindow, QApplication,QStackedWidget

class ListenThread(Thread):
    def __init__(self, socket):
        self.socket = socket
        super().__init__()

    def run(self):
       # response = self.socket.listen()
       # print(response)
        widget.setCurrentIndex(widget.currentIndex()+1)





class LoginWindow(QMainWindow, LoginUiWindow):
    def __init__(self, parent=None, socket=None):
        super().__init__(parent)
        super().setupUi(self)
        self.socket = socket
        self.playBtn.clicked.connect(self.play)

    def play(self):
        routes = Routes()
        self.playBtn.setEnabled(False)
        self.mensageLabel.setText("Buscando outro jogador...")
        msg = routes.formatLogin(self.nameInput.text())
        self.socket.dispatch(msg)
        listenThread= ListenThread(self.socket)
        listenThread.start()



class GameWindow(QMainWindow, GameUiWindow):
    def __init__(self, parent=None, socket=None):
        super().__init__(parent)
        super().setupUi(self)
        self.socket = socket



if __name__ == '__main__':
    qt = QApplication(sys.argv)
    mySocket =MySocket()
    widget = QStackedWidget()
    loginWindow = LoginWindow(None, mySocket)
    gameWindow = GameWindow(None, mySocket)
    widget.addWidget(loginWindow)
    widget.addWidget(gameWindow)
    widget.setFixedHeight(500)
    widget.setFixedWidth(900)
    widget.show()
    qt.exec_()





