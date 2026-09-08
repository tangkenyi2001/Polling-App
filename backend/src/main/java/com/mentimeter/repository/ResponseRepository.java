package com.mentimeter.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mentimeter.entity.Response;

public interface ResponseRepository extends JpaRepository<Response, Long> {
    List<Response> findByPollId(Long pollId);
}