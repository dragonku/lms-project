package com.lms.domain.repositories;

import com.lms.domain.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 협약사 Repository 인터페이스
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * 사업자등록번호로 협약사 조회
     */
    Optional<Company> findByBusinessNumber(String businessNumber);

    /**
     * 사업자등록번호 존재 여부 확인
     */
    boolean existsByBusinessNumber(String businessNumber);

    /**
     * 회사명으로 검색 (부분 일치)
     */
    List<Company> findByNameContainingIgnoreCase(String name);

    /**
     * 계약 상태별 조회
     */
    List<Company> findByContractStatus(Company.ContractStatus contractStatus);

    /**
     * 승인된 협약사 조회
     */
    @Query("SELECT c FROM Company c WHERE c.contractStatus = 'APPROVED'")
    List<Company> findApprovedCompanies();

    /**
     * 승인 대기 중인 협약사 조회
     */
    @Query("SELECT c FROM Company c WHERE c.contractStatus = 'PENDING' ORDER BY c.createdAt ASC")
    List<Company> findPendingCompanies();

    /**
     * 직원 수가 있는 활성 협약사 조회
     */
    @Query("SELECT c FROM Company c WHERE c.contractStatus = 'APPROVED' AND SIZE(c.employees) > 0")
    List<Company> findActiveCompaniesWithEmployees();

    /**
     * 회사명과 대표자명으로 검색
     */
    @Query("SELECT c FROM Company c WHERE " +
           "(:companyName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :companyName, '%'))) AND " +
           "(:representative IS NULL OR LOWER(c.representativeName) LIKE LOWER(CONCAT('%', :representative, '%')))")
    List<Company> searchByNameAndRepresentative(@Param("companyName") String companyName, 
                                               @Param("representative") String representative);

    /**
     * 계약 상태별 개수 조회
     */
    @Query("SELECT c.contractStatus, COUNT(c) FROM Company c GROUP BY c.contractStatus")
    List<Object[]> countByContractStatus();
}