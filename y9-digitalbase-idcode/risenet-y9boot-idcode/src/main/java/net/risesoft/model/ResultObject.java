package net.risesoft.model;

import lombok.Data;

import java.util.List;

@Data
public class ResultObject extends Result {
    private List<String> data;
}
