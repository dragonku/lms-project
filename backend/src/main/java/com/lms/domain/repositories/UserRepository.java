package com.lms.domain.repositories;

import com.lms.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 Repository 인터페이스
 * 도메인 계층의 Repository 정의
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자명으로 사용자 조회
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 사용자 조회
     */
    Optional<User> findByEmail(String email);

    /**
     * 사용자명 존재 여부 확인
     */
    boolean existsByUsername(String username);

    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);

    /**
     * 사용자 유형별 조회
     */
    List<User> findByUserType(User.UserType userType);

    /**
     * 사용자 상태별 조회
     */
    List<User> findByStatus(User.Status status);

    /**
     * 특정 회사 소속 직원 조회
     */
    List<User> findByCompanyId(Long companyId);

    /**
     * 승인 대기 중인 재직자 조회
     */
    @Query("SELECT u FROM User u WHERE u.userType = :userType AND u.status = :status AND u.company IS NOT NULL")
    List<User> findPendingEmployees(@Param("userType") User.UserType userType, 
                                   @Param("status") User.Status status);

    /**
     * 활성 상태의 관리자 조회
     */
    @Query("SELECT u FROM User u WHERE u.userType = 'ADMIN' AND u.status = 'ACTIVE'")
    List<User> findActiveAdmins();

    /**
     * 활성 상태의 강사 조회
     */
    @Query("SELECT u FROM User u WHERE u.userType = 'INSTRUCTOR' AND u.status = 'ACTIVE'")
    List<User> findActiveInstructors();

    /**
     * 이름과 이메일로 사용자 검색
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    List<User> findByNameContainingIgnoreCaseAndEmailContainingIgnoreCase(
            @Param("name") String name, @Param("email") String email);
}