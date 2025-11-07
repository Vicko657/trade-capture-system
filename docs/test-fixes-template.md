# Test Fixes Template

## Failures

### TradeControllerTest

#### Test Case 1: TradeControllerTest.testCreateTrade:138 Status expected:<200> but was:<201>

```
fix(test): TradeControllerTest - Changed testCreateTrade() expected response status.

- Problem: testCreateTrade() was failing with the wrong response code, 200 Ok Request, instead of a 201 Created Request.
- Root Cause: The expected response status for the test was 200, this code is not returned when a new resource is created,
  it should be 201.
- Solution: Changed status().isOk() to status().isCreated() on line 138.
- Impact: Enables the test, to test the correct response code, when a new trade is created.
```

#### Test Case 2: TradeControllerTest.testCreateTradeValidationFailure_MissingBook:175 Status expected:<400> but was:<201>

```
fix(test): TradeControllerTest - Fixed validation for testCreateTrade() when a Book or Counterparty is missing.

- Problem: testCreateTradeValidationFailure_MissingBook() was failing with the wrong response code, 201 Created, instead of a 400 Bad Request.
- Root Cause: The expected response status for the test is 400 and "Book and Counterparty is required" the Book is required and is missing from the data populated.
- Solution: Added a if statement in the trade controller, after the data is populated on line 80, to validate if the book or counterparty is null. Then returned a new response entity, ResponseEntity.badRequest().body("Book and Counterparty are required"); on line 84, to match the expected content in the test.
- Impact: Enables the test, to validate that there is no missing book or counterparty, before the trade is created.
```

#### Test Case 3: TradeControllerTest.testCreateTradeValidationFailure_MissingTradeDate:160 Response content expected:<Trade date is required> but was:<>

```
fix(test): TradeControllerTest - Fixed validation for testCreateTrade() when a tradeDate is missing.

- Problem: testCreateTradeValidationFailure_MissingTradeDate() was failing, the expected response content was empty.
- Root Cause: The is due to the validation not being handled for @NotNull("Trade date is required") messsage above the tradeDate field in the TradeDTO on line 25 and 26. The createTrade() method in the controller has a @Valid, which triggers a Bean validation before the method runs. To see the specific field error message a MethodArgumentNotValidException needs to be handled in a RestControllerAdvice class.
- Solution: Created a new class called GlobalExceptionHandler which has a @RestControllerAdvice annotation. Added a response status HttpStatus.BAD_REQUEST annotation on line 13 to make sure the response entity knows the right response code and a exception handler annotation on line 14 to specify the exception that will be handled. Created a string method which allows an exception to be thrown when validation on an argument annotated with @Valid fails (MethodArgumentNotValidException). Targeted the tradeDate fieldError by creating a new FieldError "tradeDateError" and using getBindingResult() to get the specfic field, FieldError tradeDateError = e.getBindingResult().getFieldError("tradeDate"); on line 19. An if statement was used to return the errors defaultmessage used in the tradeDTO.
- Impact: Enables the test, to validate if the "tradeDate" is missing, before the trade is created.
```

#### Test Case 4: TradeControllerTest.testDeleteTrade:224 Status expected:<204> but was:<200>

```
fix(test): TradeControllerTest - Returned the correct response status for deleting a trade in the Trade Controller.

- Problem: testDeleteTrade() was failing with the wrong response code, 200 Ok Request, instead of a 204 No Content Request.
- Root Cause: The expected response status for the test was isNoContent(). On line 126, in the Trade Controller, return ResponseEntity.ok().body("Trade cancelled successfully"), the wrong status used.
- Solution: Changed the returned response status from ResponseEntity.ok().body("Trade cancelled successfully");, to ResponseEntity.noContent().build(); on line 126.
- Impact: Enables a correct response code, when a trade is deleted.
```

#### Test Case 5: TradeControllerTest.testUpdateTrade:194 No value at JSON path "$.tradeId". TradeControllerTest.testUpdateTrade:189 » InvalidUseOfMatchers Invalid use of argument matchers! 2 matchers expected, 1 recorded:-> at com.technicalchallenge.controller.TradeControllerTest.testUpdateTrade(TradeControllerTest.java:189)This exception may occur if matchers are combined with raw values://incorrect: someMethod(any(), "raw String"); When using matchers, all arguments have to be provided by matchers. For example: //correct: someMethod(any(), eq("String by matcher")); For more info see javadoc for Matchers class.

