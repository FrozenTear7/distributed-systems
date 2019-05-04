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


def runClient():
    communicator = Ice.initialize()
    bank = Bank.AccountFactoryPrx.checkedCast(
        communicator.stringToProxy("factory:default -h localhost -p " + sys.argv[1]))
    clientInterface(bank)
    communicator.destroy()


def clientInterface(bank):
    account = None
    password = None

    while True:
        print('1 - sign up, 2 - sign in, q - exit')
        option = sys.stdin.readline()[:-1]
        if option == '1':
            print('Please provide: pesel name surname income - to sign up')
            # pesel = int(sys.stdin.readline()[:-1])
            # name = sys.stdin.readline()[:-1]
            # surname = sys.stdin.readline()[:-1]
            # income = int(sys.stdin.readline()[:-1])

            try:
                # password = bank.signUp(pesel, name, surname, income)
                password = bank.signUp(123, "Pan", "Dupciol", 10001)
                print('Account created! Your password: ' + password + '\n')
            except Bank.AccountException as e:
                print(e.message + '\n')
        elif option == '2':
            print('Please provide: pesel password - to sign in')
            # pesel = int(sys.stdin.readline()[:-1])
            # password = sys.stdin.readline()[:-1]

            try:
                print('Signed in!\n')
                # account = bank.signIn(pesel, password)
                account = bank.signIn(123, password)
            except Bank.AccountException as e:
                print(e.message + '\n')

            while True:
                print('1 - get account state, 2 - request a loan, q - exit')
                option = sys.stdin.readline()[:-1]

                if option == '1':
                    state = account.getState()
                    print("Account state:\nPesel: {0}, Name: {1}, Surname: {2}, Income: {3}, Type: {4}, Password: {5}"
                          .format(state.pesel, state.name, state.surname, state.income, state.type, state.password))
                elif option == '2':
                    print('Please provide: currency loanAmount months - to request a loan')
                    # currency = sys.stdin.readline()[:-1]
                    # loanAmount = float(sys.stdin.readline()[:-1])
                    # months = int(sys.stdin.readline()[:-1])

                    try:
                        currency = getFromEnum('EURO')
                        loan = account.requestLoan(currency, 10000, 20)
                        print('Loan rate: {0}, {1} rate: {2}'.format(loan.loanPayment, Bank.Currency.EURO.name,
                                                                     loan.loanForeignPayment))
                    except Bank.AccountException as e:
                        print(e.message + '\n')
                elif option == 'q':
                    break
        elif option == 'q':
            return
        else:
            print('Please provide a correct argument\n')


if __name__ == '__main__':
    runClient()
