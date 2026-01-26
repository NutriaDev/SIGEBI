package sigebi.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sigebi.users.dto_response.CompanyResponse;
import sigebi.users.entities.CompanyEntity;
import sigebi.users.exception.CompanyNotFoundException;
import sigebi.users.repository.CompanyRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CompanyEntity getCompanyById(Long id){
        return companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with ID: " + id));
    }

    public List<CompanyResponse> getCompaniesByStatus(boolean status) {
        return companyRepository.findAllByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CompanyResponse deleteHardCompany(Long id){
        return companyRepository.findById(id)
                .map(company ->{
                    companyRepository.delete(company);
                    return mapToResponse(company);
                })
                .orElseThrow(()-> new CompanyNotFoundException("Company not found with ID: " + id));
    }


    //Mapeo de la entidad -> response
    private CompanyResponse mapToResponse(CompanyEntity entity) {
        return CompanyResponse.builder()
                .id(entity.getId())
                .nameCompany(entity.getNameCompany())
                .status(entity.getStatus())
                .build();
    }
}
