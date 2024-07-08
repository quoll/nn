#!/bin/env python3

# This script is a trivial server that listens for incoming data from a client
# and plots it as a scatter plot using matplotlib.

import matplotlib.pyplot as plt
import socket
import json

def plot_data(data):
    if 'x' not in data or 'y' not in data:
        print("Invalid data format")
        return
    x = data['x']
    y = data['y']
    del data['x']
    del data['y']
    # pass the remaining data to the plot function
    try:
        plt.scatter(x, y, **data)
        plt.show()
    except Exception as e:
        print("Error plotting data: ", e)
        print("Data: ", data)

BUFFER_SIZE = 4096

def start_server():
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(('localhost', 65432))
    server_socket.listen(1)
    print("Server is listening on port 65432")

    while True:
        conn, addr = server_socket.accept()
        data = ""
        buffer = conn.recv(BUFFER_SIZE).decode('utf-8')
        while buffer:
            data += buffer
            buffer = conn.recv(BUFFER_SIZE).decode('utf-8')
            if not buffer:
                break
        data = json.loads(data)
        plot_data(data)
        conn.close()

if __name__ == "__main__":
    start_server()
