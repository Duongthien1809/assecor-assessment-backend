package assecor.assessment.backend.util;

import assecor.assessment.backend.dto.PersonRequest;
import assecor.assessment.backend.dto.PersonResponse;
import assecor.assessment.backend.exception.InvalidRequestException;
import assecor.assessment.backend.model.Color;
import assecor.assessment.backend.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonUtilTest {

    private PersonRequest personRequest;

    @BeforeEach
    void setUp() {
        personRequest = new PersonRequest("Hans", "Müller", 12345, "SomeCity", 1);
    }

    @Test
    void testConvertToDto() {
        Person person = new Person(1, "Hans", "Müller", 12345, "SomeCity", Color.blau);

        PersonResponse response = PersonUtil.convertToDto(person);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Hans", response.getLastname());
        assertEquals("Müller", response.getFirstname());
        assertEquals(12345, response.getZipcode());
        assertEquals("SomeCity", response.getCity());
        assertEquals("blau", response.getColor());
    }

    @Test
    void testCreatePersonInstance() {
        Person person = PersonUtil.createPersonInstance(personRequest);

        assertNotNull(person);
        assertEquals("Hans", person.getLastname());
        assertEquals("Müller", person.getFirstname());
        assertEquals(12345, person.getZipcode());
        assertEquals("SomeCity", person.getCity());
        assertEquals(Color.getColor(1), person.getColor());
    }

    @Test
    void testCreatePersonInstance_NullRequest() {
        assertThrows(InvalidRequestException.class, () -> PersonUtil.createPersonInstance(null),
                "Person request cannot be null");
    }

    @Test
    void testGetPerson() {
        Person person = PersonUtil.createPersonInstance(personRequest);

        assertNotNull(person);
        assertEquals("Hans", person.getLastname());
        assertEquals("Müller", person.getFirstname());
        assertEquals(12345, person.getZipcode());
        assertEquals("SomeCity", person.getCity());
        assertEquals(Color.getColor(1), person.getColor());
    }

}