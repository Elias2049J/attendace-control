package com.elias.attendancecontrol.service;

import java.util.Map;

public interface ExportService {
    byte[] exportReport(Map<String, Object> data, String format);
}
