# Migrating Playwright Tests from Allure to Serenity BDD

This guide walks through every step required to migrate a Playwright-for-Java test suite from vanilla Playwright + Allure reporting to Playwright with Serenity BDD. It is based on a real migration of the [Practice Software Testing](https://practicesoftwaretesting.com) test suite.

## Table of Contents

1. [Overview](#1-overview)
2. [Update Maven Dependencies](#2-update-maven-dependencies)
3. [Add Serenity Configuration](#3-add-serenity-configuration)
4. [Migrate Test Classes](#4-migrate-test-classes)
5. [Migrate Page Objects](#5-migrate-page-objects)
6. [Migrate Workflow Objects](#6-migrate-workflow-objects)
7. [Remove Allure Screenshot Infrastructure](#7-remove-allure-screenshot-infrastructure)
8. [Migrate Cucumber BDD Tests](#8-migrate-cucumber-bdd-tests)
9. [Remove the PlaywrightTestCase Base Class](#9-remove-the-playwrighttestcase-base-class)
10. [Update Maven Plugins](#10-update-maven-plugins)
11. [Summary of Import Replacements](#11-summary-of-import-replacements)
12. [Checklist](#12-checklist)

---

## 1. Overview

### What changes

| Concern | Allure | Serenity BDD |
|---|---|---|
| Reporting | Allure reports via `allure-maven` | Serenity reports via `serenity-maven-plugin` |
| `@Step` interception | AspectJ weaver (`-javaagent:aspectjweaver`) | Serenity's built-in instrumentation (no AspectJ) |
| `@Feature` / `@Story` | `io.qameta.allure` annotations | `net.serenitybdd.annotations` annotations |
| Screenshots | Manual via `Allure.addAttachment()` | Automatic — Serenity captures them for you |
| Page object injection | Manual `new PageObject(page)` construction | `@Steps` annotation with dependency injection |
| Playwright lifecycle | Manual `ThreadLocal` management or `@UsePlaywright` only | `SerenityPlaywrightExtension` + `PlaywrightSerenity.registerPage()` |
| Cucumber integration | `AllureCucumber7Jvm` plugin | `SerenityReporterParallel` plugin + `PlaywrightBrowserManager` |

### What stays the same

- `@UsePlaywright(ChromeHeadlessOptions.class)` — still used for Playwright browser configuration
- Page object structure — constructors still accept `Page`, methods still use Playwright locators
- `@DisplayName`, `@Nested`, `@Test` — JUnit 5 annotations unchanged
- `WithTracing` interface mixin — continues to work as before
- AssertJ assertions — unchanged
- Test structure and organisation — unchanged

---

## 2. Update Maven Dependencies

### 2.1 Remove Allure dependencies

Remove the Allure BOM and all Allure artifacts:

```xml
<!-- REMOVE all of these -->
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-bom</artifactId>
    <version>${allure.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>

<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-junit5</artifactId>
</dependency>
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-junit-platform</artifactId>
</dependency>
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-cucumber7-jvm</artifactId>
</dependency>
```

Also remove the `allure.version` property and delete `src/test/resources/allure.properties`.

### 2.2 Add Serenity BDD dependencies

Add a Serenity version property and the four Serenity modules:

```xml
<properties>
    <serenity.version>5.3.4-SNAPSHOT</serenity.version>
</properties>

<dependencies>
    <!-- Serenity BDD -->
    <dependency>
        <groupId>net.serenity-bdd</groupId>
        <artifactId>serenity-core</artifactId>
        <version>${serenity.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>net.serenity-bdd</groupId>
        <artifactId>serenity-junit5</artifactId>
        <version>${serenity.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>net.serenity-bdd</groupId>
        <artifactId>serenity-playwright</artifactId>
        <version>${serenity.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>net.serenity-bdd</groupId>
        <artifactId>serenity-cucumber</artifactId>
        <version>${serenity.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 2.3 Add dependency management BOMs (recommended)

Use BOMs to manage JUnit and Cucumber versions centrally, so you can remove explicit version numbers from individual dependencies:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.junit</groupId>
            <artifactId>junit-bom</artifactId>
            <version>${junit.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-bom</artifactId>
            <version>${cucumber.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2.4 Remove the cucumber-junit dependency

Serenity Cucumber uses the JUnit Platform engine, so `cucumber-junit` (the JUnit 4 runner) is no longer needed:

```xml
<!-- REMOVE this -->
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit</artifactId>
</dependency>
```

Keep `cucumber-java` and `cucumber-junit-platform-engine`.

---

## 3. Add Serenity Configuration

Create `src/test/resources/serenity.conf`:

```hocon
serenity {
  project.name = "Playwright Tool Shop Tests"
  test.root = "com.serenitydojo.playwright.toolshop"
}
```

This HOCON file configures:
- `project.name` — the title shown in Serenity reports
- `test.root` — the root package for test discovery

---

## 4. Migrate Test Classes

This is the core of the migration. Each test class needs three changes:
1. Add Serenity extensions
2. Register the Playwright page
3. Replace manual page object construction with `@Steps` injection

### 4.1 Add Serenity JUnit extensions

Add two `@ExtendWith` annotations to every test class, before `@UsePlaywright`:

```java
// BEFORE
@DisplayName("Searching for products")
@Feature("Product Catalog")
@UsePlaywright(ChromeHeadlessOptions.class)
public class SearchForProductsTest { ... }

// AFTER
@ExtendWith(SerenityJUnit5Extension.class)
@ExtendWith(SerenityPlaywrightExtension.class)
@UsePlaywright(ChromeHeadlessOptions.class)
@DisplayName("Searching for products")
@Feature("Catalog")
public class SearchForProductsTest { ... }
```

Required imports:
```java
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.playwright.junit5.SerenityPlaywrightExtension;
import org.junit.jupiter.api.extension.ExtendWith;
```

### 4.2 Register the Playwright page in `@BeforeEach`

In your `@BeforeEach` method, call `PlaywrightSerenity.registerPage(page)`. This tells Serenity which `Page` instance to inject into `@Steps`-annotated fields:

```java
// BEFORE
@BeforeEach
void openHomePage(Page page) {
    page.navigate("https://practicesoftwaretesting.com");
}

// AFTER
@BeforeEach
void openHomePage(Page page) {
    PlaywrightSerenity.registerPage(page);
    page.navigate("https://practicesoftwaretesting.com");
}
```

Required import:
```java
import net.serenitybdd.playwright.PlaywrightSerenity;
```

### 4.3 Replace manual page object construction with `@Steps`

Instead of creating page objects manually with `new`, declare them as fields annotated with `@Steps`. Serenity will instantiate them and inject the registered `Page` automatically.

```java
// BEFORE — manual construction (in @BeforeEach or test methods)
SearchComponent searchComponent;
ProductList productList;

@BeforeEach
void setUp(Page page) {
    searchComponent = new SearchComponent(page);
    productList = new ProductList(page);
    page.navigate("https://practicesoftwaretesting.com");
}

// AFTER — @Steps injection
@Steps SearchComponent searchComponent;
@Steps ProductList productList;

@BeforeEach
void setUp(Page page) {
    PlaywrightSerenity.registerPage(page);
    page.navigate("https://practicesoftwaretesting.com");
}
```

Required import:
```java
import net.serenitybdd.annotations.Steps;
```

### 4.4 Remove `Page page` from test method parameters (optional)

If test methods were receiving `Page page` only to create page objects, that parameter can be removed since `@Steps` handles injection. However, if a test method needs `page` directly (e.g., for assertions on page state), it can keep the parameter.

### 4.5 Complete before/after example

**Before (Allure):**
```java
package com.serenitydojo.playwright.toolshop.catalog;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.ProductList;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.SearchComponent;
import com.serenitydojo.playwright.toolshop.fixtures.ChromeHeadlessOptions;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;

@DisplayName("Searching for products")
@Feature("Product Catalog")
@UsePlaywright(ChromeHeadlessOptions.class)
public class SearchForProductsTest {

    @BeforeEach
    void openHomePage(Page page) {
        page.navigate("https://practicesoftwaretesting.com");
    }

    @Nested
    @DisplayName("Searching by keyword")
    @Story("Searching by keyword")
    class SearchingByKeyword {

        @Test
        @DisplayName("When there are matching results")
        void whenSearchingByKeyword(Page page) {
            SearchComponent searchComponent = new SearchComponent(page);
            ProductList productList = new ProductList(page);

            searchComponent.searchBy("tape");

            var matchingProducts = productList.getProductNames();
            Assertions.assertThat(matchingProducts)
                .contains("Tape Measure 7.5m");
        }
    }
}
```

**After (Serenity BDD):**
```java
package com.serenitydojo.playwright.toolshop.catalog;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.ProductList;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.SearchComponent;
import com.serenitydojo.playwright.toolshop.fixtures.ChromeHeadlessOptions;
import net.serenitybdd.annotations.Feature;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Story;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.playwright.junit5.SerenityPlaywrightExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SerenityJUnit5Extension.class)
@ExtendWith(SerenityPlaywrightExtension.class)
@UsePlaywright(ChromeHeadlessOptions.class)
@DisplayName("Searching for products")
@Feature("Catalog")
public class SearchForProductsTest {

    @Steps SearchComponent searchComponent;
    @Steps ProductList productList;

    @BeforeEach
    void openHomePage(Page page) {
        PlaywrightSerenity.registerPage(page);
        page.navigate("https://practicesoftwaretesting.com");
    }

    @Nested
    @DisplayName("Searching by keyword")
    @Story("Searching by keyword")
    class SearchingByKeyword {

        @Test
        @DisplayName("When there are matching results")
        void whenSearchingByKeyword(Page page) {
            searchComponent.searchBy("tape");

            var matchingProducts = productList.getProductNames();
            Assertions.assertThat(matchingProducts)
                .contains("Tape Measure 7.5m");
        }
    }
}
```

---

## 5. Migrate Page Objects

Page objects require minimal changes. The constructor and method bodies remain identical — only the `@Step` import changes.

### 5.1 Swap the `@Step` import

```java
// BEFORE
import io.qameta.allure.Step;

// AFTER
import net.serenitybdd.annotations.Step;
```

The `@Step` annotation works identically — it marks methods as reportable steps in the generated report.

### 5.2 Add `@Step` annotations to unannotated methods (recommended)

Serenity reports benefit from having `@Step` on every public action method. If you had methods without `@Step` in the Allure version, this is a good time to add them. Serenity supports parameter placeholders using `{0}`, `{1}`, etc.:

```java
// Methods that previously had no @Step annotation:
@Step("Search for '{0}'")
public void searchBy(String keyword) { ... }

@Step("Filter by category '{0}'")
public void filterBy(String category) { ... }

@Step("View product details for '{0}'")
public void viewProductDetails(String productName) { ... }

@Step("Set product quantity to {0}")
public void setQuantityTo(int quantity) { ... }

@Step("Choose payment method '{0}'")
public void choosePaymentMethod(String method) { ... }
```

### 5.3 Remove any `ScreenshotManager` calls

If any page object methods called `ScreenshotManager.takeScreenshot()` (which used `Allure.addAttachment()`), remove those calls. Serenity captures screenshots automatically.

```java
// BEFORE
public void submitForm() {
    page.getByRole(AriaRole.BUTTON, buttonOptions("Submit")).click();
    ScreenshotManager.takeScreenshot(page, "Submit contact form");
}

// AFTER
@Step("Submit the contact form")
public void submitForm() {
    page.getByRole(AriaRole.BUTTON, buttonOptions("Submit")).click();
}
```

### 5.4 No constructor changes needed

Page object constructors remain unchanged. They still accept `Page` as a parameter:

```java
public class SearchComponent {
    private final Page page;

    public SearchComponent(Page page) {
        this.page = page;
    }
    // ... methods unchanged
}
```

Serenity's `@Steps` injection will call this constructor with the registered `Page` instance.

---

## 6. Migrate Workflow Objects

Workflow objects (higher-level abstractions that compose multiple page objects) follow the same pattern, but with one additional benefit: **transitive `@Steps` injection**.

### 6.1 Replace manual page object construction with `@Steps`

The workflow's internal page objects can also use `@Steps`. Serenity handles the recursive injection — when a workflow is created via `@Steps` in a test, its own `@Steps` fields are also injected.

```java
// BEFORE (Allure)
public class PurchaseWorkflow {
    Page page;
    NavBar navBar;
    SearchComponent searchComponent;
    ProductList productList;
    ProductDetails productDetails;
    ShoppingCart shoppingCart;
    AddressForm addressForm;
    PaymentForm paymentForm;

    public PurchaseWorkflow(Page page) {
        this.page = page;
        this.navBar = new NavBar(page);
        this.searchComponent = new SearchComponent(page);
        this.productList = new ProductList(page);
        this.productDetails = new ProductDetails(page);
        this.shoppingCart = new ShoppingCart(page);
        this.addressForm = new AddressForm(page);
        this.paymentForm = new PaymentForm(page);
    }
    // ...
}

// AFTER (Serenity BDD)
public class PurchaseWorkflow {
    Page page;
    @Steps NavBar navBar;
    @Steps SearchComponent searchComponent;
    @Steps ProductList productList;
    @Steps ProductDetails productDetails;
    @Steps ShoppingCart shoppingCart;
    @Steps AddressForm addressForm;
    @Steps PaymentForm paymentForm;

    public PurchaseWorkflow(Page page) {
        this.page = page;
    }
    // ...
}
```

### 6.2 Update `@Step` descriptions with parameters

```java
// BEFORE
@Step("Add product to cart")
public void addProductToCart(String productName, int quantity) { ... }

@Step("Choose payment method")
public void choosePaymentMethod(String paymentMethod) { ... }

// AFTER
@Step("Add {1} x '{0}' to cart")
public void addProductToCart(String productName, int quantity) { ... }

@Step("Choose payment method '{0}'")
public void choosePaymentMethod(String paymentMethod) { ... }
```

---

## 7. Remove Allure Screenshot Infrastructure

Serenity BDD captures screenshots automatically, so the manual Allure screenshot infrastructure can be removed entirely.

### 7.1 Delete Allure-specific classes

Delete these files:
- `ScreenshotManager.java` — utility that called `Allure.addAttachment()`
- `RecordsAllureScreenshots.java` — interface mixin that used `Allure.addAttachment()` in `@AfterEach`

### 7.2 Update `TakesFinalScreenshot`

If you have a `TakesFinalScreenshot` mixin, update it to remove Allure API calls:

```java
// BEFORE
public interface TakesFinalScreenshot {
    @AfterEach
    default void takeScreenshot(Page page) {
        ScreenshotManager.takeScreenshot(page, "Final screenshot");
    }
}

// AFTER
public interface TakesFinalScreenshot {
    @AfterEach
    default void takeScreenshot(Page page) {
        page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
    }
}
```

### 7.3 Remove implements clauses for deleted interfaces

If any test classes implemented `RecordsAllureScreenshots`, remove that from the `implements` clause.

---

## 8. Migrate Cucumber BDD Tests

### 8.1 Rename and update the Cucumber runner

Replace the Allure Cucumber plugin with Serenity's reporter:

```java
// BEFORE — CucumberTests.java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("/features")
@ConfigurationParameter(
    key = "cucumber.plugin",
    value = "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm," +
            "html:target/cucumber-reports/cucumber.html"
)
public class CucumberTests {}

// AFTER — CucumberTestSuite.java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("/features")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
    value = "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel,pretty")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME,
    value = "com.serenitydojo.playwright.toolshop")
public class CucumberTestSuite {}
```

Key changes:
- Class renamed from `CucumberTests` to `CucumberTestSuite`
- Plugin changed from `AllureCucumber7Jvm` to `SerenityReporterParallel`
- Added explicit `GLUE_PROPERTY_NAME` to point to the step definition packages
- Uses static imports from `io.cucumber.junit.platform.engine.Constants`

If you rename the class, update the failsafe `<includes>` pattern (see [Step 10](#10-update-maven-plugins)).

### 8.2 Replace `PlaywrightCucumberFixtures` with `PlaywrightHooks`

Replace the manual `ThreadLocal`-based lifecycle management with Serenity's `PlaywrightBrowserManager`:

```java
// BEFORE — PlaywrightCucumberFixtures.java (DELETED)
public class PlaywrightCucumberFixtures {
    private static final ThreadLocal<Playwright> playwright = ...;
    private static final ThreadLocal<Browser> browser = ...;
    private static final ThreadLocal<BrowserContext> browserContext = ...;
    private static final ThreadLocal<Page> page = ...;

    @Before(order = 100)
    public void setUpBrowserContext() {
        browserContext.set(browser.get().newContext());
        page.set(browserContext.get().newPage());
    }

    @After(order = 100)
    public void closeContext() {
        browserContext.get().close();
    }

    @AfterAll
    public static void tearDown() {
        browser.get().close();
        browser.remove();
        playwright.get().close();
        playwright.remove();
    }

    public static Page getPage() { return page.get(); }
    public static BrowserContext getBrowserContext() { return browserContext.get(); }
}

// AFTER — PlaywrightHooks.java (NEW)
public class PlaywrightHooks {

    public static class HeadlessChromiumOptions implements OptionsFactory {
        @Override
        public Options getOptions() {
            return new Options()
                .setLaunchOptions(
                    new BrowserType.LaunchOptions()
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-gpu")))
                .setHeadless(true)
                .setTestIdAttribute("data-test");
        }
    }

    private static final PlaywrightBrowserManager browser =
            PlaywrightBrowserManager.managed(new HeadlessChromiumOptions());

    @Before(order = 100)
    public void setUp() {
        browser.openNewPage();
    }

    @After(order = 100)
    public void tearDown() {
        browser.closeCurrentContext();
    }

    @AfterAll
    public static void shutdown() {
        browser.close();
    }
}
```

Key differences:
- All `ThreadLocal` management is gone — `PlaywrightBrowserManager` handles this internally
- No static `getPage()` / `getBrowserContext()` methods needed
- Browser options defined via an inner `OptionsFactory` class

### 8.3 Update step definitions to use `@Steps`

Replace manual page object construction with `@Steps` injection:

```java
// BEFORE
public class ProductCatalogStepDefinitions {
    NavBar navBar;
    SearchComponent searchComponent;
    ProductList productList;

    @Before
    public void setupPageObjects() {
        navBar = new NavBar(PlaywrightCucumberFixtures.getPage());
        searchComponent = new SearchComponent(PlaywrightCucumberFixtures.getPage());
        productList = new ProductList(PlaywrightCucumberFixtures.getPage());
    }
    // ... step methods
}

// AFTER
public class ProductCatalogStepDefinitions {
    @Steps NavBar navBar;
    @Steps SearchComponent searchComponent;
    @Steps ProductList productList;

    // No @Before setup needed — Serenity injects the page objects
    // ... step methods
}
```

### 8.4 Update tracing fixtures

If you have tracing fixtures that access the browser context, update them to use `PlaywrightBrowserManager`:

```java
// BEFORE
PlaywrightCucumberFixtures.getBrowserContext().tracing().start(...);
PlaywrightCucumberFixtures.getBrowserContext().tracing().stop(...);

// AFTER
PlaywrightBrowserManager.current().getCurrentContext().tracing().start(...);
PlaywrightBrowserManager.current().getCurrentContext().tracing().stop(...);
```

Also ensure the tracing hook runs after the browser is created by setting hook order:

```java
@Before(order = 200)  // runs after PlaywrightHooks @Before(order = 100)
public void startTracing(Scenario scenario) { ... }
```

---

## 9. Remove the PlaywrightTestCase Base Class

If you had an abstract base class (`PlaywrightTestCase`) for manual Playwright lifecycle management using `ThreadLocal`:

```java
// DELETE this entire class
public abstract class PlaywrightTestCase {
    protected static ThreadLocal<Playwright> playwright = ...;
    protected static ThreadLocal<Browser> browser = ...;
    protected BrowserContext browserContext;
    protected Page page;

    @BeforeEach void setUpBrowserContext() { ... }
    @AfterEach void closeContext() { ... }
    @AfterAll static void tearDown() { ... }
}
```

Any test class that extended `PlaywrightTestCase` should be migrated to the annotation-based approach:

```java
// BEFORE
public class LoginWithRegisteredUserTest extends PlaywrightTestCase {
    // page field inherited from base class
}

// AFTER
@ExtendWith(SerenityJUnit5Extension.class)
@ExtendWith(SerenityPlaywrightExtension.class)
@UsePlaywright(ChromeHeadlessOptions.class)
@DisplayName("Login with a registered user")
@Feature("Login")
public class LoginWithRegisteredUserTest {
    // page injected as JUnit method parameter
}
```

---

## 10. Update Maven Plugins

### 10.1 Remove AspectJ weaving from Surefire and Failsafe

Allure required AspectJ load-time weaving for `@Step` interception. Serenity does not. Remove the `<argLine>` and `<dependencies>` blocks from both plugins:

```xml
<!-- REMOVE from both maven-surefire-plugin and maven-failsafe-plugin -->
<configuration>
    <argLine>
        -javaagent:"${settings.localRepository}/org/aspectj/aspectjweaver/${aspectj.version}/aspectjweaver-${aspectj.version}.jar"
    </argLine>
</configuration>
<dependencies>
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
        <version>${aspectj.version}</version>
    </dependency>
</dependencies>
```

### 10.2 Update Failsafe includes pattern

If you renamed the Cucumber runner (e.g., to `CucumberTestSuite`), add the matching pattern:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <configuration>
        <includes>
            <include>**/*Test.java</include>
            <include>**/*Tests.java</include>
            <include>**/*TestSuite.java</include>  <!-- ADD THIS -->
        </includes>
    </configuration>
</plugin>
```

### 10.3 Replace Allure Maven plugin with Serenity Maven plugin

```xml
<!-- REMOVE -->
<plugin>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-maven</artifactId>
    <version>2.15.0</version>
    <configuration>
        <reportVersion>${allure.version}</reportVersion>
    </configuration>
    <executions>
        <execution>
            <phase>post-integration-test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>

<!-- ADD -->
<plugin>
    <groupId>net.serenity-bdd.maven.plugins</groupId>
    <artifactId>serenity-maven-plugin</artifactId>
    <version>${serenity.version}</version>
    <executions>
        <execution>
            <id>serenity-reports</id>
            <phase>post-integration-test</phase>
            <goals>
                <goal>aggregate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Reports are generated automatically during `mvn verify` in the `post-integration-test` phase.

---

## 11. Summary of Import Replacements

This table covers every import change needed across the codebase:

| Old import (Allure) | New import (Serenity) |
|---|---|
| `io.qameta.allure.Feature` | `net.serenitybdd.annotations.Feature` |
| `io.qameta.allure.Story` | `net.serenitybdd.annotations.Story` |
| `io.qameta.allure.Step` | `net.serenitybdd.annotations.Step` |
| `io.qameta.allure.Allure` | *(remove — not needed)* |
| — | `net.serenitybdd.annotations.Steps` *(new)* |
| — | `net.serenitybdd.junit5.SerenityJUnit5Extension` *(new)* |
| — | `net.serenitybdd.playwright.PlaywrightSerenity` *(new)* |
| — | `net.serenitybdd.playwright.junit5.SerenityPlaywrightExtension` *(new)* |
| — | `net.serenitybdd.playwright.PlaywrightBrowserManager` *(new, Cucumber only)* |

---

## 12. Checklist

Use this checklist to track your migration progress:

### Maven
- [ ] Remove Allure BOM, `allure-junit5`, `allure-junit-platform`, `allure-cucumber7-jvm`
- [ ] Remove `cucumber-junit` dependency
- [ ] Add `serenity-core`, `serenity-junit5`, `serenity-playwright`, `serenity-cucumber`
- [ ] Add JUnit BOM and Cucumber BOM to `<dependencyManagement>`
- [ ] Remove AspectJ `<argLine>` and `<dependencies>` from surefire and failsafe
- [ ] Replace `allure-maven` plugin with `serenity-maven-plugin`
- [ ] Update failsafe `<includes>` if runner class was renamed
- [ ] Delete `src/test/resources/allure.properties`

### Configuration
- [ ] Create `src/test/resources/serenity.conf`

### Test classes (repeat for each test class)
- [ ] Add `@ExtendWith(SerenityJUnit5Extension.class)`
- [ ] Add `@ExtendWith(SerenityPlaywrightExtension.class)`
- [ ] Replace `io.qameta.allure.Feature` → `net.serenitybdd.annotations.Feature`
- [ ] Replace `io.qameta.allure.Story` → `net.serenitybdd.annotations.Story`
- [ ] Add `PlaywrightSerenity.registerPage(page)` in `@BeforeEach`
- [ ] Replace `new PageObject(page)` with `@Steps PageObject` fields

### Page objects (repeat for each page object)
- [ ] Replace `io.qameta.allure.Step` → `net.serenitybdd.annotations.Step`
- [ ] Add `@Step` annotations to unannotated public methods
- [ ] Remove any `ScreenshotManager.takeScreenshot()` or `Allure.addAttachment()` calls

### Workflow objects (repeat for each workflow)
- [ ] Replace `io.qameta.allure.Step` → `net.serenitybdd.annotations.Step`
- [ ] Replace manual page object construction with `@Steps` fields
- [ ] Enrich `@Step` descriptions with parameter placeholders

### Deleted files
- [ ] Delete `ScreenshotManager.java`
- [ ] Delete `RecordsAllureScreenshots.java`
- [ ] Delete `PlaywrightTestCase.java` (if you had a base class)

### Cucumber
- [ ] Replace Cucumber runner: `AllureCucumber7Jvm` → `SerenityReporterParallel`
- [ ] Add `GLUE_PROPERTY_NAME` to runner configuration
- [ ] Replace `PlaywrightCucumberFixtures` with `PlaywrightHooks` using `PlaywrightBrowserManager`
- [ ] Update step definitions to use `@Steps` instead of `PlaywrightCucumberFixtures.getPage()`
- [ ] Update tracing fixtures to use `PlaywrightBrowserManager.current()`

### Verify
- [ ] Run `mvn verify` and confirm all tests pass
- [ ] Check Serenity reports in `target/site/serenity/`
