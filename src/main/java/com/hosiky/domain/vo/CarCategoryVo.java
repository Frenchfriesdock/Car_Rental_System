package com.hosiky.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CarCategoryVo implements Serializable {

    private String id;
    private String name;
    private String remark;
}
