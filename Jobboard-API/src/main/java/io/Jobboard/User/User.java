package io.Jobboard.User;

public class User {

    private String email;
    private Token token;
    private AccountTypes accountType;

    public User() {}

    public User(String username, String token, String accountType) {

         this.email = username;
         this.token = new Token(token);
         this.accountType = AccountTypes.valueOf(accountType);

    }

    public String getEmail() {
        return email;
    }

    public String getAccountTypeString() {
        return accountType.toString();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAccountType(AccountTypes accountType) {
        this.accountType = accountType;
    }

    public AccountTypes getAccountType() {
        return accountType;
    }

    public void setToken(String token) {
        this.token.setToken(token);
    }

    public String getToken() {
        return token.getToken();
    }
}
