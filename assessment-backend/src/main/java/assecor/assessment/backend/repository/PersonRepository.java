package assecor.assessment.backend.repository;

import assecor.assessment.backend.dto.PersonResponse;
import assecor.assessment.backend.model.Color;
import assecor.assessment.backend.model.Person;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PersonRepository extends MongoRepository<Person, Integer> {

    List<PersonResponse> findAllByColor(Color color);
}
