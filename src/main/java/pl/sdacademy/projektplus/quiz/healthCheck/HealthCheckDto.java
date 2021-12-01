package pl.sdacademy.projektplus.quiz.healthCheck;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HealthCheckDto {
    private final boolean status;
    private final String message;
}