```
fix(test): TradeControllerTest - Returned the correct value at JSON path "$.tradeId" and returned the correct response code when a trade is amended.

- Problem: testUpdateTrade() was failing with a assertion error, there was no value at JSON path "$.tradeId".
- Root Cause: The test stubbing statements was not matching up with the controller's updateTrade() method. The wrong stubbing statements were used for amending a trade, when(tradeService.saveTrade(any(Trade.class), any(TradeDTO.class))).thenReturn(trade); on line 189. The stub was using the saveTrade() method instead of amendTrade() method call, including the verfication assertion, verify(tradeService).saveTrade(any(Trade.class), any(TradeDTO.class)); on line 196;
- Solution: The saveTrade() stubbing statement was replaced with, when(tradeService.amendTrade(eq(tradeId), any(TradeDTO.class))).thenReturn(trade);, on line 189. The tradeId was placed in mockito's argument matcher, eq() which checks for equality with the .equals() used in the controller and it prevents any argument matcher errors occuring. The doNothing().when(tradeService).populateReferenceDataByName(any(Trade.class), any(TradeDTO.class)); was also removed. To match the controller, the toDto mapper was used on line 190 to convert the amended trade back to a dto. The verfication assertion was changed to verify(tradeService).amendTrade(eq(tradeId), any(TradeDTO.class)); to verify that the amendTrade method happened once.
- Impact: Enables the test, to return the correct JSON path value that matches the tradeId and return the right response code, when a trade is amended.
```

#### Test Case 6: TradeControllerTest.testUpdateTradeIdMismatch:209 Status expected:<400> but was:<200>

```
fix(test): TradeControllerTest - Returned the correct response status if a tradeId and the pathId are not equal in the Trade Controller.

- Problem: testUpdateTradeIdMismatch() was failing, with the wrong response code, 200 Ok Request, instead of a 400 Bad Request.
- Root Cause: The tradeId was being set with pathId, tradeDTO.setTradeId(id);, but there was no validation to check if the existing tradeId and path Id were not equal, to prevent amendment mistakes.
- Solution: Created a if statement which the returned a response status, return ResponseEntity.badRequest().body("Trade ID in path must match Trade ID in request body");, in the updateTrade() response entity, in the Trade Controller on lines 111 - 113. The conditional statement was inserted before the amendedTrade method call, to prevent any amendments happening.
- Impact: Enables a correct response code, when if the tradeId and pathId do not match.
```

### TradeServiceTest

#### Test Case 1: TradeServiceTest.testCreateTrade_InvalidDates_ShouldFail:99 expected: <"Wrong error message"> but was: <"Start date cannot be before trade date">

```
fix(test): TradeServiceTest - Matched the correct expected exception error message.

- Problem: testCreateTrade_InvalidDates_ShouldFail() was failing with the wrong exception message "Wrong error message".
- Root Cause: The expected error message "Wrong error message" did not match the actual expection message "Start date cannot be before trade date" which is thrown in the validateTradeCreation() method in the TradeService class.
- Solution: Changed "Wrong error message" to "Start date cannot be before trade date" in asserEquals() on line 99.
- Impact: Enables the test, to test the runtime exception thrown when the startDate is before the tradeDate, when a new trade is created.
```

#### Test Case 2: TradeServiceTest.testCreateTrade_Success:149 » Runtime Book not found or not set

```
fix(test): TradeServiceTest - Populated the missing reference data and tradeLegs, to create a trade.

- Problem: testCreateTrade_Success() was failing, the reference data was null and throws a RuntimeException "Book not found or not set".
- Root Cause: The createTrade() method in the TradeService.class calls validateReferenceData(), which throws exceptions for Book, Counterparty and TradeStatus, if the reference data is null. The testCreateTrade_Success() is missing stubbing statements that reference the data.
- Solution: 1.Mocked the Book and Counterparty repositories in the class on line 50 - 54. Created new entities on line 61 - 63 and setup new reference data for the Book, Counterparty and TradeStatus in the setUp() method on lines 92 - 114. Added stubbing statements in the test to mock the reference data being populated on lines 133 - 135. A stub was used to mock the tradeRepository saving the entity. 2. A NullPointerException was thrown, get.tradeLegid() was null, due missing Tradeleg entitiy which was referenced in the service layer, createTradeLegsWithCashflows() on line 106. A new tradeLeg entity was created on line 66 and 80, a stub was added into the test to mock the tradeLegRepository saving a new tradeLeg entity.
- Impact: The test creates a new trade successfully, without any exceptions being thrown.
```

#### Test Case 3: TradeServiceTest.testAmendTrade_Success:232 » NullPointer Cannot invoke "java.lang.Integer.intValue()" because the return value of "com.technicalchallenge.model.Trade.getVersion()" is null

