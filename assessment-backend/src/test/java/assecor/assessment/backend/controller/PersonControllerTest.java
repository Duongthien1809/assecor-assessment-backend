package assecor.assessment.backend.controller;

import assecor.assessment.backend.dto.PersonRequest;
import assecor.assessment.backend.exception.EntityNotFoundException;
import assecor.assessment.backend.exception.InvalidRequestException;
import assecor.assessment.backend.model.Color;
import assecor.assessment.backend.model.Person;
import assecor.assessment.backend.service.FileService;
import assecor.assessment.backend.service.PersonService;
import assecor.assessment.backend.util.PersonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = PersonController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonService personService;

    @MockitoBean
    private FileService fileService;


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

    //    Test Controller from FileSystem
    @Test
    void getAllPersonsFromFileSystemTest() throws Exception {
        when(fileService.getAllPerson()).thenReturn(Collections.singletonList(PersonUtil.convertToDto(person)));

        ResultActions response = mockMvc.perform(get("/persons").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastname").value("Hans"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstname").value("Müller"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].zipcode").value(12345))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].city").value("berlin"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].color").value("blau"));
    }

    @Test
    void getPersonByIdSuccessFromFileSystemTest() throws Exception {
        when(fileService.getPersonById(any(Integer.class))).thenReturn(PersonUtil.convertToDto(person));

        ResultActions response = mockMvc.perform(get("/persons/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastname").value("Hans"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstname").value("Müller"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipcode").value(12345))
                .andExpect(MockMvcResultMatchers.jsonPath("$.city").value("berlin"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value("blau"));
    }

    @Test
    void getPersonsByIDFromFileThrowErrorFromFileSystemTest() throws Exception {
        when(fileService.getPersonById(any(Integer.class))).thenThrow(new EntityNotFoundException("Could not find person with id 1"));

        ResultActions response = mockMvc.perform(get("/persons/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("Could not find person with id 1")));
    }

    @Test
    void getPersonsByColorFromFileSystemTest() throws Exception {
        when(fileService.getPersonsByColor(any(String.class))).thenReturn(Collections.singletonList(PersonUtil.convertToDto(person)));

        ResultActions response = mockMvc.perform(get("/persons/color/blau").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastname").value("Hans"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstname").value("Müller"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].zipcode").value(12345))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].city").value("berlin"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].color").value("blau"));
    }

    @Test
    void getPersonsByColorThrowErrorFromFileSystemTest() throws Exception {
        when(fileService.getPersonsByColor(any(String.class))).thenThrow(new EntityNotFoundException("Could not find person with color blau"));

        ResultActions response = mockMvc.perform(get("/persons/color/blau").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("Could not find person with color blau")));

    }

    @Test
    void addPersonSuccessFromFileSystemTest() throws Exception {
        String expectedResponse = "Person added successfully";
        when(fileService.addPerson(any(PersonRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(personRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));

        verify(fileService, times(1)).addPerson(any(PersonRequest.class));
    }

    @Test
    void addPersonFailFromFileSystemTest() throws Exception {
        String expectedFailResponse = "Could not add person";
        when(fileService.addPerson(any(PersonRequest.class))).thenReturn(expectedFailResponse);

        mockMvc.perform(post("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(personRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string(expectedFailResponse));

        verify(fileService, times(1)).addPerson(any(PersonRequest.class));
    }

    @Test
    void addPersonThrowErrorColorNotFoundFromFileSystemTest() throws Exception {
        PersonRequest personRequest_temp = PersonRequest.builder()
                .lastname("Hans")
                .firstname("Müller")
                .zipcode(12345)
                .city("berlin")
                .colorID(20)
                .build();

        String expectedFailResponse = "Color ID 20 not found";

        when(fileService.addPerson(any(PersonRequest.class)))
                .thenThrow(new InvalidRequestException(expectedFailResponse));

        mockMvc.perform(post("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(personRequest_temp)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(expectedFailResponse));

        verify(fileService, times(1)).addPerson(any(PersonRequest.class));
    }


    @Test
    void addPersonThrowErrorInvalidPersonDataFromFileSystemTest() throws Exception {
        PersonRequest personRequest_temp = PersonRequest.builder()
                .lastname("")
                .firstname("Müller")
                .zipcode(12345)
                .city("berlin")
                .colorID(1)
                .build();

        String expectedFailResponse = "Invalid person data: All fields must be filled correctly.";

        when(fileService.addPerson(any(PersonRequest.class)))
                .thenThrow(new InvalidRequestException(expectedFailResponse));

        mockMvc.perform(post("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(personRequest_temp)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(expectedFailResponse));

        verify(fileService, times(1)).addPerson(any(PersonRequest.class));
    }

//    Test Controller for database

    @Test
    void getAllPersonsFromPersonSystemTest() throws Exception {
        when(personService.getAllPersons()).thenReturn(Collections.singletonList(PersonUtil.convertToDto(person)));

        ResultActions response = mockMvc.perform(get("/api/persons").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastname").value("Hans"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstname").value("Müller"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].zipcode").value(12345))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].city").value("berlin"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].color").value("blau"));
    }

    @Test
    void getPersonByIdSuccessFromPersonSystemTest() throws Exception {
        when(personService.getPersonByID(any(Integer.class))).thenReturn(PersonUtil.convertToDto(person));

        ResultActions response = mockMvc.perform(get("/api/persons/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastname").value("Hans"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstname").value("Müller"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipcode").value(12345))
                .andExpect(MockMvcResultMatchers.jsonPath("$.city").value("berlin"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value("blau"));
    }

    @Test
    void getPersonsByIDFromFileThrowErrorFromPersonSystemTest() throws Exception {
        when(personService.getPersonByID(any(Integer.class))).thenThrow(new EntityNotFoundException("Could not find person with id 1"));

        ResultActions response = mockMvc.perform(get("/api/persons/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("Could not find person with id 1")));
    }

    @Test
    void getPersonsByColorFromPersonSystemTest() throws Exception {
        when(personService.fetchPersonByColor(any(String.class))).thenReturn(Collections.singletonList(PersonUtil.convertToDto(person)));

        ResultActions response = mockMvc.perform(get("/api/persons/color/blau").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastname").value("Hans"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstname").value("Müller"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].zipcode").value(12345))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].city").value("berlin"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].color").value("blau"));
    }

    @Test
    void getPersonsByColorThrowErrorFromPersonSystemTest() throws Exception {
        when(personService.fetchPersonByColor(any(String.class))).thenThrow(new EntityNotFoundException("Could not find person with color blau"));

        ResultActions response = mockMvc.perform(get("/api/persons/color/blau").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("Could not find person with color blau")));

    }

    @Test
    void addPersonSuccessFromPersonSystemTest() throws Exception {
        String expectedResponse = "Person added successfully";
        when(personService.createPerson(any(PersonRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(personRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));

        verify(personService, times(1)).createPerson(any(PersonRequest.class));
    }

    @Test
    void addPersonFailFromPersonSystemTest() throws Exception {
        String expectedFailResponse = "Could not add person";
        when(personService.createPerson(any(PersonRequest.class))).thenReturn(expectedFailResponse);

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(personRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string(expectedFailResponse));

        verify(personService, times(1)).createPerson(any(PersonRequest.class));
    }

    @Test
    void addPersonThrowErrorColorNotFoundFromPersonSystemTest() throws Exception {
        PersonRequest personRequest_temp = PersonRequest.builder()
                .lastname("Hans")
                .firstname("Müller")
                .zipcode(12345)
                .city("berlin")
                .colorID(20)
                .build();

        String expectedFailResponse = "Color ID 20 not found";

        when(personService.createPerson(any(PersonRequest.class)))
                .thenThrow(new InvalidRequestException(expectedFailResponse));

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(personRequest_temp)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(expectedFailResponse));

        verify(personService, times(1)).createPerson(any(PersonRequest.class));
    }


    @Test
    void addPersonThrowErrorInvalidPersonDataFromPersonSystemTest() throws Exception {
        PersonRequest personRequest_temp = PersonRequest.builder()
                .lastname("")
                .firstname("Müller")
                .zipcode(12345)
                .city("berlin")
                .colorID(1)
                .build();

        String expectedFailResponse = "Invalid person data: All fields must be filled correctly.";

        when(personService.createPerson(any(PersonRequest.class)))
                .thenThrow(new InvalidRequestException(expectedFailResponse));

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(personRequest_temp)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(expectedFailResponse));

        verify(personService, times(1)).createPerson(any(PersonRequest.class));
    }

}