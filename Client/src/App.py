import sys
import typing

from PyQt5.QtWidgets import QApplication

class App(QApplication):
    def __init__(self, argv: typing.List[str], socket):
        self.socket = socket
        super().__init__(argv)

    def __del__(self):
        self.socket.close_connection()

