package com.mentimeter.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RatingPoll {
    @Id
    private Long pollId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name="poll_id",nullable = false)
    private Poll poll;

    private Integer maxRating;

    private Integer minRating;
}
