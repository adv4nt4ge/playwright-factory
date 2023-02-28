# Playwright Factory

*Page Objects for [Playwright Java](https://playwright.dev/java/) made easy!*

Is a Java library that helps you create clean and readable Page Objects. Inspired
by [Selenium's PageFactory](https://github.com/SeleniumHQ/selenium/wiki/PageFactory), solution allows you to create
locators easily by annotating fields.

## Installation

```xml

<dependency>
    <groupId>io.github.adv4nt4ge</groupId>
    <artifactId>playwright-factory</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Getting Started

To define [Locators](https://playwright.dev/java/docs/locators), create a Field and annotate it with `@FindBy`:

```java

public class HomePage {

    @FindBy(locator = "#some-id")
    public Locator header;

    @FindBy(testId = "some-id")
    public Locator myButton;

    @FindBy(placeholder = "#some-id")
    public Locator myPlaceholder;

    @FindBy(altText = "#some-id")
    public Locator myAltText;

    @FindBy(text = "#some-id")
    public Locator myText;

    @FindBy(label = "#some-id")
    public Locator myLabel;

    @FindBy(title = "#some-id")
    public Locator myTitle;

}
```

`@FindBy(locator = "")` accepts any Playwright [Other Locators](https://playwright.dev/java/docs/other-locators).

To use your Page Object in your tests use the `PageFactory` to create an instance of your page and pass it your page
class and an instance of Playwright's [Page](https://playwright.dev/java/docs/pages):

```java
    HomePage homePage=PageFactory.create(HomePage.class,page)
```
The PageFactory can create an instance of any page that has a default constructor or a constructor with just a
Playwright Page parameter.

```java

public void initElementsPageWithFieldDecorator() {
        HomePage homePage = new HomePage();
        PageFactory.initElements(homePage, new ElementFieldDecorator(page));
}
```

## Dependent Locators

At times, you may want to find a locator that is under another locator. The way to do this in Playwright would
be: `page.locator("#parent").locator(".child")`. To define this you can use the `@Under` annotation:

```java

public class HomePage {

    @FindBy(testId = "some-id")
    public Locator parentLocator;

    @Under("parentLocator")
    @FindBy(locator = "#some-id")
    public Locator childLocator;

    @Under("parentLocator")
    @FindBy(placeholder = "#some-id")
    public Locator anotherChild;
}
```

The value you pass to the `@Under` annotation should be the name of the parent Locator.

## Requirements

Stagehand requires Java 8+ and Playwright 1.20.0+.
