package com.rise.contactsapi.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "contacts")
public class Contact {

  @Id
  private UUID uuid;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String address;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;

  public Contact() {
  }

  public Contact(UUID uuid, String firstName, String lastName, String phoneNumber, String address,
      LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.uuid = uuid;
    this.firstName = firstName;
    this.lastName = lastName;
    this.phoneNumber = phoneNumber;
    this.address = address;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  @PrePersist
  private void generateUuid() {
    if (uuid == null) {
      uuid = UUID.randomUUID();
    }
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public LocalDateTime getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(LocalDateTime deletedAt) {
    this.deletedAt = deletedAt;
  }

  @Override
  public String toString() {
    return "Contact{uuid=" + this.uuid +
        ", firstName='" + this.firstName +
        ", lastName='" + this.lastName +
        ", phoneNumber='" + this.phoneNumber +
        ", address='" + this.address + "}";
  }

  @Override
  public boolean equals(Object contact) {
    if (contact == this) {
      return true;
    } else if (!(contact instanceof Contact)) {
      return false;
    } else {
      Contact that = (Contact) contact;
      return this.uuid.equals(that.getUuid()) &&
          this.firstName.equals(that.getFirstName()) &&
          this.lastName.equals(that.getLastName()) &&
          this.phoneNumber.equals(that.getPhoneNumber()) &&
          this.address.equals(that.getAddress()) &&
          this.updatedAt.equals(that.getUpdatedAt()) &&
          this.createdAt.equals(that.getCreatedAt());
    }
  }

  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= this.uuid.hashCode();
    h$ *= 1000003;
    h$ ^= this.firstName.hashCode();
    h$ *= 1000003;
    h$ ^= this.lastName.hashCode();
    h$ *= 1000003;
    h$ ^= this.phoneNumber.hashCode();
    h$ *= 1000003;
    h$ ^= this.address.hashCode();
    h$ *= 1000003;
    h$ ^= this.updatedAt.hashCode();
    h$ *= 1000003;
    h$ ^= this.createdAt.hashCode();
    return h$;
  }
}
