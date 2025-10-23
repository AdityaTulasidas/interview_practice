package com.thomsonreuters.metadataregistry.utils;

            import jakarta.validation.ConstraintViolation;
            import jakarta.validation.Validation;
            import jakarta.validation.Validator;
            import jakarta.validation.ValidatorFactory;
            import org.junit.jupiter.api.BeforeAll;
            import org.junit.jupiter.params.ParameterizedTest;
            import org.junit.jupiter.params.provider.CsvSource;

            import java.util.Set;

            import static org.junit.jupiter.api.Assertions.assertEquals;

            class NotBlankIfPresentValidatorTest {

                private static Validator validator;

                @BeforeAll
                static void setUp() {
                    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
                    validator = factory.getValidator();
                }

                static class TestClass {
                    @NotBlankIfPresent
                    private String field;

                    public void setField(String field) {
                        this.field = field;
                    }
                }

                @ParameterizedTest
                @CsvSource({
                    "null, 0",    // Field is null, no violations
                    "'Valid Value', 0", // Field is not blank, no violations
                    "'  ', 1"     // Field is blank, one violation
                })
                void should_ValidateFieldCorrectly(String fieldValue, int expectedViolationCount) {
                    // Arrange
                    TestClass testObject = new TestClass();
                    testObject.setField("null".equals(fieldValue) ? null : fieldValue);

                    // Act
                    Set<ConstraintViolation<TestClass>> violations = validator.validate(testObject);

                    // Assert
                    assertEquals(expectedViolationCount, violations.size());
                }
            }