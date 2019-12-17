package com.zysl.aws.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Setter
@Getter
public class BucketResponse implements Serializable {

    private static final long serialVersionUID = 1359667460045101939L;

    private String name;
    private Instant creationDate;

}
