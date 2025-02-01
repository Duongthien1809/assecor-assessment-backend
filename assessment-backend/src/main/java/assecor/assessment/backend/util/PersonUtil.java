package assecor.assessment.backend.util;

import assecor.assessment.backend.dto.PersonRequest;
import assecor.assessment.backend.dto.PersonResponse;
import assecor.assessment.backend.exception.InvalidParamException;
import assecor.assessment.backend.exception.InvalidRequestException;
import assecor.assessment.backend.model.Color;
import assecor.assessment.backend.model.Person;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersonUtil {
    public static PersonResponse convertToDto(Person person) {
        return PersonResponse.builder().id(person.getId()).lastname(person.getLastname()).firstname(person.getFirstname()).zipcode(person.getZipcode()).city(person.getCity()).color(person.getColor().name()).build();
    }

    public static Person createPersonInstance(PersonRequest personRequest) {
        if (isInvalidPersonRequest(personRequest)) {
            throw new InvalidRequestException("Invalid person data: All fields must be filled correctly.");
        }
        return getPerson(personRequest);
    }

    public static Color parseColor(String color) {
        try {
            return Color.valueOf(color);
        } catch (IllegalArgumentException e) {
            throw new InvalidParamException("Invalid color value: " + color);
        }
    }

    private static boolean isInvalidPersonRequest(PersonRequest personRequest) {
        return isNullOrEmpty(personRequest.getFirstname()) || isNullOrEmpty(personRequest.getLastname()) || personRequest.getZipcode() <= 0 || isNullOrEmpty(personRequest.getCity());
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static Person getPerson(PersonRequest personRequest) {
        if (personRequest == null) throw new InvalidRequestException("Person request cannot be null");
        Person person = new Person();
        person.setFirstname(personRequest.getFirstname());
        person.setLastname(personRequest.getLastname());
        person.setZipcode(personRequest.getZipcode());
        person.setCity(personRequest.getCity());
        person.setColor(Color.getColor(personRequest.getColorID()));
        return person;
    }
}
