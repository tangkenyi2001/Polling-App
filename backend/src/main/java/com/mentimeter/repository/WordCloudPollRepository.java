package com.mentimeter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mentimeter.entity.WordCloudPoll;

public interface WordCloudPollRepository extends JpaRepository<WordCloudPoll,Long>{

}
