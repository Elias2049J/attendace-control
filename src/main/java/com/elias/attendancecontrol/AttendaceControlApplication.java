package com.elias.attendancecontrol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.util.TimeZone;
@SpringBootApplication
@EnableScheduling
public class AttendaceControlApplication {
    public static void main(String[] args) {
        String tz = System.getProperty("app.timezone",
                     System.getenv().getOrDefault("APP_TIMEZONE", "America/Lima"));
        TimeZone.setDefault(TimeZone.getTimeZone(tz));
        SpringApplication.run(AttendaceControlApplication.class, args);
    }
}
