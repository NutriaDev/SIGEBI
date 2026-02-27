package sigebi.users.services.get;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.exception.CompanyNotFoundException;
import sigebi.users.repository.CompanyRepository;
import sigebi.users.service.CompanyService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCompanyByIdNotFoundTest {

    @Mock
    CompanyRepository companyRepository;

    @InjectMocks
    CompanyService companyService;

    @Test
    void getCompanyById_notFound_throwsException() {

        when(companyRepository.findById(99L))
                .thenReturn(Optional.empty());

        CompanyNotFoundException exception = assertThrows(
                CompanyNotFoundException.class,
                () -> companyService.getCompanyById(99L)
        );

        assertEquals("Company not found with ID: 99",
                exception.getMessage());

        verify(companyRepository).findById(99L);
    }
}