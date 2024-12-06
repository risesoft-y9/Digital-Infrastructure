package net.risesoft.model;

import java.util.List;

import lombok.Data;

/**
 * @author qinman
 */
@Data
public class ResultObject extends Result {
    private List<String> data;
}
