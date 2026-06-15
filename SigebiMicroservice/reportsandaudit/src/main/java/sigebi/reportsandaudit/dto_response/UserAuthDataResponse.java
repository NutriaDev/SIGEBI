package sigebi.reportsandaudit.dto_response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserAuthDataResponse {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;

    public String getFullName() {
        String first = firstName != null ? firstName.trim() : "";
        String last  = lastName  != null ? lastName.trim()  : "";
        String full  = (first + " " + last).trim();
        return full.isBlank() ? email : full;
    }
}