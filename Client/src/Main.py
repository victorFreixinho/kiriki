import sys
from App import App
from MySocket import MySocket
from Session import Session
from ListenThread import ListenThread
from LoginWindow import LoginWindow
from GameWindow import GameWindow
from PyQt5.QtWidgets import QStackedWidget

# Change this params to your server settings
host = '192.168.0.11'
port = 5000

if __name__ == '__main__':
    mySocket = MySocket(host, port)
    qt = App(sys.argv, mySocket)
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
