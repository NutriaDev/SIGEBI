package sigebi.users.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

@ExtendWith(MockitoExtension.class)
public class GetAllCompaniesTest {
    @Mock
    CompanyRepository companyRepository;

    @InjectMocks
    CompanyService companyService;

    @Test
    void getAllCompanies_returnsList() {

        CompanyEntity company1 = CompanyEntity.builder()
                .id(1L)
                .nameCompany("Company A")
                .status(true)
                .build();

        CompanyEntity company2 = CompanyEntity.builder()
                .id(2L)
                .nameCompany("Company B")
                .status(false)
                .build();

        when(companyRepository.findAll())
                .thenReturn(List.of(company1, company2));

        List<CompanyResponse> result = companyService.getAllCompanies();

        assertEquals(2, result.size());
        assertEquals("Company A", result.get(0).getNameCompany());

        verify(companyRepository).findAll();
    }

}
