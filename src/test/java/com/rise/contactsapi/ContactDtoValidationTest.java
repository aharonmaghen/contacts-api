package com.rise.contactsapi;

import com.rise.contactsapi.dto.ContactCreateDto;
import com.rise.contactsapi.dto.ContactPatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactDtoValidationTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void shouldValidateValidCreateDto() {
    ContactCreateDto dto = new ContactCreateDto("John", "Doe", "123-456-7890", "123 Main St");

    Set<ConstraintViolation<ContactCreateDto>> violations = validator.validate(dto);
    assertThat(violations).isEmpty();
  }

  @Test
  void shouldFailValidationWhenFirstNameIsTooShort() {
    ContactCreateDto dto = new ContactCreateDto("J", "Doe", "123-456-7890", "123 Main St");

    Set<ConstraintViolation<ContactCreateDto>> violations = validator.validate(dto);
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("firstName"));
  }

  @Test
  void shouldFailValidationWhenPhoneNumberIsInvalid() {
    ContactCreateDto dto = new ContactCreateDto("John", "Doe", "abc-123", "123 Main St");

    Set<ConstraintViolation<ContactCreateDto>> violations = validator.validate(dto);
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber"));
  }

  @Test
  void shouldAllowNullFieldsInPatchDto() {
    ContactPatchDto dto = new ContactPatchDto(null, null, null, null);

    Set<ConstraintViolation<ContactPatchDto>> violations = validator.validate(dto);
    assertThat(violations).isEmpty();
  }

  @Test
  void shouldValidateMaxLengthOnPatchDtoAddress() {
    String longAddress = "A".repeat(101);
    ContactPatchDto dto = new ContactPatchDto(null, null, null, longAddress);

    Set<ConstraintViolation<ContactPatchDto>> violations = validator.validate(dto);
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("address"));
  }
}