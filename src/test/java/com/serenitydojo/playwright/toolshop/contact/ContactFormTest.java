package com.serenitydojo.playwright.toolshop.contact;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.NavBar;
import com.serenitydojo.playwright.toolshop.fixtures.ChromeHeadlessOptions;
import com.serenitydojo.playwright.toolshop.fixtures.TakesFinalScreenshot;
import com.serenitydojo.playwright.toolshop.fixtures.WithTracing;
import net.serenitybdd.annotations.Feature;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Story;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.playwright.junit5.SerenityPlaywrightExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@ExtendWith(SerenityJUnit5Extension.class)
@ExtendWith(SerenityPlaywrightExtension.class)
@UsePlaywright(ChromeHeadlessOptions.class)
@DisplayName("Contact form")
@Feature("Contacts")
public class ContactFormTest implements TakesFinalScreenshot, WithTracing {

    @Steps
    ContactForm contactForm;

    @Steps
    NavBar navigate;

    @BeforeEach
    void openContactPage(Page page) {
        PlaywrightSerenity.registerPage(page);
        navigate.toTheContactPage();
    }

    @Nested
    @Story("Contact form")
    class WhenSubmittingTheForm {
        @DisplayName("Customers can use the contact form to contact us")
        @Test
        void completeForm() throws URISyntaxException {
            contactForm.setFirstName("Sarah-Jane");
            contactForm.setLastName("Smith");
            contactForm.setEmail("sarah@example.com");
            contactForm.setMessage("A very long message to the warranty service about a warranty on a product!");
            contactForm.selectSubject("Warranty");

            Path fileToUpload = Paths.get(ClassLoader.getSystemResource("data/sample-data.txt").toURI());
            contactForm.setAttachment(fileToUpload);

            contactForm.submitForm();

            assertThat(contactForm.alertMessage()).isVisible();
            assertThat(contactForm.alertMessage()).hasText("Thanks for your message! We will contact you shortly.");
        }

        @DisplayName("First name, last name, email and message are mandatory")
        @ParameterizedTest(name = "{arguments} is a mandatory field")
        @ValueSource(strings = {"First name", "Last name", "Email", "Message"})
        void mandatoryFields(String fieldName) {
            // Fill in the field values
            contactForm.setFirstName("Sarah-Jane");
            contactForm.setLastName("Smith");
            contactForm.setEmail("sarah@example.com");
            contactForm.setMessage("A very long message to the warranty service about a warranty on a product!");
            contactForm.selectSubject("Warranty");

            // Clear one of the fields
            contactForm.clearField(fieldName);

            contactForm.submitForm();

            // Check the error message for that field
            assertThat(contactForm.alertMessage()).isVisible();
            assertThat(contactForm.alertMessage()).hasText(fieldName + " is required");
        }

        @DisplayName("The message must be at least 50 characters long")
        @Test
        void messageTooShort() {

            contactForm.setFirstName("Sarah-Jane");
            contactForm.setLastName("Smith");
            contactForm.setEmail("sarah@example.com");
            contactForm.setMessage("A short long message.");
            contactForm.selectSubject("Warranty");

            contactForm.submitForm();

            assertThat(contactForm.alertMessage()).isVisible();
            assertThat(contactForm.alertMessage()).hasText("Message must be minimal 50 characters");
        }

        @DisplayName("The email address must be correctly formatted")
        @ParameterizedTest(name = "'{arguments}' should be rejected")
        @ValueSource(strings = {"not-an-email", "not-an.email.com", "notanemail"})
        void invalidEmailField(String invalidEmail) {
            contactForm.setFirstName("Sarah-Jane");
            contactForm.setLastName("Smith");
            contactForm.setEmail(invalidEmail);
            contactForm.setMessage("A very long message to the warranty service about a warranty on a product!");
            contactForm.selectSubject("Warranty");

            contactForm.submitForm();

            assertThat(contactForm.alertMessage()).isVisible();
            assertThat(contactForm.alertMessage()).hasText("Email format is invalid");
        }
    }
}
