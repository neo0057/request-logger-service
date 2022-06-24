package com.smaato.interview.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.Objects;

@RedisHash("requests")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Request implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private Integer requestId;
    private Integer hour;
    private Integer minute;

    public Request(Integer requestId, int hour, int minute) {
        this.requestId = requestId;
        this.hour = hour;
        this.minute = minute;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Request otherRequest)) return false;
        return Objects.equals(this.requestId, otherRequest.requestId) && Objects.equals(this.getHour(), otherRequest.getHour())
                && Objects.equals(this.getMinute(), otherRequest.getMinute());
    }
}
