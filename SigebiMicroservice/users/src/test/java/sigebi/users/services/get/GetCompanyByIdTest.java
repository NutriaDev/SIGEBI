package sigebi.users.services.get;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.entities.CompanyEntity;
import sigebi.users.repository.CompanyRepository;
import sigebi.users.service.CompanyService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class GetCompanyByIdTest {
    @Mock
    CompanyRepository companyRepository;

    @InjectMocks
    CompanyService companyService;

    @Test
    void getCompanyById_existingCompany_returnsCompany() {

        CompanyEntity company = CompanyEntity.builder()
                .id(1L)
                .nameCompany("Company A")
                .status(true)
                .build();

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        CompanyEntity result = companyService.getCompanyById(1L);

        assertNotNull(result);
        assertEquals("Company A", result.getNameCompany());

        verify(companyRepository).findById(1L);
    }
}
