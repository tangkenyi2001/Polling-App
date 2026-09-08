package com.mentimeter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mentimeter.entity.MCQResponse;

public interface MCQResponseRepository extends JpaRepository<MCQResponse,Long>{
    List <MCQResponse> findByResponsePollId(Long pollId);

    long countByResponsePollId(Long pollId);

    @Query("SELECT r.mcqOption.id AS optionId, COUNT(r) AS voteCount "
            + "FROM MCQResponse r WHERE r.response.poll.id = :pollId GROUP BY r.mcqOption.id")
    List<OptionVoteCount> countByOption(@Param("pollId") Long pollId);

    interface OptionVoteCount {
        Long getOptionId();

        long getVoteCount();
    }
}
