package com.rise.contactsapi.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rise.contactsapi.model.Contact;

public interface ContactRepository extends JpaRepository<Contact, UUID> {
  Optional<Contact> findByUuid(UUID contactUuid);

  Page<Contact> findAllByDeletedAtIsNull(Pageable pageable);

  @Query("SELECT c FROM Contact c WHERE " +
      "(LOWER(c.firstName) LIKE %:searchTerm% OR " +
      "LOWER(c.lastName) LIKE %:searchTerm% OR " +
      "LOWER(c.phoneNumber) LIKE %:searchTerm% OR " +
      "LOWER(c.address) LIKE %:searchTerm%) AND " +
      "c.deletedAt IS NULL")
  Page<Contact> findAllBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
}