package com.momento.organizer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerSearchDTO {
    private Integer organizerId;
    private String name;
    private String introduction;
}
