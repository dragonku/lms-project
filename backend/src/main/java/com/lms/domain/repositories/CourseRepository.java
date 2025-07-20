package com.lms.domain.repositories;

import com.lms.domain.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 교육 과정 Repository 인터페이스
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * 과정 상태별 조회
     */
    List<Course> findByStatus(Course.CourseStatus status);

    /**
     * 과정 유형별 조회
     */
    List<Course> findByCourseType(Course.CourseType courseType);

    /**
     * 활성 과정 조회
     */
    @Query("SELECT c FROM Course c WHERE c.status = 'ACTIVE'")
    List<Course> findActiveCourses();

    /**
     * 재직자 신청 가능한 과정 조회
     */
    @Query("SELECT c FROM Course c WHERE c.status = 'ACTIVE' AND (c.courseType = 'EMPLOYEE' OR c.courseType = 'COMMON')")
    List<Course> findCoursesForEmployees();

    /**
     * 구직자 신청 가능한 과정 조회
     */
    @Query("SELECT c FROM Course c WHERE c.status = 'ACTIVE' AND (c.courseType = 'JOB_SEEKER' OR c.courseType = 'COMMON')")
    List<Course> findCoursesForJobSeekers();

    /**
     * 과정명으로 검색 (부분 일치)
     */
    List<Course> findByTitleContainingIgnoreCase(String title);

    /**
     * 교육시간 범위로 조회
     */
    List<Course> findByDurationHoursBetween(Integer minHours, Integer maxHours);

    /**
     * 과정명과 설명에서 키워드 검색
     */
    @Query("SELECT c FROM Course c WHERE " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Course> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 과정 유형과 상태로 조회
     */
    List<Course> findByCourseTypeAndStatus(Course.CourseType courseType, Course.CourseStatus status);

    /**
     * 최대 참여인원이 설정된 활성 과정 조회
     */
    @Query("SELECT c FROM Course c WHERE c.status = 'ACTIVE' AND c.maxParticipants IS NOT NULL")
    List<Course> findActiveCoursesWithMaxParticipants();

    /**
     * 과정 유형별 개수 조회
     */
    @Query("SELECT c.courseType, COUNT(c) FROM Course c WHERE c.status = 'ACTIVE' GROUP BY c.courseType")
    List<Object[]> countActiveCoursesByType();
}