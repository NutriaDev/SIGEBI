package sigebi.reportsandaudit.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import sigebi.reportsandaudit.dto_request.SparePartItem;

import java.util.List;

@Converter
public class SparePartsListConverter implements AttributeConverter<List<SparePartItem>, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<SparePartItem> attribute) {
        if (attribute == null) return null;
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializando lista de repuestos", e);
        }
    }

    @Override
    public List<SparePartItem> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return List.of();
        try {
            return mapper.readValue(dbData, new TypeReference<List<SparePartItem>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializando lista de repuestos", e);
        }
    }
}
