import json
import sys
from random import randint
from threading import Thread

import Ice
import grpc

import Bank
import bank_pb2
import bank_pb2_grpc

Ice.loadSlice('bank.ice')

currencyNames = []
currencyTable = []
accounts = []

currencyPath = '../Utils/currency.json'

premiumThreshold = 5000
loanInterest = 1.10


def getNewPassword():
    return 'admin' + str(randint(0, 9)) + str(randint(0, 9))


class AccountI(Bank.Account):
    def __init__(self, pesel, name, surname, income):
        self.pesel = pesel
        self.name = name
        self.surname = surname
        self.income = income
        self.type = Bank.AccountType.PREMIUM if income >= premiumThreshold else Bank.AccountType.STANDARD
        self.password = getNewPassword()

    def getPesel(self):
        return self.pesel

    def getPassword(self):
        return self.password

    def getState(self, current=None):
        print('Account state requested by: ' + str(self.pesel))
        return Bank.AccountData(self.pesel, self.name, self.surname, self.income, self.type, self.password)

    def requestLoan(self, currency, loanAmount, months, current=None):
        print('Login requested by: ' + str(self.pesel))
        if self.type == Bank.AccountType.STANDARD:
            raise Bank.AccountException('Requesting a loan requires a PREMIUM account!')
        else:
            try:
                foreignCurrencyValue = next(
                    currencyIter['value'] for currencyIter in currencyTable if currencyIter['type'] == currency.name
                )
                return Bank.LoanRates(loanAmount * loanInterest, loanAmount * loanInterest * foreignCurrencyValue)
            except StopIteration:
                raise Bank.AccountException('Please provide a valid currency!')


class AccountFactoryI(Bank.AccountFactory):
    def signUp(self, pesel, name, surname, income, current=None):
        print('Registration requested by: ' + str(pesel))
        if any([pesel == account.getPesel() for account in accounts]):
            raise Bank.AccountException('Already signed up!')

        account = AccountI(pesel, name, surname, income)
        accounts.append(account)

        return account.getPassword()

    def signIn(self, pesel, password, current=None):
        print('Login requested by: ' + str(pesel))
        for account in accounts:
            if pesel == account.getPesel() and password == account.getPassword():
                return Bank.AccountPrx.uncheckedCast(current.adapter.addWithUUID(account))
            else:
                raise Bank.AccountException('Please provide correct login info')


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
    # adapter.add(AccountI(), Ice.stringToIdentity("standard"))
    adapter.add(AccountFactoryI(), Ice.stringToIdentity("factory"))

    adapter.activate()
    communicator.waitForShutdown()


if __name__ == '__main__':
    with open(currencyPath, 'r') as f:
        currencyNames = json.load(f)

    Thread(target=currencyService).start()
    Thread(target=bankServer).start()
