package com.mentimeter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mentimeter.entity.Poll;
import java.util.List;

public interface PollRepository extends JpaRepository<Poll,Long>{
    List<Poll> findByUserId(long id);
}
