package sigebi.users.services.get;

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
class GetCompaniesByStatusTest {

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

        List<CompanyResponse> result =
                companyService.getCompaniesByStatus(true);

        assertEquals(1, result.size());
        assertEquals("Active Company", result.get(0).getNameCompany());
        assertTrue(result.get(0).getStatus());

        verify(companyRepository).findAllByStatus(true);
    }

    @Test
    void getCompaniesByStatus_empty_returnsEmptyList() {

        when(companyRepository.findAllByStatus(false))
                .thenReturn(List.of());

        List<CompanyResponse> result =
                companyService.getCompaniesByStatus(false);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(companyRepository).findAllByStatus(false);
    }
}