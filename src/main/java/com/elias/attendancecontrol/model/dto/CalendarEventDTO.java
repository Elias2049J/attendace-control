package com.elias.attendancecontrol.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarEventDTO {
    private String title;
    private String start;
    private String end;
    private String description;
    private String color;
}
