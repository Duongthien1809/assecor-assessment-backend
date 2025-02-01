package assecor.assessment.backend.service;

import assecor.assessment.backend.dto.PersonRequest;
import assecor.assessment.backend.dto.PersonResponse;
import assecor.assessment.backend.exception.EntityNotFoundException;
import assecor.assessment.backend.io.FileSystem;
import assecor.assessment.backend.model.Color;
import assecor.assessment.backend.model.Person;
import assecor.assessment.backend.util.PersonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    FileSystem fileSystem;

    @InjectMocks
    FileService fileService;

    private Person person;

    @BeforeEach
    void setUp() {
        person = Person.builder()
                .id(1)
                .lastname("doe")
                .firstname("john")
                .zipcode(12345)
                .city("berlin")
                .color(Color.getColor(1))
                .build();
    }

    @Test
    void getAllPersonSuccessTest() {
        when(fileSystem.loadDataFromCsv(anyString())).thenReturn(List.of(person));

        List<PersonResponse> persons = fileService.getAllPerson();
        assertEquals(1, persons.size());
        assertEquals(PersonUtil.convertToDto(person), persons.get(0));
    }

    @Test
    void getAllPersonFailTest() {
        when(fileSystem.loadDataFromCsv(anyString())).thenReturn(Collections.emptyList());

        List<PersonResponse> persons = fileService.getAllPerson();
        assertEquals(0, persons.size());
    }

    @Test
    void getPersonByIdSuccessTest() {
        when(fileSystem.loadDataFromCsv(anyString())).thenReturn(List.of(person));

        PersonResponse person = fileService.getPersonById(1);
        assertNotNull(person);
        assertEquals(PersonUtil.convertToDto(this.person), person);
    }

    @Test
    void getPersonByIdNotFoundTest() {
        when(fileSystem.loadDataFromCsv(anyString())).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class, () -> fileService.getPersonById(3));
    }

    @Test
    void getPersonsByColorSuccessTest() {
        when(fileSystem.loadDataFromCsv(anyString())).thenReturn(List.of(person));

        List<PersonResponse> persons = fileService.getPersonsByColor("blau");
        assertEquals(1, persons.size());
        assertEquals(PersonUtil.convertToDto(person), persons.get(0));
    }

    @Test
    void getPersonsByColorNotFoundTest() {
        when(fileSystem.loadDataFromCsv(anyString())).thenReturn(List.of(person));

        assertThrows(EntityNotFoundException.class, () -> fileService.getPersonsByColor("rot"));
    }

    @Test
    void addPersonSuccessTest() {
        PersonRequest request = new PersonRequest("doe", "john", 12345, "Berlin", 1);
        when(fileSystem.writeDataToCsv(anyString(), any())).thenReturn(true);

        String response = fileService.addPerson(request);
        assertEquals("Person added successfully", response);
    }

    @Test
    void addPersonFailTest() {
        PersonRequest request = new PersonRequest("doe", "john", 12345, "Berlin", 1);
        when(fileSystem.writeDataToCsv(anyString(), any())).thenReturn(false);

        String response = fileService.addPerson(request);
        assertEquals("Could not add person", response);
    }

}