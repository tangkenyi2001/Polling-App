package com.mentimeter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mentimeter.entity.WordCloudResponse;

public interface WordCloudResponseRepository extends JpaRepository<WordCloudResponse,Long>{
    List <WordCloudResponse> findByResponsePollId(Long pollId);

    long countByResponsePollId(Long pollId);

    @Query("SELECT LOWER(TRIM(r.text)) AS word, COUNT(r) AS wordCount "
            + "FROM WordCloudResponse r WHERE r.response.poll.id = :pollId AND TRIM(r.text) <> '' "
            + "GROUP BY LOWER(TRIM(r.text))")
    List<WordCount> countByWord(@Param("pollId") Long pollId);

    interface WordCount {
        String getWord();

        long getWordCount();
    }
}
