import grpc
import json

import bank_pb2
import bank_pb2_grpc

currencyNames = []
currencyTable = []

currencyPath = '../Utils/currency.json'


def currencyService():
    with grpc.insecure_channel('localhost:8080') as channel:
        stub = bank_pb2_grpc.CurrencyServiceStub(channel)
        call = stub.getCurrency(bank_pb2.CurrencyRequest(currency=['EURO', 'USD']))

        for response in call:
            newCurrencyTable = []

            for currency in response.currencyTable:
                newCurrencyTable.append({'type': currencyNames[currency.currency], 'value': currency.value})

            currencyTable = newCurrencyTable
            print(currencyTable)


if __name__ == '__main__':
    with open(currencyPath, 'r') as f:
        currencyNames = json.load(f)

    currencyService()