```
fix(test): TradeServiceTest - Fixed the amendTrade test to successfully amend a trade.

- Problem: testAmendTrade_Success() was failing with null fields and missing stub statements.
- Root Cause: The amended trade test method did not match the arguments set in the method call, amendtrade(), in the TradeService.class. The version field was not set, throwing a NullPointerException, the existing trade was not disabled and a new trade was not created to replace the trade.
- Solution: 1. Set the missing field "Version" to 1 for the trade and dto on line 76 and 127 in the setup(). 2. Disabled the trade entity by changing these fields, trade.setActive(false);, trade.setDeactivatedDate(LocalDateTime.now()); on line 233 and 234. 3. Created a new trade with the same tradeId "100001L", set the "Version" field to + 1 and set the "TradeStatus" to "AMENDED" on lines 237 - 253. 4. Added a stubbing statement to mock the tradeLegRepository, saving a new tradeLeg entity for the amended trade on line 260. 5. Assertion methods were added, to check if the existing trade was disabled and the amended trade with the same tradeid, was active with the changes on lines 267 - 271.
- Impact: Enables the test, to save the disabled test and the new amended test successfully, without any exceptions being thrown.
```

#### Test Case 4: TradeServiceTest.testCashflowGeneration_MonthlySchedule:301 expected: <1> but was: <12>

```
fix(test): TradeServiceTest - Fixed the void generateCashflows() method in the TradeService.class and the testCashflowGeneration_MonthlySchedule() to successfully generate cashflows for 2 tradeLegs.

- Problem: testCashflowGeneration_MonthlySchedule() was failing, the test method was incomplete and had logical errors.
- Root Cause: There was missing stubbing statements, method call and the assertEquals(1, 12); on line 301 was redundant. The void generateCashflows() method was in the createTrade() method, which could not be directly target by a method call and was also not adding the new cashflows generated for the two tradeLegs onto the list and would return null. The tradeLeg was returning 24 cashflows for each leg instead of 12 cashflows.
- Solution: The stubbing statements and method call used for the testCreateTrade_Success() on lines 371 to 389 was added to the test. In the setUp() method two of each ("new TradeLeg entities, new Cashflow entities and new Cashflow array lists") were created on line 149 to 198. The Schduele repository was mocked and a new schduele() was created to define the monthly interval, to pass in the string "1M" in setCalculationPeriodSchedule(schduele); for each tradeleg entity on line 172 and 178. The mock stub for the Cashflowrepositiory to save the cashflows was added into the test, when(cashflowRepository.save(any(Cashflow.class))).thenAnswer(invocation -> invocation.getArgument(0));, this saved the different instances for the Cashflows so they weren't overwritten. In the Tradeleg.class, private List<Cashflow> cashflows; was modified to private List<Cashflow> cashflows = new ArrayList<Cashflow>(); on line 70, to create a new arraylist each time. In the TradeService.class, in the void generateCashflows() method, leg.getCashflows().add(cashflow); was added at the end of the for loop, to make sure the cashflow list, contains all the cashflows generated once they are saved. The tradeLeg stubbing statement was updated to return two different tradeLegs instead of one tradeLeg which stopped the duplication of cashflows and returned 12 cashflows. Assertions were added to verify that two tradelegs had 12 cashflows each i.e. assertEquals(12, tradeleg1.getCashflows().size()); and to check how many times the cashflow repository saves the cashflows, verify(cashflowRepository, times(24)).save(any(Cashflow.class)); on lines 392 to 396.
- Impact: Enables the test, to test out the functionality of the generated cashflows for the tradelegs, when trade is created.
```

### BookServiceTest

#### Test Case 1: BookServiceTest.testFindBookById:63 expected: <"true"> but was: <"false">

```
fix(test): BookServiceTest - Returned the correct boolean for finding a Book with a Id.

- Problem: testFindBookById() was failing, the id was not present.
- Root Cause: The expected result for the assertion, assertTrue(found.isPresent()); on line 71, was true, but returned false. No mapper was stubbed, so the method call returned a empty optional.
- Solution: Added a stub, using the bookMapper to convert the entity into a dto, when(bookMapper.toDto(book)).thenReturn(bookDTO); on line 65.
- Impact: Enables the test to return a book, when a id has been found.
```

#### Test Case 2: BookServiceTest.testFindBookByNonExistentId:88 » NullPointer (Error)

