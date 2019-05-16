#include <iostream>
#include <thread>
#include <winsock2.h>
#include <ws2tcpip.h>

#define DEFAULT_BUFLEN 512

using namespace std;

void clientTask(string clientId, int ownPort, int receiverPort, int prevPor, bool token, bool connectionType);

void clientSocketTCP(string myId, int myPort, int receiverPort, string msg);

string serverSocketTCP(string myId, int myPort, int receiverPort);

void clientSocketUDP(string myId, int myPort, int receiverPort, string msg);

string serverSocketUDP(string myId, int myPort, int receiverPort);

void multicastInfo(string myId);

int main(int argc, char **argv) {
    if (argc != 2) {
        cout << "Wrong number of arguments!" << endl;
        return -1;
    }

    bool connectionType = atoi(argv[1]) == 0 ? false : true;

    thread myThreads[100];
//
//    for (int i = 0; i < atoi(argv[1]); i++) {
//        if (i == 0) {
//            myThreads[i] = thread(clientTask, to_string(i), 9090 + i, 9090 + i + 1, true, connectionType);
//        } else if (i == atoi(argv[1]) - 1) {
//            myThreads[i] = thread(clientTask, to_string(i), 9090 + i, 9090, false, connectionType);
//        } else {
//            myThreads[i] = thread(clientTask, to_string(i), 9090 + i, 9090 + i + 1, false, connectionType);
//        }
//    }

    int threadCounter = 0;

    while (true) {
        string clientId;
        int ownPort, receiverPort, prevPort;

        cout << "ClientId, Own Port, Receiver Port, Previous Port:" << endl;
        cin >> clientId >> ownPort >> receiverPort >> prevPort;

        if (threadCounter == 0) {
            myThreads[threadCounter] = thread(clientTask, clientId, ownPort, receiverPort, prevPort, true,
                                              connectionType);
        } else {
            myThreads[threadCounter] = thread(clientTask, clientId, ownPort, receiverPort, prevPort, false,
                                              connectionType);
        }

        threadCounter++;
    }

    for (int i = 0; i < atoi(argv[1]); i++) {
        myThreads[i].join();
    }

    return 0;
}

void clientTask(string clientId, int ownPort, int receiverPort, int prevPort, bool token, bool connectionType) {
    if (ownPort == receiverPort) {
        receiverPort = atoi((!connectionType ? serverSocketTCP(clientId, ownPort, receiverPort) :
                             serverSocketUDP(clientId, ownPort, receiverPort)).c_str());
    }

    bool started = false;

    while (true) {
        if (token) {
            this_thread::sleep_for(1s);

            !connectionType ? clientSocketTCP(clientId, ownPort, receiverPort, clientId) :
            clientSocketUDP(clientId, ownPort, receiverPort, clientId);

            token = false;
        } else {
            if (!started) {
                this_thread::sleep_for(1s);

                !connectionType ? clientSocketTCP(clientId, ownPort, prevPort, to_string(ownPort)) :
                clientSocketUDP(clientId, ownPort, prevPort, to_string(ownPort));

                started = true;
            } else {
                string msg = (!connectionType ? serverSocketTCP(clientId, ownPort, receiverPort) :
                              serverSocketUDP(clientId, ownPort, receiverPort));

                if (msg.length() > 2) {
                    receiverPort = atoi(msg.c_str());
                    continue;
                }

                cout << clientId << " - got a token from: " << msg << endl;

                multicastInfo(clientId);

                token = true;

                this_thread::sleep_for(1s);

                !connectionType ? clientSocketTCP(clientId, ownPort, receiverPort, clientId) :
                clientSocketUDP(clientId, ownPort, receiverPort, clientId);

                token = false;
            }
        }
    }
}

