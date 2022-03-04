package com.example.busserviceapp.DistanceMatrixApiModel;

import java.util.List;

import com.example.busserviceapp.DistanceMatrixApiModel.Element;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class Row {
    @JsonProperty("elements")
    private List<Element> elements = null;

    @JsonProperty("elements")
    public List<Element> getElements() {
        return elements;
    }
}
