package assecor.assessment.backend.service;

import assecor.assessment.backend.dto.PersonRequest;
import assecor.assessment.backend.dto.PersonResponse;
import assecor.assessment.backend.exception.EntityNotFoundException;
import assecor.assessment.backend.io.FileSystem;
import assecor.assessment.backend.model.Color;
import assecor.assessment.backend.model.Person;
import assecor.assessment.backend.util.PersonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {


    private FileSystem fileSystem;
    private final String filePath = "static/sample-input.csv";

    @Autowired
    public FileService(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public List<PersonResponse> getAllPerson() {
        List<Person> persons = fileSystem.loadDataFromCsv(filePath);
        log.info("Loaded {} persons", persons.size());
        return persons.stream().map(PersonUtil::convertToDto).collect(Collectors.toList());
    }

    public PersonResponse getPersonById(int id) {
        return fileSystem.loadDataFromCsv(filePath).stream().filter(person -> person.getId() == id).map(PersonUtil::convertToDto).findFirst().orElseThrow(() -> new EntityNotFoundException(String.format("Could not find person with id %d", id)));
    }

    public List<PersonResponse> getPersonsByColor(String color) {
        Color parsedColor = PersonUtil.parseColor(color);
        List<PersonResponse> response = fileSystem.loadDataFromCsv(filePath).stream().filter(person -> person.getColor().equals(parsedColor)).map(PersonUtil::convertToDto).toList();
        if (response.isEmpty()) {
            throw new EntityNotFoundException(String.format("Could not find person with color '%s'", color));
        }
        return response;
    }

    public String addPerson(PersonRequest personRequest) {
        if (personRequest == null) {
            throw new IllegalArgumentException("Person request cannot be null");
        }
        Person person = PersonUtil.createPersonInstance(personRequest);
        log.info("Adding person: {}", person);

        boolean isWritten = fileSystem.writeDataToCsv(filePath, person);

        return isWritten ? "Person added successfully" : "Could not add person";
    }

}
