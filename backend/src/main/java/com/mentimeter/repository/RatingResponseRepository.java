package com.mentimeter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mentimeter.entity.RatingResponse;

public interface RatingResponseRepository extends JpaRepository<RatingResponse,Long>{
    List <RatingResponse> findByResponsePollId(Long pollId);

    long countByResponsePollId(Long pollId);

    @Query("SELECT r.rating AS rating, COUNT(r) AS ratingCount "
            + "FROM RatingResponse r WHERE r.response.poll.id = :pollId GROUP BY r.rating ORDER BY r.rating")
    List<RatingCount> countByRating(@Param("pollId") Long pollId);

    interface RatingCount {
        Integer getRating();

        long getRatingCount();
    }
}
