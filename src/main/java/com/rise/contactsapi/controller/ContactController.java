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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/contacts")
@Tag(name = "Contacts", description = "Endpoints for managing contacts")
@Slf4j
public class ContactController {
  private ContactRepository contactRepository;
  @Value("${MAX_PAGE_SIZE}")
  private int maxPageSize;

  @Autowired
  public ContactController(ContactRepository contactRepository) {
    this.contactRepository = contactRepository;
  }

  @Operation(summary = "Get all contacts", description = "Returns a paginated list of contacts. Default and max page size is 10 results per page (0-indexed).")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Contacts retrieved successfully")
  })
  @GetMapping
  public ResponseEntity<List<ContactDto>> getAllContacts(Pageable pageable) {
    int pageSize = Math.min(pageable.getPageSize(), maxPageSize);
    log.info("Fetching contacts - page: {}, size: {}", pageable.getPageNumber(), pageSize);

    Page<ContactDto> page = contactRepository.findAllByDeletedAtIsNull(PageRequest.of(pageable.getPageNumber(),
        pageSize, pageable.getSortOr(Sort.by(Sort.Direction.ASC, "firstName")))).map(ContactMapper::toDTO);

    log.debug("Fetched {} contacts", page.getContent().size());
    return ResponseEntity.ok(page.getContent());
  }

  @Operation(summary = "Get a contact by UUID", description = "Retrieves a contact by its UUID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Contact found"),
      @ApiResponse(responseCode = "404", description = "Contact not found", content = @Content())
  })
  @GetMapping("/{contactUuid}")
  public ResponseEntity<ContactDto> getContactById(@PathVariable UUID contactUuid) {
    log.info("Fetching contact with UUID: {}", contactUuid);

    Optional<Contact> contactOpt = contactRepository.findByUuidAndDeletedAtIsNull(contactUuid);
    if (contactOpt.isPresent()) {
      log.debug("Contact found: {}", contactOpt.get().getUuid());
      return ResponseEntity.ok(ContactMapper.toDTO(contactOpt.get()));
    }

    log.warn("Contact not found: {}", contactUuid);
    throw new NotFoundException(String.format(ErrorMessages.CONTACT_NOT_FOUND, contactUuid));
  }

  @Operation(summary = "Create a new contact", description = "Creates a new contact with the provided details")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Contact created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content())
  })
  @PostMapping
  public ResponseEntity<ContactDto> createContact(@Valid @RequestBody ContactCreateDto newContactRequest,
      UriComponentsBuilder ucb) {
    log.info("Creating new contact");

    Contact contactToSave = ContactMapper.fromCreateDTO(newContactRequest);
    Contact savedContact = contactRepository.save(contactToSave);

    log.info("Contact created with UUID: {}", savedContact.getUuid());

    URI locationOfNewContact = ucb.path("/api/contacts/{contactUuid}").buildAndExpand(savedContact.getUuid()).toUri();
    return ResponseEntity.created(locationOfNewContact).body(ContactMapper.toDTO(savedContact));
  }

  @Operation(summary = "Delete a contact", description = "Deletes a contact by its UUID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Contact deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Contact not found", content = @Content())
  })
  @DeleteMapping("/{contactUuid}")
  public ResponseEntity<Void> deleteContact(@PathVariable UUID contactUuid) {
    log.info("Deleting contact with UUID: {}", contactUuid);

    Optional<Contact> contactOpt = contactRepository.findByUuidAndDeletedAtIsNull(contactUuid);
    if (!contactOpt.isPresent()) {
      log.warn("Delete failed - contact not found: {}", contactUuid);
      throw new NotFoundException(String.format(ErrorMessages.CONTACT_NOT_FOUND, contactUuid));
    }

    Contact contact = contactOpt.get();
    contact.setDeletedAt(LocalDateTime.now());
    contactRepository.save(contact);

    log.info("Contact soft-deleted: {}", contactUuid);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Update an existing contact", description = "Partially updates a contact using its UUID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Contact updated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content()),
      @ApiResponse(responseCode = "404", description = "Contact not found", content = @Content())
  })
  @PatchMapping("/{contactUuid}")
  public ResponseEntity<ContactDto> updateContact(@PathVariable UUID contactUuid,
      @Valid @RequestBody ContactPatchDto contactPatchDto) {
    log.info("Patching contact with UUID: {}", contactUuid);

    Optional<Contact> contactOpt = contactRepository.findByUuidAndDeletedAtIsNull(contactUuid);
    if (!contactOpt.isPresent()) {
      log.warn("Patch failed - contact not found: {}", contactUuid);
      throw new NotFoundException(String.format(ErrorMessages.CONTACT_NOT_FOUND, contactUuid));
    }

    Contact contact = contactOpt.get();
    ContactMapper.applyPatch(contact, contactPatchDto);
    contact.setUpdatedAt(LocalDateTime.now());
    contactRepository.save(contact);

    log.info("Contact updated: {}", contactUuid);
    return ResponseEntity.ok(ContactMapper.toDTO(contact));
  }

  @Operation(summary = "Search contacts", description = "Searches for contacts. Results are paginated with a default and max page (0-indexed) size of 10.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Contacts retrieved successfully")
  })
  @GetMapping("/search")
  public ResponseEntity<List<ContactDto>> searchContacts(@RequestParam String searchTerm, Pageable pageable) {
    int pageSize = Math.min(pageable.getPageSize(), maxPageSize);
    log.info("Searching contacts with term: '{}', page: {}, size: {}", searchTerm, pageable.getPageNumber(), pageSize);

    Page<ContactDto> page = contactRepository.findAllBySearchTerm(searchTerm, PageRequest.of(pageable.getPageNumber(),
        pageSize, pageable.getSortOr(Sort.by(Sort.Direction.ASC, "firstName")))).map(ContactMapper::toDTO);

    log.debug("Search returned {} contacts", page.getContent().size());
    return ResponseEntity.ok(page.getContent());
  }
}