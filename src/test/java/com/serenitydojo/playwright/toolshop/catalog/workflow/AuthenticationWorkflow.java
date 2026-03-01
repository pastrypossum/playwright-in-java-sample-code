package com.serenitydojo.playwright.toolshop.catalog.workflow;

import com.microsoft.playwright.Page;
import com.serenitydojo.playwright.toolshop.actions.api.UserAPIClient;
import com.serenitydojo.playwright.toolshop.domain.User;
import com.serenitydojo.playwright.toolshop.login.LoginPage;
import net.serenitybdd.annotations.Step;

public class AuthenticationWorkflow {

    private final UserAPIClient userAPI;
    private final LoginPage loginPage;

    public AuthenticationWorkflow(Page page) {
        this.userAPI = new UserAPIClient(page);
        this.loginPage = new LoginPage(page);
    }

    @Step("Register a new user called '{0}'")
    public User registerUserCalled(String firstName) {
        User someUser = User.randomUserNamed(firstName);
        userAPI.registerUser(someUser);
        return someUser;
    }

    @Step("Log in as {0}")
    public void loginAs(User user) {
        loginPage.open();
        loginPage.loginAs(user);
    }
}
