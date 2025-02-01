package assecor.assessment.backend.service;

import assecor.assessment.backend.dto.PersonRequest;
import assecor.assessment.backend.dto.PersonResponse;
import assecor.assessment.backend.exception.EntityNotFoundException;
import assecor.assessment.backend.exception.InvalidRequestException;
import assecor.assessment.backend.model.Color;
import assecor.assessment.backend.model.Person;
import assecor.assessment.backend.repository.PersonRepository;
import assecor.assessment.backend.util.PersonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {
    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    private Person person;
    private PersonRequest personRequest;

    @BeforeEach
    void setUp() {
        person = Person.builder()
                .id(1)
                .lastname("Hans")
                .firstname("Müller")
                .zipcode(12345)
                .city("berlin")
                .color(Color.getColor(1))
                .build();

        personRequest = PersonRequest.builder()
                .lastname("Hans")
                .firstname("Müller")
                .zipcode(12345)
                .city("berlin")
                .colorID(1)
                .build();
    }

    @Test
    void getAllPersonsTest() {
        when(personRepository.findAll()).thenReturn(Collections.singletonList(person));

        List<PersonResponse> persons = personService.getAllPersons();
        assertEquals(1, persons.size());
        assertEquals(PersonUtil.convertToDto(person), persons.get(0));
    }

    @Test
    void getPersonByIDSuccessTest(){
        when(personRepository.findById(any())).thenReturn(Optional.ofNullable(person));

        PersonResponse personResponse  = personService.getPersonByID(1);
        assertNotNull(personResponse);
        assertEquals(PersonUtil.convertToDto(person), personResponse);
    }

    @Test
    void getPersonByIDNotFoundTest(){
        when(personRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> personService.getPersonByID(1));
    }

    @Test
    void createPersonSuccessTest() {
        when(personRepository.count()).thenReturn(0L);
        when(personRepository.save(any(Person.class))).thenReturn(person);
        when(personRepository.existsById(person.getId())).thenReturn(true);

        String response = personService.createPerson(personRequest);
        assertNotNull(response);
        assertEquals("Person with id 1 added successfully!", response);
    }

    @Test
    void createPersonFailTest() {
        when(personRepository.count()).thenReturn(0L);
        when(personRepository.save(any(Person.class))).thenReturn(person);
        when(personRepository.existsById(person.getId())).thenReturn(false);

        String response = personService.createPerson(personRequest);
        assertNotNull(response);
        assertEquals("Adding new person failed!", response);
    }

    @Test
    void fetchPersonByColorSuccessTest(){
        when(personRepository.findAllByColor(any(Color.class))).thenReturn(Collections.singletonList(PersonUtil.convertToDto(person)));
        List<PersonResponse> personResponses  = personService.fetchPersonByColor("blau");

        assertEquals(1, personResponses.size());
        assertEquals(PersonUtil.convertToDto(person), personResponses.get(0));
    }

    @Test
    void fetchPersonByColorRequestEmptyTest(){
        assertThrows(InvalidRequestException.class, () -> personService.fetchPersonByColor(""));
    }
}