package com.rise.contactsapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.rise.contactsapi.dto.ContactCreateDto;
import com.rise.contactsapi.dto.ContactDto;
import com.rise.contactsapi.dto.ContactPatchDto;
import com.rise.contactsapi.mapper.ContactMapper;
import com.rise.contactsapi.model.Contact;

public class ContactMapperTest {

  @Test
  void shouldMapEntityToDto() {
    Contact contact = new Contact();
    contact.setUuid(UUID.randomUUID());
    contact.setFirstName("John");
    contact.setLastName("Doe");
    contact.setPhoneNumber("123-456-7890");
    contact.setAddress("123 Main St");
    contact.setCreatedAt(LocalDateTime.now());
    contact.setUpdatedAt(LocalDateTime.now());

    ContactDto dto = ContactMapper.toDTO(contact);

    assertThat(dto.getUuid()).isEqualTo(contact.getUuid());
    assertThat(dto.getFirstName()).isEqualTo("John");
    assertThat(dto.getLastName()).isEqualTo("Doe");
    assertThat(dto.getPhoneNumber()).isEqualTo("123-456-7890");
    assertThat(dto.getAddress()).isEqualTo("123 Main St");
    assertThat(dto.getCreatedAt()).isEqualTo(contact.getCreatedAt().toString());
    assertThat(dto.getUpdatedAt()).isEqualTo(contact.getUpdatedAt().toString());
  }

  @Test
  void shouldMapCreateDtoToEntity() {
    ContactCreateDto dto = new ContactCreateDto();
    dto.setFirstName("Alice");
    dto.setLastName("Smith");
    dto.setPhoneNumber("111-222-3333");
    dto.setAddress("456 Oak Ave");

    Contact contact = ContactMapper.fromCreateDTO(dto);

    assertThat(contact.getFirstName()).isEqualTo("Alice");
    assertThat(contact.getLastName()).isEqualTo("Smith");
    assertThat(contact.getPhoneNumber()).isEqualTo("111-222-3333");
    assertThat(contact.getAddress()).isEqualTo("456 Oak Ave");
    assertThat(contact.getCreatedAt()).isNotNull();
    assertThat(contact.getUpdatedAt()).isNotNull();
  }

  @Test
  void shouldApplyPatchToContact() {
    Contact contact = new Contact();
    contact.setFirstName("Bob");
    contact.setLastName("Brown");
    contact.setPhoneNumber("999-888-7777");
    contact.setAddress("Old Address");
    contact.setUpdatedAt(LocalDateTime.now().minusDays(5));

    ContactPatchDto patchDto = new ContactPatchDto();
    patchDto.setAddress("New Address");

    ContactMapper.applyPatch(contact, patchDto);

    assertThat(contact.getFirstName()).isEqualTo("Bob");
    assertThat(contact.getLastName()).isEqualTo("Brown");
    assertThat(contact.getPhoneNumber()).isEqualTo("999-888-7777");
    assertThat(contact.getAddress()).isEqualTo("New Address");
    assertThat(contact.getUpdatedAt()).isNotNull();
  }

  @Test
  void shouldHandleNullFieldsInToDtoGracefully() {
    Contact contact = new Contact();
    contact.setUuid(null);
    contact.setFirstName(null);
    contact.setLastName(null);
    contact.setPhoneNumber(null);
    contact.setAddress(null);
    contact.setCreatedAt(LocalDateTime.now());
    contact.setUpdatedAt(LocalDateTime.now());

    ContactDto dto = ContactMapper.toDTO(contact);

    assertThat(dto.getUuid()).isNull();
    assertThat(dto.getFirstName()).isNull();
    assertThat(dto.getLastName()).isNull();
    assertThat(dto.getPhoneNumber()).isNull();
    assertThat(dto.getAddress()).isNull();
    assertThat(dto.getCreatedAt()).isEqualTo(contact.getCreatedAt().toString());
    assertThat(dto.getUpdatedAt()).isEqualTo(contact.getUpdatedAt().toString());
  }

  @Test
  void shouldIgnoreNullPatchFields() {
    Contact contact = new Contact();
    contact.setFirstName("Original");
    contact.setLastName("User");
    contact.setPhoneNumber("000-000-0000");
    contact.setAddress("Somewhere");

    ContactPatchDto patchDto = new ContactPatchDto();
    ContactMapper.applyPatch(contact, patchDto);

    assertThat(contact.getFirstName()).isEqualTo("Original");
    assertThat(contact.getLastName()).isEqualTo("User");
    assertThat(contact.getPhoneNumber()).isEqualTo("000-000-0000");
    assertThat(contact.getAddress()).isEqualTo("Somewhere");
    assertThat(contact.getUpdatedAt()).isNotNull();
  }

  @Test
  void shouldHandleEmptyStringsInPatchDto() {
    Contact contact = new Contact();
    contact.setFirstName("John");
    contact.setLastName("Smith");
    contact.setPhoneNumber("123-456-7890");
    contact.setAddress("123 Street");

    ContactPatchDto patchDto = new ContactPatchDto();
    patchDto.setFirstName("");
    patchDto.setAddress("");

    ContactMapper.applyPatch(contact, patchDto);

    assertThat(contact.getFirstName()).isEqualTo("");
    assertThat(contact.getAddress()).isEqualTo("");
    assertThat(contact.getLastName()).isEqualTo("Smith");
    assertThat(contact.getPhoneNumber()).isEqualTo("123-456-7890");
  }

  @Test
  void shouldCreateEntityWithNullOptionalFields() {
    ContactCreateDto dto = new ContactCreateDto();
    dto.setFirstName("Jane");
    dto.setLastName("Doe");
    dto.setPhoneNumber("555-123-4567");
    dto.setAddress(null);

    Contact contact = ContactMapper.fromCreateDTO(dto);

    assertThat(contact.getAddress()).isNull();
    assertThat(contact.getFirstName()).isEqualTo("Jane");
    assertThat(contact.getLastName()).isEqualTo("Doe");
  }
}