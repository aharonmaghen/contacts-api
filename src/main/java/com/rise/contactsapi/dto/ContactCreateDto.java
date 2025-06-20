package com.rise.contactsapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ContactCreateDto extends ContactBaseDto {

  public ContactCreateDto() {
    super();
  }

  public ContactCreateDto(String firstName, String lastName, String phoneNumber, String address) {
    super(firstName, lastName, phoneNumber, address);
  }

  @Override
  @Size(min = 2, max = 50)
  @NotBlank
  public String getFirstName() {
    return firstName;
  }

  @Override
  @Size(min = 2, max = 50)
  @NotBlank
  public String getLastName() {
    return lastName;
  }

  @Override
  @Pattern(regexp = "^[0-9\\-]{10,15}$", message = "invalid phone number")
  @NotBlank
  public String getPhoneNumber() {
    return phoneNumber;
  }

  @Override
  @Size(max = 100)
  public String getAddress() {
    return address;
  }
}