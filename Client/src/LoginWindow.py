from Routes import Routes
from GUI.Login import Ui_MainWindow as LoginUiWindow
from PyQt5.QtWidgets import QMainWindow


class LoginWindow(QMainWindow, LoginUiWindow):
    def __init__(self, parent=None, socket=None, session=None):
        super().__init__(parent)
        super().setupUi(self)
        self.socket = socket
        self.session = session
        self.playBtn.clicked.connect(self.play)

    def play(self):
        routes = Routes()
        self.playBtn.setEnabled(False)
        self.mensageLabel.setText("Buscando outro jogador...")
        userName = self.nameInput.text()
        self.session.userName = userName
        msg = routes.formatLogin(userName)
        self.socket.dispatch(msg)
