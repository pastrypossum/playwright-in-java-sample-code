package com.serenitydojo.playwright.toolshop.fixtures;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.AfterEach;

public interface TakesFinalScreenshot {

    @AfterEach
    default void takeScreenshot(Page page)  {
        page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
    }
}