```
fix(test): BookServiceTest - Added in the Mocked reposistory and mapper to setup the BookServiceTest.

- Problem: testFindBookByNonExistentId() was failing with a NullPointerException error.
- Root Cause: The test was throwing a NullPointerException due to there not being a mocked bookMapper, in BookServiceTest to handle the conversion of the Book (entity) and bookDTO (dto).
- Solution: Mocked the bookMapper and costCenterRepository, @Mock private BookMapper bookMapper; on line 32-33 and @Mock
    private CostCenterRepository costCenterRepository; on line 34-25. Additionally, created a new bookService in the beforeEach setup() method, bookService = new BookService(bookRepository, costCenterRepository, bookMapper); on line 44 and created test data in the setup.
- Impact: The test returns a empty set, when a non exisiting id has not been found.
```

#### Test Case 3: BookServiceTest.testSaveBook:98 » PotentialStubbingProblem Strict stubbing argument mismatch. Please check: - this invocation of 'save' method: bookRepository.save(null); -> at com.technicalchallenge.service.BookService.saveBook(BookService.java:56) - has following stubbing(s) with different arguments: 1. bookRepository.save(com.technicalchallenge.model.Book@4cb45048); -> at com.technicalchallenge.service.BookServiceTest.testSaveBook(BookServiceTest.java:81) Typically, stubbing argument mismatch indicates user mistake when writing tests.Mockito fails early so that you can debug potential problem easily.However, there are legit scenarios when this exception generates false negative signal: - stubbing the same method multiple times using 'given().will()' or 'when().then()' API Please use 'will().given()' or 'doReturn().when()' API for stubbing. - stubbed method is intentionally invoked with different arguments by code under test Please use default or 'silent' JUnit Rule (equivalent of Strictness.LENIENT). For more information see javadoc for PotentialStubbingProblem class.

```
fix(test): BookServiceTest - Fixed the saving book test, using the right stubbing arguments.

- Problem: testSaveBook() was failing with a PotentialStubbingProblem error message.
- Root Cause: The stubbing argument used, when(bookRepository.save(any(Book.class))).thenReturn(book); on line 99, did not match all the arguments used in the method call when the test was being executed.
- Solution: Added missing stubbing arguments, that matched how the book was saved in the service layer. 1. Added a stub when(bookMapper.toEntity(bookDTO)).thenReturn(book); on line 92, which used the bookMapper to convert the dto (bookDTO) to an entity (book), before the bookRepositiory saves the entity. 2. Using the mockito spy method, created a partial mock of bookService to target the void method - populateReferenceDataByName in the same service layer and added a void stub, doNothing().when(bookService).populateReferenceDataByName(book, bookDTO); on line 96. 3. After the bookRepositiory saves the entity, an additional stubbing argument is used, when(bookMapper.toDto(book)).thenReturn(bookDTO); on line 103, the bookMapper converts the entity (book) back into a dto (bookDTO). 4. Added verify(bookRepository).save(book) to verify if the book was saved.
- Impact: The test is now using the right stubbing arguments to save a book and returns the id of the new book.
```

### TradeLegControllerTest

#### Test Case 1: TradeLegControllerTest.testCreateTradeLegValidationFailure_NegativeNotional:166 Response content expected:<"Notional must be positive"> but was:<"Validation has failed">

```
fix(test): TradeLegControllerTest - Handled and seperated "notional" validation errors to correctly validate if notional is postive before the tradeLeg is created.

- Problem: testCreateTradeLegValidationFailure_NegativeNotional() was failing, the response content was "Validation has failed" and did not match the expected "Notional must be positive".
- Root Cause: A Global Exception Handler was created previously to handle a MethodArgumentNotValidException for a tradeData field error which had a @NotNull annotation and was empty. The "notional" field instance has both a @NotNull(message = "Notional is required") and @Positive(message = "Notional must be positive") annotation in the TradeLegDTO and in the controller has a if statement which checks if the notional is null or positive and returns "Notional must be positive". @Valid in the createTradeLeg() response entity in the controller overides the if statement, by triggering a bean validation.
- Solution: Removed the if statement from the controller, it was returning the wrong message for when a notional was null and the controller needs to handle both validation errors seperately. In the Global Exception Handler, handleFieldValidationExceptions() method, the "notional" fieldError was targeted, by creating a new FieldError "notionalError" and using getBindingResult() to get the specfic field, FieldError notionalError = e.getBindingResult().getFieldError("notional"); on line 24. An if statement was used to return both the @NotNull and @Postive validation errors defaultmessages used in the tradeLegDTO.
- Impact: Enables the test, to validate if the "notional" is positive, before the tradeLeg is created
```

### Tests are now failing due to enhancement being implemented in to the system such as Spring Security, validation and a new type of error handling
