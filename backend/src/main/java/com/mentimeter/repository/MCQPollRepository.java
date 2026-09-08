package com.mentimeter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mentimeter.entity.MCQPoll;

public interface MCQPollRepository extends JpaRepository<MCQPoll,Long>{
    
    
}
