package com.rise.contactsapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.rise.contactsapi.dto.ContactCreateDto;
import com.rise.contactsapi.dto.ContactPatchDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ContactIntegrationTest {
  @Autowired
  TestRestTemplate restTemplate;
  @Autowired
  JdbcTemplate jdbcTemplate;

  @Test
  void shouldReturnAContact() {
    ContactCreateDto request = new ContactCreateDto("James", "Dalton", "1234567890", "4 Abbey St");
    ResponseEntity<String> postResponse = restTemplate.postForEntity("/api/contacts", request, String.class);
    assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    URI location = postResponse.getHeaders().getLocation();
    assertThat(location).isNotNull();

    DocumentContext postDocumentContext = JsonPath.parse(postResponse.getBody());
    String newUuid = postDocumentContext.read("$.uuid");

    ResponseEntity<String> getResponse = restTemplate.getForEntity("/api/contacts/" + newUuid, String.class);
    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
    String uuid = documentContext.read("$.uuid");
    String firstName = documentContext.read("$.firstName");
    String lastName = documentContext.read("$.lastName");
    String phoneNumber = documentContext.read("$.phoneNumber");
    String address = documentContext.read("$.address");
    String updatedAt = documentContext.read("$.updatedAt");
    String createdAt = documentContext.read("$.createdAt");
    assertThat(uuid).isEqualTo(newUuid);
    assertThat(firstName).isEqualTo("James");
    assertThat(lastName).isEqualTo("Dalton");
    assertThat(phoneNumber).isEqualTo("1234567890");
    assertThat(address).isEqualTo("4 Abbey St");
    assertThat(updatedAt).isNotNull();
    assertThat(createdAt).isNotNull();
  }

  @Test
  void shouldReturn404WhenContactNotFound() {
    String nonExistentUuid = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";

    ResponseEntity<String> response = restTemplate.getForEntity("/api/contacts/" + nonExistentUuid, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    DocumentContext json = JsonPath.parse(response.getBody());
    String errorMessage = json.read("$.error");

    assertThat(errorMessage).contains("not found with id");
  }

  @Test
  void shouldCreateAContact() {
    ContactCreateDto request = new ContactCreateDto("Tony", "Doey", "3471112233", "1 Main St");
    ResponseEntity<String> response = restTemplate.postForEntity("/api/contacts", request, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    URI location = response.getHeaders().getLocation();
    assertThat(location).isNotNull();

    DocumentContext documentContext = JsonPath.parse(response.getBody());
    String uuid = documentContext.read("$.uuid");
    String firstName = documentContext.read("$.firstName");
    String lastName = documentContext.read("$.lastName");
    String phoneNumber = documentContext.read("$.phoneNumber");
    String address = documentContext.read("$.address");
    String updatedAt = documentContext.read("$.updatedAt");
    String createdAt = documentContext.read("$.createdAt");
    assertThat(uuid).isNotNull();
    assertThat(firstName).isEqualTo("Tony");
    assertThat(lastName).isEqualTo("Doey");
    assertThat(phoneNumber).isEqualTo("3471112233");
    assertThat(address).isEqualTo("1 Main St");
    assertThat(updatedAt).isNotNull();
    assertThat(createdAt).isNotNull();
  }

  @Test
  void shouldReturn400WhenCreatingContactWithInvalidData() {
    ContactCreateDto request = new ContactCreateDto("Tom", null, "123", "123 Derby St");

    ResponseEntity<String> response = restTemplate.postForEntity("/api/contacts", request, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void shouldDeleteAContact() {
    ContactCreateDto request = new ContactCreateDto("Delete", "Me", "1234567890", "4 Test St");
    ResponseEntity<String> response = restTemplate.postForEntity("/api/contacts", request, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    URI location = response.getHeaders().getLocation();
    assertThat(location).isNotNull();

    DocumentContext documentContext = JsonPath.parse(response.getBody());
    String uuid = documentContext.read("$.uuid");

    ResponseEntity<Void> deletedResponse = restTemplate.exchange("/api/contacts/" + uuid, HttpMethod.DELETE, null,
        Void.class);
    assertThat(deletedResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ResponseEntity<Void> getResponse = restTemplate.getForEntity("/api/contacts/" + uuid, Void.class);
    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturn404WhenDeletingNonexistentContact() {
    String nonExistentUuid = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";

    ResponseEntity<String> response = restTemplate.exchange("/api/contacts/" + nonExistentUuid, HttpMethod.DELETE, null,
        String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    DocumentContext json = JsonPath.parse(response.getBody());
    String error = json.read("$.error");
    assertThat(error).contains("not found with id");
  }

  @Test
  void shouldUpdateContactPartially() {
    ContactCreateDto request = new ContactCreateDto("Jane", "Doey", "555-000-1234", "123 Start St");
    ResponseEntity<String> response = restTemplate.postForEntity("/api/contacts", request, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    URI location = response.getHeaders().getLocation();
    assertThat(location).isNotNull();

    ContactPatchDto patchDto = new ContactPatchDto();
    patchDto.setAddress("456 New St");
    HttpEntity<ContactPatchDto> patchRequest = new HttpEntity<>(patchDto);

    ResponseEntity<String> patchResponse = restTemplate.exchange(location, HttpMethod.PATCH, patchRequest,
        String.class);
    assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    DocumentContext documentContext = JsonPath.parse(patchResponse.getBody());
    String firstName = documentContext.read("$.firstName");
    String address = documentContext.read("$.address");
    assertThat(firstName).isEqualTo("Jane");
    assertThat(address).isEqualTo("456 New St");
  }

  @Test
  void shouldReturn404WhenPatchingNonexistentContact() {
    String nonExistentUuid = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";

    ContactPatchDto patchDto = new ContactPatchDto();
    patchDto.setAddress("456 New St");
    HttpEntity<ContactPatchDto> patchRequest = new HttpEntity<>(patchDto);

    ResponseEntity<String> response = restTemplate.exchange("/api/contacts/" + nonExistentUuid, HttpMethod.PATCH,
        patchRequest,
        String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    DocumentContext json = JsonPath.parse(response.getBody());
    String errorMessage = json.read("$.error");

    assertThat(errorMessage).contains("not found with id");
  }

  @Test
  void shouldReturnContactsMatchingSearchTerm() {
    ContactCreateDto request = new ContactCreateDto("Maximus", "Johnson", "9876543210", "Elm St");
    ResponseEntity<Void> getResponse = restTemplate.postForEntity("/api/contacts", request, Void.class);
    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ResponseEntity<String> searchResponse = restTemplate.getForEntity("/api/contacts/search?searchTerm=maximus&size=1",
        String.class);
    assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    DocumentContext json = JsonPath.parse(searchResponse.getBody());
    int resultCount = json.read("$.length()");
    assertThat(resultCount).isEqualTo(1);

    String firstName = json.read("$[0].firstName");
    assertThat(firstName).isEqualTo("Maximus");
  }

}
