import sys
from mySocket import MySocket
from Session import Session
from ListenThread import ListenThread
from LoginWindow import LoginWindow
from GameWindow import GameWindow
from PyQt5.QtWidgets import QApplication, QStackedWidget

# Change this params to your server settings
host = '192.168.0.13'
port = 5000

if __name__ == '__main__':
    qt = QApplication(sys.argv)
    mySocket = MySocket(host, port)
    session = Session()
    widget = QStackedWidget()
    loginWindow = LoginWindow(None, mySocket, session)
    gameWindow = GameWindow(None, mySocket, session)
    listenThread = ListenThread(mySocket, widget, gameWindow)
    listenThread.start()
    widget.addWidget(loginWindow)
    widget.addWidget(gameWindow)
    widget.setFixedHeight(500)
    widget.setFixedWidth(900)
    widget.show()
    qt.setQuitOnLastWindowClosed(True)
    qt.exec_()
