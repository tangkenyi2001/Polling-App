package com.mentimeter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mentimeter.entity.MCQOption;

public interface MCQOptionRepository extends JpaRepository<MCQOption,Long>{
    List<MCQOption> findByPollId(Long pollId);
}
