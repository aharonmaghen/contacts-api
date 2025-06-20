package com.rise.contactsapi.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.rise.contactsapi.dto.ContactCreateDto;
import com.rise.contactsapi.dto.ContactDto;
import com.rise.contactsapi.dto.ContactPatchDto;
import com.rise.contactsapi.exception.ErrorMessages;
import com.rise.contactsapi.exception.NotFoundException;
import com.rise.contactsapi.mapper.ContactMapper;
import com.rise.contactsapi.model.Contact;
import com.rise.contactsapi.repository.ContactRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contacts")
@Tag(name = "Contacts", description = "Operations related to contacts")
public class ContactController {
  private ContactRepository contactRepository;
  @Value("${MAX_PAGE_SIZE}")
  private int maxPageSize;

  @Autowired
  public ContactController(ContactRepository contactRepository) {
    this.contactRepository = contactRepository;
  }

  @GetMapping
  public ResponseEntity<List<ContactDto>> getContacts(Pageable pageable) {
    int pageSize = pageable.getPageSize();

    if (pageSize > maxPageSize) {
      pageSize = maxPageSize;
    }

    Page<ContactDto> page = contactRepository.findAllByDeletedAtIsNull(PageRequest.of(pageable.getPageNumber(),
        pageSize, pageable.getSortOr(Sort.by(Sort.Direction.ASC, "firstName")))).map(ContactMapper::toDTO);
    return ResponseEntity.ok(page.getContent());
  }

  @GetMapping("/{contactUuid}")
  public ResponseEntity<ContactDto> findById(@PathVariable UUID contactUuid) {
    Optional<Contact> contactOpt = contactRepository.findByUuidAndDeletedAtIsNull(contactUuid);
    if (contactOpt.isPresent()) {
      return ResponseEntity.ok(ContactMapper.toDTO(contactOpt.get()));
    }
    throw new NotFoundException(String.format(ErrorMessages.CONTACT_NOT_FOUND, contactUuid));
  }

  @PostMapping
  public ResponseEntity<ContactDto> createContact(@Valid @RequestBody ContactCreateDto newContactRequest,
      UriComponentsBuilder ucb) {
    Contact contactToSave = ContactMapper.fromCreateDTO(newContactRequest);
    Contact savedContact = contactRepository.save(contactToSave);
    URI locationOfNewContact = ucb.path("/api/contacts/{contactUuid}").buildAndExpand(savedContact.getUuid()).toUri();
    return ResponseEntity.created(locationOfNewContact).body(ContactMapper.toDTO(savedContact));
  }

  @DeleteMapping("/{contactUuid}")
  public ResponseEntity<Void> deleteContact(@PathVariable UUID contactUuid) {
    Optional<Contact> contactOpt = contactRepository.findByUuidAndDeletedAtIsNull(contactUuid);
    if (!contactOpt.isPresent()) {
      throw new NotFoundException(String.format(ErrorMessages.CONTACT_NOT_FOUND, contactUuid));
    }

    Contact contact = contactOpt.get();
    contact.setDeletedAt(LocalDateTime.now());
    contactRepository.save(contact);

    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{contactUuid}")
  public ResponseEntity<ContactDto> updateContact(@PathVariable UUID contactUuid,
      @Valid @RequestBody ContactPatchDto contactPatchDto) {
    Optional<Contact> contactOpt = contactRepository.findByUuidAndDeletedAtIsNull(contactUuid);
    if (!contactOpt.isPresent()) {
      throw new NotFoundException(String.format(ErrorMessages.CONTACT_NOT_FOUND, contactUuid));
    }

    Contact contact = contactOpt.get();
    ContactMapper.applyPatch(contact, contactPatchDto);
    contact.setUpdatedAt(LocalDateTime.now());
    contactRepository.save(contact);
    return ResponseEntity.ok(ContactMapper.toDTO(contact));
  }

  @GetMapping("/search")
  public ResponseEntity<List<ContactDto>> searchContacts(@RequestParam String searchTerm, Pageable pageable) {
    int pageSize = pageable.getPageSize();

    if (pageSize > maxPageSize) {
      pageSize = maxPageSize;
    }

    Page<ContactDto> page = contactRepository.findAllBySearchTerm(searchTerm, PageRequest.of(pageable.getPageNumber(),
        pageSize, pageable.getSortOr(Sort.by(Sort.Direction.ASC, "firstName")))).map(ContactMapper::toDTO);
    return ResponseEntity.ok(page.getContent());
  }
}