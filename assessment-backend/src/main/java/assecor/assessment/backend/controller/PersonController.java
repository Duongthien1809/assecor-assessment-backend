package assecor.assessment.backend.controller;

import assecor.assessment.backend.dto.PersonRequest;
import assecor.assessment.backend.dto.PersonResponse;
import assecor.assessment.backend.service.FileService;
import assecor.assessment.backend.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PersonController {
    private final FileService fileService;
    private final PersonService personService;

    @Autowired
    public PersonController(FileService fileService, PersonService personService) {
        this.fileService = fileService;
        this.personService = personService;
    }


//    REST for load and save file

    @GetMapping( value = "/persons", produces = "application/json")
    public ResponseEntity<List<PersonResponse>> getAllPerson() {
        return new ResponseEntity<>(fileService.getAllPerson(), HttpStatus.OK);
    }

    @GetMapping(value = "/persons/{id}", produces = "application/json")
    public ResponseEntity<PersonResponse> getPersonById(@PathVariable("id") int id) {
        return new ResponseEntity<>(fileService.getPersonById(id), HttpStatus.OK);
    }

    @GetMapping(value = "/persons/color/{color}", produces = "application/json")
    public ResponseEntity<List<PersonResponse>> getPersonsByColor(@PathVariable("color") String color) {
        return new ResponseEntity<>(fileService.getPersonsByColor(color), HttpStatus.OK);
    }

    @PostMapping(value = "/persons", produces = "application/json")
    public ResponseEntity<String> addPerson( @RequestBody PersonRequest personRequest) {
        return new ResponseEntity<>(fileService.addPerson(personRequest), HttpStatus.CREATED);
    }

// REST for Database

    @GetMapping( value = "api/persons", produces = "application/json")
    public ResponseEntity<List<PersonResponse>> fetchAllPersons() {
        return new ResponseEntity<>(personService.getAllPersons(), HttpStatus.OK);
    }

    @GetMapping(value = "api/persons/{id}", produces = "application/json")
    public ResponseEntity<PersonResponse> fetchPersonByID(@PathVariable("id") int id) {
        return new ResponseEntity<>(personService.getPersonByID(id), HttpStatus.OK);
    }

    @GetMapping(value = "api/persons/color/{color}", produces = "application/json")
    public ResponseEntity<List<PersonResponse>> fetchPersonsByColor(@PathVariable("color") String color) {
        return new ResponseEntity<>(personService.fetchPersonByColor(color), HttpStatus.OK);
    }

    @PostMapping(value = "/api/persons", produces = "application/json")
    public ResponseEntity<String> addNewPerson( @RequestBody PersonRequest personRequest) {
        return new ResponseEntity<>(personService.createPerson(personRequest), HttpStatus.CREATED);
    }

}
