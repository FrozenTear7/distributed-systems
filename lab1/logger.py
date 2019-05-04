import socket
import struct
import datetime

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
sock.bind(('', 4242))
mreq = struct.pack("=4sl", socket.inet_aton("224.3.29.71"), socket.INADDR_ANY)

sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)

while True:
    data, address = sock.recvfrom(1024)
    data = str(data)[2:(str(data).index('\\'))]
    print(("%s : %s\n" % (str(datetime.datetime.now()), data)))
    with open("log.txt", "a+") as log:
        log.write("%s : %s\n" % (str(datetime.datetime.now()), data))
