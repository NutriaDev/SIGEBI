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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteHardCompanyTest {
    @Mock
    CompanyRepository companyRepository;

    @InjectMocks
    CompanyService companyService;

    @Test
    void deleteHardCompany_existingCompany_deletesAndReturns() {

        CompanyEntity company = CompanyEntity.builder()
                .id(1L)
                .nameCompany("Company A")
                .status(true)
                .build();

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        CompanyResponse result = companyService.deleteHardCompany(1L);

        assertNotNull(result);
        assertEquals("Company A", result.getNameCompany());

        verify(companyRepository).delete(company);
    }

}
