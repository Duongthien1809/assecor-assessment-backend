package assecor.assessment.backend.io;

import assecor.assessment.backend.model.Color;
import assecor.assessment.backend.model.Person;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class FileSystem {

    public List<Person> loadDataFromCsv(String filePath) {
        Resource resource = new ClassPathResource(filePath);
        List<Person> persons = new ArrayList<>();
        int count = 1;
        try (CSVReader reader = createCsvReader(resource)) {
            List<String[]> lines = reader.readAll();
            log.info("Loaded {} lines from {}", lines.size(), filePath);

            for (String[] line : lines) {
                if (isInvalidLine(line)) {
                    log.warn("Skipping invalid data line: {}", Arrays.toString(line));
                    continue;
                }

                Person person = createPersonFromCsvLine(count, line);
                persons.add(person);
                count++;
            }
        } catch (CsvException | IOException e) {
            log.error("Error reading CSV file: {}", filePath, e);
            throw new RuntimeException("Error reading CSV file", e);
        }

        return persons;
    }

    public boolean writeDataToCsv(String filePath, Person person) {
        try {
            List<String[]> lines = readCsvFile(filePath);

            appendPersonToCsv(lines, person);

            writeCsvFile(filePath, lines);
            return true;
        } catch (IOException | CsvException e) {
            log.error("Error writing data to CSV file: {}", e.getMessage(), e);
            return false;
        }
    }

    private CSVReader createCsvReader(Resource resource) throws IOException {
        return new CSVReader(new InputStreamReader(resource.getInputStream()));
    }

    boolean isInvalidLine(String[] line) {
        return line.length < 4 || line[0].trim().isEmpty() || line[1].trim().isEmpty() || !hasValidZipcodeAndCity(line[2]);
    }

    private boolean hasValidZipcodeAndCity(String line) {
        return line.trim().split(" ", 2).length == 2;
    }

    private Person createPersonFromCsvLine(int id, String[] line) {
        String[] zipcodeCity = line[2].trim().split(" ", 2);

        int colorCode = Integer.parseInt(line[3].trim());
        Color color = Color.getColor(colorCode);
        return new Person(
                id,
                line[1].trim(),
                line[0].trim(),
                Long.parseLong(zipcodeCity[0].trim()),
                zipcodeCity[1].trim(),
                color
        );
    }

    private List<String[]> readCsvFile(String filePath) throws IOException, CsvException {
        Resource resource = new ClassPathResource(filePath);
        File inputFile = resource.getFile();

        try (CSVReader csvReader = new CSVReader(new FileReader(inputFile))) {
            return csvReader.readAll();
        }
    }

    private void appendPersonToCsv(List<String[]> lines, Person person) {
        int colorIndex = person.getColor().ordinal() + 1;
        String[] newLine = {
                person.getLastname(),
                person.getFirstname(),
                person.getZipcode() + " " + person.getCity(),
                String.valueOf(colorIndex)
        };
        lines.add(newLine);
    }

    private void writeCsvFile(String filePath, List<String[]> lines) throws IOException {
        Resource resource = new ClassPathResource(filePath);
        File inputFile = resource.getFile();

        try (CSVWriter writer = new CSVWriter(new FileWriter(inputFile),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.RFC4180_LINE_END)) {
            writer.writeAll(lines);
        }
    }
}
