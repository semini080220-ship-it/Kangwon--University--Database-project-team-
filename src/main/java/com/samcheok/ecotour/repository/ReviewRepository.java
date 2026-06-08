package com.samcheok.ecotour.repository;

import com.samcheok.ecotour.domain.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByAttractionId(Long attractionId);

    long countByAttractionId(Long attractionId);

    /** 관광지 평균 평점 (리뷰 없으면 null). */
    @Query("select avg(r.rating) from Review r where r.attraction.id = :attractionId")
    Double findAverageRatingByAttractionId(@Param("attractionId") Long attractionId);
}
