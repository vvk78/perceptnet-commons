package com.perceptnet.commons.json.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 03.11.2019
 */
public class TemplatesGroupDto extends BaseIdentifiedDto {
    private String groupName;
    private List<TemplateShortDto> items = new ArrayList<>();

    public List<TemplateShortDto> getItems() {
        return items;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
