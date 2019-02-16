package com.github.lbpmodding.lbpproxy.data;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "identifier")
public class Dependency {
    private String identifier;
}
