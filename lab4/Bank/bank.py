import json
import sys
from random import randint
from threading import Thread

import Ice
import grpc

import Bank
import bank_pb2
import bank_pb2_grpc

currencyNames = []
currencyTable = []
accounts = []

currencyPath = '../Utils/currency.json'

premiumThreshold = 5000
loanInterest = 1.10


def getNewPassword():
    return 'admin' + str(randint(0, 9)) + str(randint(0, 9))


def checkSignIn(pesel, password):
    for account in accounts:
        if pesel == account.pesel and password == account.password:
            return account
        else:
            return False


class BankHandlerI(Bank.BankHandler):
    def signUp(self, pesel, name, surname, income, current=None):
        print('Registration requested by: ' + str(pesel))
        if any([pesel == account.pesel for account in accounts]):
            raise Bank.AccountException('Already signed up!')

        account = Bank.Account(pesel, name, surname, income,
                               Bank.AccountType.PREMIUM if income >= premiumThreshold else Bank.AccountType.STANDARD,
                               getNewPassword())
        accounts.append(account)

        return account.password

    def getState(self, current=None):
        print('Account state requested by: ' + current.ctx['pesel'])

        try:
            account = checkSignIn(int(current.ctx['pesel']), current.ctx['password'])
            if not account:
                raise Bank.AccountException('Please provide correct login info')
            else:
                return Bank.Account(account.pesel, account.name, account.surname, account.income, account.type, account.password)
        except ValueError:
                raise Bank.AccountException('Please provide correct arguments')

    def requestLoan(self, currency, loanAmount, months, current=None):
        print('Login requested by: ' + current.ctx['pesel'])

        account = checkSignIn(int(current.ctx['pesel']), current.ctx['password'])

        if not account:
            raise Bank.AccountException('Please provide correct login info')
        else:
            if account.type == Bank.AccountType.STANDARD:
                raise Bank.AccountException('Requesting a loan requires a PREMIUM account!')
            else:
                try:
                    foreignCurrencyValue = next(
                        currencyIter['value'] for currencyIter in currencyTable if currencyIter['type'] == currency.name
                    )
                    return Bank.LoanRates(loanAmount * loanInterest, loanAmount * loanInterest * foreignCurrencyValue)
                except StopIteration:
                    raise Bank.AccountException('Please provide a valid currency!')


def currencyService():
    with grpc.insecure_channel('localhost:8080') as channel:
        stub = bank_pb2_grpc.CurrencyServiceStub(channel)
        call = stub.getCurrency(bank_pb2.CurrencyRequest(currency=sys.argv[2:]))

        for response in call:
            newCurrencyTable = []

            for currency in response.currencyTable:
                newCurrencyTable.append({'type': currencyNames[currency.currency], 'value': currency.value})

            global currencyTable
            currencyTable = newCurrencyTable


def bankServer():
    communicator = Ice.initialize()
    adapter = communicator.createObjectAdapterWithEndpoints("Bank", "default -h localhost -p " + sys.argv[1])
    adapter.add(BankHandlerI(), Ice.stringToIdentity("bank"))

    adapter.activate()
    communicator.waitForShutdown()


if __name__ == '__main__':
    with open(currencyPath, 'r') as f:
        currencyNames = json.load(f)

    Thread(target=currencyService).start()
    Thread(target=bankServer).start()
