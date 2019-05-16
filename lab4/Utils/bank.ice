module Bank {
    enum AccountType { STANDARD, PREMIUM };

    enum Currency { PLN, USD, EURO };

    struct Account {
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

    interface BankHandler {
        string signUp(long pesel, string name, string surname, int income) throws AccountException;
        Account getState() throws AccountException;
        LoanRates requestLoan(Currency currency, float loanAmount, int months) throws AccountException;
    };
};