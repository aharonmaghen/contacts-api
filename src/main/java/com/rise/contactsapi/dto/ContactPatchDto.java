package com.rise.contactsapi.dto;

public class ContactPatchDto extends ContactBaseDto {

  public ContactPatchDto() {
    super();
  }

  public ContactPatchDto(String firstName, String lastName, String phoneNumber, String address) {
    super(firstName, lastName, phoneNumber, address);
  }
}