package com.tunely.repository;

import com.tunely.domain.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByCourseIdAndUserId(Long courseId, String userId);

    int countByCourseId(Long courseId);

    List<Enrollment> findByUserId(String userId);
}
