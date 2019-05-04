module Bank {
    enum AccountType { STANDARD, PREMIUM };

    enum Currency { PLN, USD, EURO };

    struct AccountData {
        int pesel;
        string name;
        string surname;
        int income;
        AccountType type;
        string password;
    };

    struct LoanRates {
        float loanPayment;
        float loanForeignPayment;
    };

    exception AccountException {
        string message;
    };

    interface Account {
        AccountData getState();
        LoanRates requestLoan(Currency currency, float loanAmount, int months) throws AccountException;
    };

    interface AccountFactory {
        string signUp(long pesel, string name, string surname, int income) throws AccountException;
        Account *signIn(long pesel, string password) throws AccountException;
    };
};