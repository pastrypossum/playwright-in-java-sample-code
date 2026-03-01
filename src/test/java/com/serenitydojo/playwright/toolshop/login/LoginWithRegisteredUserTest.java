package com.serenitydojo.playwright.toolshop.login;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.serenitydojo.playwright.toolshop.actions.api.UserAPIClient;
import com.serenitydojo.playwright.toolshop.domain.User;
import com.serenitydojo.playwright.toolshop.fixtures.ChromeHeadlessOptions;
import net.serenitybdd.annotations.Feature;
import net.serenitybdd.annotations.Story;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.playwright.junit5.SerenityPlaywrightExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SerenityJUnit5Extension.class)
@ExtendWith(SerenityPlaywrightExtension.class)
@UsePlaywright(ChromeHeadlessOptions.class)
@Feature("Login")
@Story("Login with a registered user")
public class LoginWithRegisteredUserTest {

    @BeforeEach
    void setUp(Page page) {
        PlaywrightSerenity.registerPage(page);
    }

    @Test
    @DisplayName("Should be able to login with a registered user")
    void should_login_with_registered_user(Page page) {
        // Register a user via the API
        User user = User.randomUserNamed("Reg");
        UserAPIClient userAPIClient = new UserAPIClient(page);
        userAPIClient.registerUser(user);

        // Login via the login page
        LoginPage loginPage = new LoginPage(page);
        loginPage.open();
        loginPage.loginAs(user);

        // Check that we are on the right account page
        assertThat(loginPage.title()).isEqualTo("My account");
    }

    @Test
    @DisplayName("Should reject a user if they provide a wrong password")
    void should_reject_user_with_invalid_password(Page page) {
        User user = User.randomUserNamed("Reg");
        UserAPIClient userAPIClient = new UserAPIClient(page);
        userAPIClient.registerUser(user);

        LoginPage loginPage = new LoginPage(page);
        loginPage.open();
        loginPage.loginAs(user.withPassword("wrong-password"));

        assertThat(loginPage.loginErrorMessage()).isEqualTo("Invalid email or password");
    }
}
