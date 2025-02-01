package assecor.assessment.backend.service;

import assecor.assessment.backend.dto.PersonRequest;
import assecor.assessment.backend.dto.PersonResponse;
import assecor.assessment.backend.exception.EntityNotFoundException;
import assecor.assessment.backend.exception.InvalidRequestException;
import assecor.assessment.backend.model.Color;
import assecor.assessment.backend.model.Person;
import assecor.assessment.backend.repository.PersonRepository;
import assecor.assessment.backend.util.PersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PersonService {
    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<PersonResponse> getAllPersons() {
        List<Person> persons = personRepository.findAll();
        return persons.stream().map(PersonUtil::convertToDto).toList();
    }

    public PersonResponse getPersonByID(int id) {
        return personRepository.findById(id)
                .map(PersonUtil::convertToDto)
                .orElseThrow(() -> new EntityNotFoundException("Person with ID " + id + " not found!"));
    }

    public String createPerson(PersonRequest personRequest) {
        Person person = PersonUtil.createPersonInstance(personRequest);
        person.setId((int) (personRepository.count() + 1));

        Person savedPerson = personRepository.save(person);

        return personRepository.existsById(savedPerson.getId())
                ? "Person with id " + savedPerson.getId() + " added successfully!"
                : "Adding new person failed!";
    }

    public List<PersonResponse> fetchPersonByColor(String color) {
        Color parsedColor = parseColor(color);
        return personRepository.findAllByColor(parsedColor);
    }

    private Color parseColor(String color) {
        try {
            return Color.valueOf(color);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Invalid color value: " + color);
        }
    }
}
