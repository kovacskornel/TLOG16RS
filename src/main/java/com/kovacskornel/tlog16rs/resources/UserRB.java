package com.kovacskornel.tlog16rs.resources;

import lombok.Getter;
import lombok.Setter;
/**
 *
 * @author Kovacs Kornel
 * @since 2017.07.03
 */
@Getter
@Setter
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class UserRB {
    private String name;
    private String password;
}
