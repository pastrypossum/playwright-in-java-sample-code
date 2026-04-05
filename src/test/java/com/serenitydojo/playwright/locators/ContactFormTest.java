package com.serenitydojo.playwright.locators;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.serenitydojo.playwright.PlaywrightTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ContactFormTest extends PlaywrightTest {

    @DisplayName("Submit contact form")
    @Nested
    public class CompleteContactForm {

        @BeforeEach
        public void setUp() {
            page.navigate("https://practicesoftwaretesting.com/contact");
        }

        @DisplayName("Completed contact form")
        @Test
        void addCorrectContact() throws URISyntaxException {

            var firstName = page.getByLabel("First Name");
            var lastName = page.getByLabel("Last Name");
            var email = page.getByLabel("Email address");
            var subject = page.getByLabel("Subject");
            var message = page.getByLabel("Message");
            var upload = page.getByLabel("Attachment");

            Path filePath = Paths.get(ClassLoader.getSystemClassLoader().getResource("data/test.txt").toURI());

            firstName.fill("Robert");
            lastName.fill("Smith");
            email.fill("rsmith@foogoo.com");
            subject.selectOption("Warranty");
            message.fill("Hello, World! Hello, World! Hello, World! Hello, World! Hello, World! Hello, World! Hello, World! Hello, World! Hello, World! Hello, World! Hello, World! Hello, World!");
            page.setInputFiles("#attachment", filePath);
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Send")).click();

            PlaywrightAssertions.assertThat(page.getByText(" Thanks for your message! We will contact you shortly. ")).isVisible();
        }

        @DisplayName("Incompleted contact form")
        @Test
        void addIncorrectContactAll() throws URISyntaxException {

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Send")).click();

            List<String> alerts = page.getByRole(AriaRole.ALERT).allTextContents();

            PlaywrightAssertions.assertThat(
                    page.getByRole(AriaRole.ALERT).getByText("First name is required")).isVisible();
            PlaywrightAssertions.assertThat(
                    page.getByRole(AriaRole.ALERT).getByText("Last name is required")).isVisible();
            PlaywrightAssertions.assertThat(
                    page.getByRole(AriaRole.ALERT).getByText("Email is required")).isVisible();
            PlaywrightAssertions.assertThat(
                    page.getByRole(AriaRole.ALERT).getByText("Message is required")).isVisible();
        }

        @DisplayName("Missing required values")
        @ParameterizedTest
        @ValueSource(strings = {"Fist Name", "Last Name", "Email", "Message"})
        void addIncorrectContactSingle(String fieldname) throws URISyntaxException {

            var firstName = page.getByLabel("First Name");
            var lastName = page.getByLabel("Last Name");
            var email = page.getByLabel("Email address");
            var subject = page.getByLabel("Subject");
            var message = page.getByLabel("Message");

            Path filePath = Paths.get(ClassLoader.getSystemClassLoader().getResource("data/test.txt").toURI());

            firstName.fill("Robert");
            lastName.fill("Smith");
            email.fill("rsmith@foogoo.com");
            subject.selectOption("Warranty");
            message.fill("Hello, World! Hello, World! Hello, World! Hello, World! Hello, World! Hello, World! Hello, World! Hello, World! Hello, World! Hello, World! Hello, World! Hello, World!");
            page.setInputFiles("#attachment", filePath);

            page.getByLabel(fieldname).clear();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Send")).click();

            PlaywrightAssertions.assertThat(
                    page.getByRole(AriaRole.ALERT).getByText(fieldname + " is required")).isVisible();
        }
    }
}
