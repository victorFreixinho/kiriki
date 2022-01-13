import sys
from App import App
from MySocket import MySocket
from Session import Session
from ListenThread import ListenThread
from LoginWindow import LoginWindow
from GameWindow import GameWindow
from PyQt5.QtWidgets import QStackedWidget

# Change this params to your server settings
host = '192.168.0.13'
port = 5000

#TODO:
# Create a window where the user can give the host's Ip and the server's port in an input.
# Solve the dice removal bug: whe are able to click only in the blank space of the dice.
# Solve the send button bug: the button becomes green only when the cursor pass through it.
# Create the play again option.

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
