package sigebi.users.services;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.dto_response.CompanyResponse;
import sigebi.users.entities.CompanyEntity;
import sigebi.users.repository.CompanyRepository;
import sigebi.users.service.CompanyService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetCompaniesByStatusTest {
    @Mock
    CompanyRepository companyRepository;

    @InjectMocks
    CompanyService companyService;

    @Test
    void getCompaniesByStatus_returnsFilteredList() {

        CompanyEntity company = CompanyEntity.builder()
                .id(1L)
                .nameCompany("Active Company")
                .status(true)
                .build();

        when(companyRepository.findAllByStatus(true))
                .thenReturn(List.of(company));

        List<CompanyResponse> result = companyService.getCompaniesByStatus(true);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getStatus());

        verify(companyRepository).findAllByStatus(true);
    }

}
