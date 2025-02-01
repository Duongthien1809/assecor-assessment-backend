package assecor.assessment.backend.io;

import assecor.assessment.backend.model.Color;
import assecor.assessment.backend.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileSystemTest {

    @InjectMocks
    private FileSystem fileSystem;

    private String filePath;

    @BeforeEach
    void setUp() {
        filePath = "static/sample-input.csv";
    }

    @Test
    void loadDataFromCsvSuccessTest() {
        int expectedSize = 9;
        List<Person> persons = fileSystem.loadDataFromCsv(filePath);

        assertEquals(expectedSize, persons.size());
        Person firstPerson = persons.get(0);
        assertEquals("Hans", firstPerson.getLastname());
        assertEquals("Müller", firstPerson.getFirstname());
        assertEquals(67742, firstPerson.getZipcode());
        assertEquals("Lauterecken", firstPerson.getCity());
        assertEquals(Color.blau, firstPerson.getColor());
        assertEquals(1, firstPerson.getId());

    }

    @Test
    void loadDataFromCsvInvalidDataLineTest() {
        String classpathFile = "test-invalid.csv";

        List<Person> persons = fileSystem.loadDataFromCsv(classpathFile);

        assertEquals(1, persons.size(), "only one line is expected.");
        Person firstPerson = persons.get(0);
        assertEquals("Hans", firstPerson.getLastname().trim());
        assertEquals("Müller", firstPerson.getFirstname().trim());
        assertEquals(67742, firstPerson.getZipcode());
        assertEquals("Lauterecken", firstPerson.getCity());
        assertEquals(Color.violet, firstPerson.getColor());
        assertEquals(1, firstPerson.getId());
    }

    @Test
    void testIsValidCsvLine() {
        String[] validLine = {"Hans", "Müller", "12345 SomeCity", "1"};
        String[] invalidLine1 = {"", "Müller", "12345 SomeCity", "1"};
        String[] invalidLine2 = {"Hans", "", "12345 SomeCity", "1"};
        String[] invalidLine3 = {"Hans", "Müller", "12345", "1"};

        assertFalse(fileSystem.isInvalidLine(validLine));
        assertTrue(fileSystem.isInvalidLine(invalidLine1));
        assertTrue(fileSystem.isInvalidLine(invalidLine2));
        assertTrue(fileSystem.isInvalidLine(invalidLine3));
    }

    @Test
    void writeDataToCsvSuccessTest() {
        Person person = Person.builder()
                .id(1)
                .lastname("Hans")
                .firstname("Müller")
                .zipcode(12345)
                .city("berlin")
                .color(Color.getColor(1))
                .build();
        assertTrue(fileSystem.writeDataToCsv(filePath, person));
    }


}