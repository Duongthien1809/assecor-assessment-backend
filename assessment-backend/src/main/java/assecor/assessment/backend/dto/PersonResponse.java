package assecor.assessment.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponse {
    private int id;
    private String lastname;
    private String firstname;
    private long zipcode;
    private String city;
    private String color;
}
