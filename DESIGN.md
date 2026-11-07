# Design decisions

## Why `ThreadLocal<WebDriver>`
TestNG runs methods in parallel on a thread pool. A `WebDriver` is not thread-safe, and a static field would be shared by every thread. `DriverFactory` stores the driver in a `ThreadLocal`, so each worker thread creates, uses and quits its own browser and can never see another test's session. `quitDriver()` calls `ThreadLocal.remove()` so pooled threads don't keep stale references between tests.

## Why one browser per test method
`BaseUiTest` starts a browser in `@BeforeMethod` and quits it in `@AfterMethod`. Sharing a browser across tests would be faster but leaks state (cookies, cart contents, logged-in user) and makes results depend on execution order. A fresh browser keeps every test independent, which is a prerequisite for both parallelism and retries.

## Why API-driven test setup
Registering a user through the UI takes ~15–20 seconds and exercises screens that are not under test. `BaseUiTest.createUserViaApi()` does the same in about a second, and cleanup also happens through the API. UI tests therefore spend their time only on the behaviour they verify (login, checkout, …), are faster, and fail only for reasons related to what they test. The one test that is about registration (`AuthTest.registerUser`) still does it through the UI.

## Why page objects on top of a `BasePage`
Page objects keep locators and page mechanics out of tests: tests read as user intent (`login(...)`, `addToCartAndViewCart(1)`), and a markup change is fixed in one place. `BasePage` centralises the cross-cutting concerns every page needs:
- **Explicit waits only.** `click`, `type`, `text` wait for the right condition (`elementToBeClickable`, `visibilityOf`). There is no `Thread.sleep` and no implicit wait (mixing implicit and explicit waits causes unpredictable timeouts).
- **Robust clicks.** `click()` re-finds the element inside the wait (survives stale references), scrolls it to the centre, and if an ad overlay intercepts the click, strips the ads and retries via JavaScript.
- **Ad and consent handling.** `handlePopups()` accepts the consent dialog when present, reloads without the `#google_vignette` hash, and removes ad iframes/containers from the DOM. It is best-effort and never fails a test.
- Navigation methods wait for the target URL before returning, avoiding races where assertions run against the previous page.

Page methods return the next page object (fluent navigation), which makes the flow explicit and type-checked.

## API layer
- `ApiClient` holds one immutable `RequestSpecification` (base URI, content type, Allure filter, SLF4J logging filter). Each call starts a new request from it, so clients are stateless and safe in parallel.
- `ApiResponse` encodes this API's quirk in one place: HTTP status is always 200, the real status is the JSON `responseCode`, and the content type is `text/html`. It parses the raw body with Jackson regardless of headers.
- One client per resource (`ProductsClient`, `BrandsClient`, `AccountClient`) with `@Step` annotations so each call shows up as a readable step in Allure, with the request/response attached by `AllureRestAssured`.
- POJOs (`UserAccount`, `ProductsResponse`, …) for typed assertions; `UserAccount.toFormParams()` maps Java fields to the API's form field names.
- JSON Schema validation guards the contract (types, required fields, price format) independently of the specific data.

## Retry policy
`RetryAnalyzer` allows **one** retry, and `TestListener` (as an `IAnnotationTransformer`) attaches it **only to classes extending `BaseUiTest`** (UI and hybrid tests). The live site serves third-party ads that occasionally interfere with the browser; one retry absorbs that noise. API tests are never retried: they are deterministic, and a retry would only hide real failures. A retried test shows as "skipped" + the final result in reports, so flakiness stays visible rather than silently swallowed.

## Failure evidence
`TestListener.onTestFailure` runs before `@AfterMethod` quits the browser, so it can attach a screenshot, the page source and the URL to Allure. It reads the driver from the same `ThreadLocal`, so evidence always belongs to the failing test even in parallel runs.

## How parallelism stays safe
- Browser state: one `WebDriver` per thread (`ThreadLocal`), one browser per test.
- Data: `TestDataFactory` generates unique emails (timestamp + atomic counter + random suffix), so parallel tests never touch the same account.
- Cleanup bookkeeping: `BaseUiTest` tracks users to delete in a `ThreadLocal<List>`; `AccountApiTest` uses a concurrent set.
- Shared objects are immutable or stateless: `ConfigManager` (read-only after load), the base `RequestSpecification`, page objects are created per test.
- Data providers run in parallel too (`parallel = true`), and each row gets its own browser.

## Configuration
`ConfigManager` reads `config.properties` and lets any `-D` system property override it. The same build runs locally, on Grid and in CI by flipping flags (`-DgridUrl`, `-Dbrowser`, `-Dheadless`), with no profile-specific code.

## Local vs Grid
`DriverFactory` builds the same `ChromeOptions`/`FirefoxOptions` for both. If `gridUrl` is empty it starts a local driver (resolved by Selenium Manager); otherwise a `RemoteWebDriver` with a `LocalFileDetector`, so file uploads work when the browser runs in a container.

## Reporting
Allure collects test steps (`@Step` on page and client methods), API traffic, and failure attachments. CI merges API and UI results, keeps history from the previous `gh-pages` build for trend graphs, and publishes the report to GitHub Pages.
