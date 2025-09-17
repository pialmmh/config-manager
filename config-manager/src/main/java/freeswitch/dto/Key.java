package freeswitch.dto;

import lombok.Data;

@Data
public class Key<T> {
    private T id;
}
