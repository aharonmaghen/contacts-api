package com.rise.contactsapi.mapper;

import com.rise.contactsapi.dto.ContactCreateDto;
import com.rise.contactsapi.dto.ContactDto;
import com.rise.contactsapi.dto.ContactPatchDto;
import com.rise.contactsapi.model.Contact;

public class ContactMapper {
  public static ContactDto toDTO(Contact contact) {
    ContactDto dto = new ContactDto();
    dto.setUuid(contact.getUuid());
    dto.setFirstName(contact.getFirstName());
    dto.setLastName(contact.getLastName());
    dto.setPhoneNumber(contact.getPhoneNumber());
    dto.setAddress(contact.getAddress());
    dto.setCreatedAt(contact.getCreatedAt().toString());
    dto.setUpdatedAt(contact.getUpdatedAt().toString());
    return dto;
  }

  public static Contact fromCreateDTO(ContactCreateDto dto) {
    Contact contact = new Contact();
    contact.setFirstName(dto.getFirstName());
    contact.setLastName(dto.getLastName());
    contact.setPhoneNumber(dto.getPhoneNumber());
    contact.setAddress(dto.getAddress());
    contact.setCreatedAt(java.time.LocalDateTime.now());
    contact.setUpdatedAt(java.time.LocalDateTime.now());
    return contact;
  }

  public static void applyPatch(Contact contact, ContactPatchDto dto) {
    if (dto.getFirstName() != null)
      contact.setFirstName(dto.getFirstName());
    if (dto.getLastName() != null)
      contact.setLastName(dto.getLastName());
    if (dto.getPhoneNumber() != null)
      contact.setPhoneNumber(dto.getPhoneNumber());
    if (dto.getAddress() != null)
      contact.setAddress(dto.getAddress());
    contact.setUpdatedAt(java.time.LocalDateTime.now());
  }
}