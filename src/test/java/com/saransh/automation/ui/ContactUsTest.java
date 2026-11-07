package com.saransh.automation.ui;

import com.saransh.automation.base.BaseUiTest;
import com.saransh.automation.pages.ContactUsPage;
import com.saransh.automation.pages.HomePage;
import com.saransh.automation.utils.JsonDataReader;
import com.saransh.automation.utils.TestDataFactory;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Contact Us")
public class ContactUsTest extends BaseUiTest {

    @Test(groups = {"ui", "regression"}, description = "Submit the contact form with a file attachment")
    public void submitContactFormWithAttachment() throws URISyntaxException {
        Map<String, String> data = JsonDataReader.readList("testdata/contact-us.json").get(0);
        Path attachment = Path.of(getClass().getClassLoader().getResource("testdata/upload-sample.txt").toURI());

        ContactUsPage page = new HomePage(driver()).open().goToContactUs();
        assertThat(page.heading()).isEqualToIgnoringCase("Get In Touch");

        page.submit(data.get("name"), TestDataFactory.uniqueEmail(), data.get("subject"), data.get("message"), attachment);

        assertThat(page.successMessage()).isEqualTo("Success! Your details have been submitted successfully.");
    }
}
