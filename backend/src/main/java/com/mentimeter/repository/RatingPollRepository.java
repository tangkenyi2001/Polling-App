package com.mentimeter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mentimeter.entity.RatingPoll;

public interface RatingPollRepository extends JpaRepository<RatingPoll,Long>{

}
