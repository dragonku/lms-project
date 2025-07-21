package com.lms.domain.entities;


import com.lms.domain.repositories.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Company Entity 테스트
 */
@DataJpaTest
@ActiveProfiles("test")

@DisplayName("Company Entity 테스트")
class CompanyEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    @DisplayName("Company Entity 생성 및 저장 테스트")
    void createAndSaveCompany() {
        // Given
        Company company = Company.builder()
                .businessNumber("123-45-67890")
                .name("테스트 주식회사")
                .representativeName("홍길동")
                .phone("02-1234-5678")
                .email("contact@test.com")
                .address("서울시 강남구 테스트로 123")
                .contractStatus(Company.ContractStatus.PENDING)
                .build();

        // When
        Company savedCompany = companyRepository.save(company);
        entityManager.flush();

        // Then
        assertThat(savedCompany.getId()).isNotNull();
        assertThat(savedCompany.getBusinessNumber()).isEqualTo("123-45-67890");
        assertThat(savedCompany.getName()).isEqualTo("테스트 주식회사");
        assertThat(savedCompany.getRepresentativeName()).isEqualTo("홍길동");
        assertThat(savedCompany.getContractStatus()).isEqualTo(Company.ContractStatus.PENDING);
        assertThat(savedCompany.getCreatedAt()).isNotNull();
        assertThat(savedCompany.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Company 조회 테스트 - 사업자등록번호로 조회")
    void findByBusinessNumber() {
        // Given
        Company company = Company.builder()
                .businessNumber("999-88-77777")
                .name("조회테스트 회사")
                .representativeName("김대표")
                .build();
        
        companyRepository.save(company);
        entityManager.flush();

        // When
        Company foundCompany = companyRepository.findByBusinessNumber("999-88-77777").orElse(null);

        // Then
        assertThat(foundCompany).isNotNull();
        assertThat(foundCompany.getName()).isEqualTo("조회테스트 회사");
        assertThat(foundCompany.getRepresentativeName()).isEqualTo("김대표");
    }

    @Test
    @DisplayName("Company 정보 수정 테스트")
    void updateCompany() {
        // Given
        Company company = Company.builder()
                .businessNumber("111-22-33333")
                .name("수정 전 회사")
                .representativeName("수정 전 대표")
                .build();
        
        Company savedCompany = companyRepository.save(company);
        entityManager.flush();

        // When
        savedCompany.updateBasicInfo(
                "수정 후 회사",
                "수정 후 대표",
                "02-9999-8888",
                "new@company.com",
                "서울시 서초구 새주소 456"
        );
        companyRepository.save(savedCompany);
        entityManager.flush();

        // Then
        Company updatedCompany = companyRepository.findById(savedCompany.getId()).orElse(null);
        assertThat(updatedCompany).isNotNull();
        assertThat(updatedCompany.getName()).isEqualTo("수정 후 회사");
        assertThat(updatedCompany.getRepresentativeName()).isEqualTo("수정 후 대표");
        assertThat(updatedCompany.getPhone()).isEqualTo("02-9999-8888");
        assertThat(updatedCompany.getEmail()).isEqualTo("new@company.com");
    }

    @Test
    @DisplayName("Company 계약 상태 변경 테스트")
    void changeContractStatus() {
        // Given
        Company company = Company.builder()
                .businessNumber("555-66-77777")
                .name("상태변경 테스트 회사")
                .representativeName("상태변경 대표")
                .contractStatus(Company.ContractStatus.PENDING)
                .build();
        
        Company savedCompany = companyRepository.save(company);
        entityManager.flush();

        // When
        savedCompany.changeContractStatus(Company.ContractStatus.APPROVED);
        companyRepository.save(savedCompany);
        entityManager.flush();

        // Then
        Company updatedCompany = companyRepository.findById(savedCompany.getId()).orElse(null);
        assertThat(updatedCompany).isNotNull();
        assertThat(updatedCompany.getContractStatus()).isEqualTo(Company.ContractStatus.APPROVED);
        assertThat(updatedCompany.isApproved()).isTrue();
        assertThat(updatedCompany.canEmployeesApplyCourses()).isTrue();
    }

    @Test
    @DisplayName("Company 비즈니스 메서드 테스트")
    void companyBusinessMethods() {
        // Given
        Company pendingCompany = Company.builder()
                .businessNumber("111-11-11111")
                .name("승인대기 회사")
                .representativeName("대표1")
                .contractStatus(Company.ContractStatus.PENDING)
                .build();

        Company approvedCompany = Company.builder()
                .businessNumber("222-22-22222")
                .name("승인완료 회사")
                .representativeName("대표2")
                .contractStatus(Company.ContractStatus.APPROVED)
                .build();

        // When & Then
        assertThat(pendingCompany.isApproved()).isFalse();
        assertThat(pendingCompany.canEmployeesApplyCourses()).isFalse();

        assertThat(approvedCompany.isApproved()).isTrue();
        assertThat(approvedCompany.canEmployeesApplyCourses()).isTrue();

        assertThat(pendingCompany.getEmployeeCount()).isEqualTo(0);
        assertThat(approvedCompany.getEmployeeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Company 삭제 테스트")
    void deleteCompany() {
        // Given
        Company company = Company.builder()
                .businessNumber("000-00-00000")
                .name("삭제 테스트 회사")
                .representativeName("삭제 대표")
                .build();
        
        Company savedCompany = companyRepository.save(company);
        entityManager.flush();
        Long companyId = savedCompany.getId();

        // When
        companyRepository.deleteById(companyId);
        entityManager.flush();

        // Then
        assertThat(companyRepository.findById(companyId)).isEmpty();
    }
}