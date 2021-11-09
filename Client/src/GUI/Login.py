# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'C:\projetos\Kiriki\Client\src\GUI\Login.ui'
#
# Created by: PyQt5 UI code generator 5.15.6
#
# WARNING: Any manual changes made to this file will be lost when pyuic5 is
# run again.  Do not edit this file unless you know what you are doing.


from PyQt5 import QtCore, QtGui, QtWidgets


class Ui_MainWindow(object):
    def setupUi(self, MainWindow):
        MainWindow.setObjectName("MainWindow")
        MainWindow.setEnabled(True)
        MainWindow.resize(655, 416)
        MainWindow.setStyleSheet("QMainWindow{\n"
"    background-color: #c7c7c7;\n"
"}\n"
"QLabel{\n"
"    font-size:18px;\n"
"    font-family:Calibri;\n"
"}")
        self.centralwidget = QtWidgets.QWidget(MainWindow)
        self.centralwidget.setStyleSheet("#gameTitleLabel{\n"
"    font-family: Algerian;\n"
"    text-align:center;\n"
"}")
        self.centralwidget.setObjectName("centralwidget")
        self.yellowContainer = QtWidgets.QWidget(self.centralwidget)
        self.yellowContainer.setGeometry(QtCore.QRect(199, 9, 281, 401))
        self.yellowContainer.setStyleSheet("#yellowContainer {\n"
"    background-color: #ffc000;\n"
"    padding: 15px 25px;\n"
"    text-align: center;    \n"
"    text-decoration: none;\n"
"     border: none;\n"
"      border-radius: 40px;\n"
"}\n"
"")
        self.yellowContainer.setObjectName("yellowContainer")
        self.gameTitleLabel = QtWidgets.QLabel(self.yellowContainer)
        self.gameTitleLabel.setGeometry(QtCore.QRect(3, 30, 281, 66))
        self.gameTitleLabel.setStyleSheet("#gameTitleLabel{\n"
"    font-size: 60px;\n"
"}")
        self.gameTitleLabel.setAlignment(QtCore.Qt.AlignCenter)
        self.gameTitleLabel.setObjectName("gameTitleLabel")
        self.playBtn = QtWidgets.QPushButton(self.yellowContainer)
        self.playBtn.setEnabled(True)
        self.playBtn.setGeometry(QtCore.QRect(20, 240, 241, 60))
        self.playBtn.setCursor(QtGui.QCursor(QtCore.Qt.PointingHandCursor))
        self.playBtn.setStyleSheet("#playBtn {\n"
"  display: inline-block;\n"
"  padding: 15px 25px;\n"
"  font-size: 28px;\n"
"  font-family: Calibri;\n"
"  text-align: center;    \n"
"  text-decoration: none;\n"
"  outline: none;\n"
"  color: #fff;\n"
"  background-color: #70ad46;\n"
"  border: none;\n"
"  border-radius: 15px;\n"
"}\n"
"\n"
"#playBtn[enabled=\"false\"] {\n"
"  display: inline-block;\n"
"  padding: 15px 25px;\n"
"  font-size: 28px;\n"
"  font-family: Calibri;\n"
"  text-align: center;    \n"
"  text-decoration: none;\n"
"  outline: none;\n"
"  color: #fff;\n"
"  background-color: #a0a0a0;\n"
"  border: none;\n"
"  border-radius: 15px;\n"
"}\n"
"\n"
"#playBtn:hover {\n"
"background-color: #3e8e41\n"
"}\n"
"\n"
"#playBtn:pressed {\n"
"  background-color: #4CAF50;\n"
"}\n"
"")
        self.playBtn.setObjectName("playBtn")
        self.nameInput = QtWidgets.QLineEdit(self.yellowContainer)
        self.nameInput.setGeometry(QtCore.QRect(20, 141, 241, 60))
        self.nameInput.setStyleSheet("#nameInput {\n"
"  padding: 15px 25px;\n"
"  font-size: 24px;\n"
"  text-align: center;    \n"
"  text-decoration: none;\n"
"  outline: none;\n"
"  border: none;\n"
"  border-radius: 15px;\n"
"\n"
"}")
        self.nameInput.setAlignment(QtCore.Qt.AlignCenter)
        self.nameInput.setClearButtonEnabled(False)
        self.nameInput.setObjectName("nameInput")
        self.userInputLabel = QtWidgets.QLabel(self.yellowContainer)
        self.userInputLabel.setGeometry(QtCore.QRect(30, 120, 211, 16))
        self.userInputLabel.setStyleSheet("#userInputLabel{\n"
"    font-size:18px;\n"
"    font-family:Calibri;\n"
"}")
        self.userInputLabel.setObjectName("userInputLabel")
        self.mensageLabel = QtWidgets.QLabel(self.yellowContainer)
        self.mensageLabel.setEnabled(True)
        self.mensageLabel.setGeometry(QtCore.QRect(20, 310, 241, 20))
        self.mensageLabel.setStyleSheet("")
        self.mensageLabel.setText("")
        self.mensageLabel.setAlignment(QtCore.Qt.AlignCenter)
        self.mensageLabel.setProperty("message", "")
        self.mensageLabel.setObjectName("mensageLabel")
        MainWindow.setCentralWidget(self.centralwidget)

        self.retranslateUi(MainWindow)
        QtCore.QMetaObject.connectSlotsByName(MainWindow)

    def retranslateUi(self, MainWindow):
        _translate = QtCore.QCoreApplication.translate
        MainWindow.setWindowTitle(_translate("MainWindow", "Kiriki"))
        self.gameTitleLabel.setText(_translate("MainWindow", "Kiriki"))
        self.playBtn.setText(_translate("MainWindow", "Jogar"))
        self.userInputLabel.setText(_translate("MainWindow", "UserName"))