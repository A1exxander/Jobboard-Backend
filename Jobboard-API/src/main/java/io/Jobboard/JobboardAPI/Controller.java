package io.Jobboard.JobboardAPI;

import io.Jobboard.DB.QueryValidator;
import io.Jobboard.User.*;
import io.Jobboard.Mail.*;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.HashMap;
import javax.mail.MessagingException;


@RestController
public final class Controller { // Possibly better for each mapping to have their own controller but probably a bit overkill

    private Model model = new Model();
    private View view = new View(); // We need a view as our getters for some classes such as user return an enum & not key-val pair like we want
    private UserTokens userTokens = UserTokens.getInstance();

    Controller() throws SQLException, InterruptedException {}

    @PostMapping("/login")
    public HashMap<String, String> login(@RequestHeader String Email, @RequestHeader String Password) throws SQLException { // Take headers not params as we do not want to expose credentials through the query params

        if(QueryValidator.validQueryArgs(new String[]{Email, Password}) && model.authenticate(Email, Password)) {
            User user = model.getUserData(Email);
            return View.getUserData(user); // Unsure if I should make this another GET endpoint ie retrieveUserData which takes token & in this function just return a token, but I think this is fine.
        }
        else {
            return View.error();
        }

    }

    @PutMapping("/register")
    public HashMap<String, String> register(@RequestHeader String Email, @RequestHeader String Password, @RequestParam String account_type) throws SQLException, MessagingException {

        if (!QueryValidator.validQueryArgs(new String[]{Email, Password, account_type}) || model.emailExists(Email) ){
            return View.error();
        }

        User user = new User(Email, Token.genToken(), account_type);
        model.registerUser(user, Password);
        userTokens.createTokenUserPair(user.getToken(), user.getEmail());
        return View.getUserData(user);

    }

    @PutMapping("/forgot-password")
    public void createPasswordResetToken(@RequestHeader String Email) throws SQLException, MessagingException {

        if ( !QueryValidator.validQueryArgs(new String[]{Email}) || !model.emailExists(Email) || model.passwordResetTokenExists(Email)) {
            return;
        }
        else {
            String Token = model.genPasswordResetToken(Email);
            Mail m = new Mail();
            m.sendMessage(Email, EmailTemplates.RESET_PASSWORD);
        }

    }

    @GetMapping("/check-password-token") // Used after submitting a /forgot-password request
    public HashMap<String, String> validatePasswordResetToken(@RequestHeader String Token) throws SQLException, MessagingException { // Take headers not params as we do not want to expose credentials through the query params

        if(QueryValidator.validQueryArgs(new String[]{Token}) && model.passwordResetTokenValid(Token)) {
            return View.valid();
        }
        else {
            return View.error();
        }

    }

    @GetMapping("/contracts")
    public HashMap<String, Object> getContracts() throws SQLException {

        return View.getJobContracts(model.getContracts());

    }

    @GetMapping("/contracts/{email}") // Unsure if I should do /{email}/contracts/ or /contracts/{email}, I think /{email}/contracts/ since contracts is a relation where each user has contracts.
    public HashMap<String, Object> getContracts(@RequestHeader String Token, @PathVariable String email, @RequestParam String account_type, @RequestParam(required = false) Boolean is_completed) throws SQLException { // There is sadly no simple way to make one parameter required only if the other one is present

            if (!model.emailExists(email)){
                return null;
            }

            if (is_completed == null || is_completed == false ) {
                return View.getJobContracts(model.getContracts(Token, email, account_type));
            }
            else {
                return View.getJobContracts(model.getCompletedContracts(Token, email, account_type));
            }
    }

    @PutMapping("select_contract/{jobid}")
    public void selectContract(@RequestHeader String Token, @PathVariable int jobid) throws SQLException { // There is sadly no simple way to make one parameter required only if the other one is present

        if (userTokens.tokenExists(Token)){
            model.selectContract(Token, userTokens.getUsername(Token), jobid);
        }

    }

    @PutMapping("complete_contract/{jobid}")
    public void completeContract(@RequestHeader String Token, @PathVariable int jobid) throws SQLException {
        if (userTokens.tokenExists(Token)) {
            model.completeContract(Token, userTokens.getUsername(Token), jobid);
        }
    }

    @PutMapping("create_contract")
    public HashMap<String, Integer> createContract(@RequestHeader String Token, @RequestParam int days, @RequestParam String description ) throws SQLException { // Returns job contract
        if (userTokens.tokenExists(Token)) {
            return View.createContract(model.createContract(Token, userTokens.getUsername(Token), days, description));
        }
        else {
            return null;
        }
    }

    @DeleteMapping("delete_contract/{jobid}")
    public void deleteContract(@RequestHeader String Token, @RequestParam int jobid) throws SQLException {
        model.deleteContract(jobid);
    }

}