void clientSocketTCP(string myId, int myPort, int receiverPort, string msg) {
    WSADATA wsaData;
    SOCKET ConnectSocket = INVALID_SOCKET;
    struct addrinfo *result = NULL, *ptr = NULL, hints;
    const char *sendbuf = msg.c_str();

    WSAStartup(MAKEWORD(2, 2), &wsaData);
    ZeroMemory(&hints, sizeof(hints));
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_protocol = IPPROTO_TCP;
    getaddrinfo(NULL, to_string(receiverPort).c_str(), &hints, &result);

    for (ptr = result; ptr != NULL; ptr = ptr->ai_next) {
        ConnectSocket = socket(ptr->ai_family, ptr->ai_socktype, ptr->ai_protocol);
        connect(ConnectSocket, ptr->ai_addr, (int) ptr->ai_addrlen);
    }

    freeaddrinfo(result);

    send(ConnectSocket, sendbuf, (int) strlen(sendbuf), 0);
    shutdown(ConnectSocket, SD_SEND);
    closesocket(ConnectSocket);
    WSACleanup();
}

string serverSocketTCP(string myId, int myPort, int receiverPort) {
    WSADATA wsaData;
    SOCKET ListenSocket;
    SOCKET ClientSocket;

    struct addrinfo *result = NULL;
    struct addrinfo hints;

    char recvbuf[DEFAULT_BUFLEN];
    int recvbuflen = DEFAULT_BUFLEN;
    memset(recvbuf, 0, sizeof recvbuf);

    WSAStartup(MAKEWORD(2, 2), &wsaData);
    ZeroMemory(&hints, sizeof(hints));
    hints.ai_family = AF_INET;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_protocol = IPPROTO_TCP;
    hints.ai_flags = AI_PASSIVE;

    getaddrinfo(NULL, to_string(myPort).c_str(), &hints, &result);
    ListenSocket = socket(result->ai_family, result->ai_socktype, result->ai_protocol);
    bind(ListenSocket, result->ai_addr, (int) result->ai_addrlen);
    freeaddrinfo(result);
    listen(ListenSocket, SOMAXCONN);

    ClientSocket = accept(ListenSocket, NULL, NULL);
    closesocket(ListenSocket);
    recv(ClientSocket, recvbuf, recvbuflen, 0);

    shutdown(ClientSocket, SD_SEND);
    closesocket(ClientSocket);
    WSACleanup();

    return recvbuf;
}

void clientSocketUDP(string myId, int myPort, int receiverPort, string msg) {
    WSADATA wsaData;
    WSAStartup(MAKEWORD(2, 2), &wsaData);
    const char *sendbuf = msg.c_str();

    sockaddr_in server;
    server.sin_family = AF_INET;
    server.sin_port = htons(receiverPort);
    server.sin_addr.S_un.S_addr = inet_addr("127.0.0.1");

    SOCKET out = socket(AF_INET, SOCK_DGRAM, 0);

    sendto(out, sendbuf, sizeof(sendbuf) + 1, 0, (sockaddr *) &server, sizeof(server));
    closesocket(out);
    WSACleanup();
}

string serverSocketUDP(string myId, int myPort, int receiverPort) {
    WSADATA wsaData;
    WSAStartup(MAKEWORD(2, 2), &wsaData);

    SOCKET in = socket(AF_INET, SOCK_DGRAM, 0);

    sockaddr_in serverHint;
    serverHint.sin_addr.S_un.S_addr = ADDR_ANY;
    serverHint.sin_family = AF_INET;
    serverHint.sin_port = htons(myPort);

    bind(in, (sockaddr *) &serverHint, sizeof(serverHint));

    sockaddr_in client;
    int clientLength = sizeof(client);

    char recvbuf[DEFAULT_BUFLEN];
    memset(recvbuf, 0, sizeof recvbuf);
    ZeroMemory(&client, clientLength);

    recvfrom(in, recvbuf, 1024, 0, (sockaddr *) &client, &clientLength);
    closesocket(in);
    WSACleanup();

    return recvbuf;
}

void multicastInfo(string myId) {
    WSADATA wsaData;
    WSAStartup(MAKEWORD(2, 2), &wsaData);
    const char *sendbuf = myId.c_str();

    sockaddr_in server;
    server.sin_family = AF_INET;
    server.sin_port = htons(4242);
    server.sin_addr.S_un.S_addr = inet_addr("224.3.29.71");

    SOCKET out = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);

    bind(out, (SOCKADDR *) &server, sizeof(server));

    setsockopt(out, IPPROTO_IP, IP_MULTICAST_TTL, 0, 0);

    sendto(out, sendbuf, sizeof(sendbuf) + 1, 0, (sockaddr *) &server, sizeof(server));
    closesocket(out);
    WSACleanup();
}