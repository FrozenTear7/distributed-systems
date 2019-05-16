import sys

import Ice

import Bank


def getFromEnum(currency):
    if currency == 'PLN':
        return Bank.Currency.PLN
    elif currency == 'USD':
        return Bank.Currency.USD
    elif currency == 'EURO':
        return Bank.Currency.EURO
    else:
        raise Bank.AccountException('Provide a correct currency')


def readArg(type=None):
    if type == 'int':
        while True:
            try:
                return int(sys.stdin.readline()[:-1])
            except ValueError:
                print('Please provide a correct argument')
    elif type == 'float':
        while True:
            try:
                return float(sys.stdin.readline()[:-1])
            except ValueError:
                print('Please provide a correct argument')
    else:
        return sys.stdin.readline()[:-1]


def runClient():
    communicator = Ice.initialize()
    bank = Bank.BankHandlerPrx.checkedCast(
        communicator.stringToProxy("bank:default -h localhost -p " + sys.argv[1]))
    clientInterface(bank)
    communicator.destroy()


def clientInterface(bank):
    while True:
        print('1 - sign up, 2 - account state, 3 - loan, q - exit')
        option = sys.stdin.readline()[:-1]
        if option == '1':
            print('Please provide: pesel name surname income - to sign up')

            pesel = readArg('int')
            name = readArg()
            surname = readArg()
            income = readArg('int')

            try:
                password = bank.signUp(pesel, name, surname, income)
                print('Account created! Your password: ' + password)
            except Bank.AccountException as e:
                print(e.message)
        elif option == '2':
            print('Please provide: pesel password - to get the account state')
            pesel = readArg()
            password = readArg()

            try:
                state = bank.getState(context={'pesel': pesel, 'password': password})
                print("Account state:\nPesel: {0}, Name: {1}, Surname: {2}, Income: {3}, Type: {4}, Password: {5}"
                      .format(state.pesel, state.name, state.surname, state.income, state.type, state.password))
            except Bank.AccountException as e:
                print(e.message)
        elif option == '3':
            print('Please provide: pesel password currency loanAmount months - to request a loan')
            pesel = readArg()
            password = readArg()
            currency = readArg()
            loanAmount = readArg('float')
            months = readArg('int')

            try:
                currency = getFromEnum(currency)
                loan = bank.requestLoan(currency, loanAmount, months, context={'pesel': pesel, 'password': password})
                print('Loan rate: {0}, {1} rate: {2}'.format(loan.loanPayment, currency,
                                                             loan.loanForeignPayment))
            except Bank.AccountException as e:
                print(e.message)
        elif option == 'q':
            return
        else:
            print('Please provide a correct argument')


if __name__ == '__main__':
    runClient()
