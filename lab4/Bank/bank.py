import Ice
import json
import sys
import traceback
from threading import Thread

import grpc

import Demo
import bank_pb2
import bank_pb2_grpc

Ice.loadSlice('bank.ice')

currencyNames = []
currencyTable = []

currencyPath = '../Utils/currency.json'


class BankI(Demo.Hello):
    def sayHello(self, current=None):
        print("Hello World!")


def currencyService():
    with grpc.insecure_channel('localhost:8080') as channel:
        stub = bank_pb2_grpc.CurrencyServiceStub(channel)
        call = stub.getCurrency(bank_pb2.CurrencyRequest(currency=sys.argv[2:]))

        for response in call:
            newCurrencyTable = []

            for currency in response.currencyTable:
                newCurrencyTable.append({'type': currencyNames[currency.currency], 'value': currency.value})

            currencyTable = newCurrencyTable
            print(currencyTable)


def bankServer():
    try:
        communicator = Ice.initialize()
        adapter = communicator.createObjectAdapterWithEndpoints("Bank", "default -h localhost -p " + sys.argv[1])
        adapter.add(BankI(), communicator.stringToIdentity("bank"))
        adapter.activate()
        communicator.waitForShutdown()
        communicator.destroy()
    except:
        traceback.print_exc()
        sys.exit(1)


if __name__ == '__main__':
    with open(currencyPath, 'r') as f:
        currencyNames = json.load(f)

    Thread(target=currencyService).start()
    Thread(target=bankServer).start()
