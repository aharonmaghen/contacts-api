package com.rise.contactsapi.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public abstract class ContactBaseDto {

  protected String firstName;
  protected String lastName;
  protected String phoneNumber;
  protected String address;

  public ContactBaseDto() {
  }

  public ContactBaseDto(String firstName, String lastName, String phoneNumber, String address) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.phoneNumber = phoneNumber;
    this.address = address;
  }

  @Size(min = 2, max = 50)
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @Size(min = 2, max = 50)
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @Pattern(regexp = "^[0-9\\-]{10,15}$", message = "Phone number must be 10 to 15 characters long and contain only digits and dashes")
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @Size(max = 100)
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}