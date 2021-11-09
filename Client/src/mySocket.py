import socket


class MySocket:
    def __init__(self):
        self.HOST = '192.168.15.37'
        self.PORT = 5000
        self.tcp = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.open_connection()

    def open_connection(self):
        dest = (self.HOST, self.PORT)
        self.tcp.connect(dest)

    def dispatch(self, msg):
        self.tcp.send((msg+'\n').encode())

    def listen(self):
        msg = self.tcp.recv(1024)
        return msg.decode()


    def close_connection(self):
        self.tcp.close()

# https://www.tutorialspoint.com/python_penetration_testing/python_penetration_testing_socket_and_methods.htm
