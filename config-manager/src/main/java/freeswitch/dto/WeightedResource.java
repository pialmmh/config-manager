package freeswitch.dto;


import lombok.Data;

@Data
public class WeightedResource <T> {
    private final T resource;
    private final float weight;


    public WeightedResource(T resource,float weight) {
        this.weight = weight;
        this.resource = resource;
    }
}

