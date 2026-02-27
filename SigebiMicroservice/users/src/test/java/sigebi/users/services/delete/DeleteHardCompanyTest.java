package sigebi.users.services.delete;

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
class DeleteHardCompanyTest {

    @Mock
    CompanyRepository companyRepository;

    @InjectMocks
    CompanyService companyService;

    @Test
    void deleteHardCompany_existingCompany_deletesAndReturns() {

        Long companyId = 1L;

        CompanyEntity company = CompanyEntity.builder()
                .id(companyId)
                .nameCompany("Company A")
                .status(true)
                .build();

        when(companyRepository.findById(companyId))
                .thenReturn(Optional.of(company));

        CompanyResponse result =
                companyService.deleteHardCompany(companyId);

        assertNotNull(result);
        assertEquals(companyId, result.getId());
        assertEquals("Company A", result.getNameCompany());

        verify(companyRepository).findById(companyId);
        verify(companyRepository).delete(company);
    }
}