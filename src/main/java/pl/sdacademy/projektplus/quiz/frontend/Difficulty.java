package pl.sdacademy.projektplus.quiz.frontend;

import java.util.Collection;
import java.util.EnumSet;

public enum Difficulty {
    EASY,
    MEDIUM,
    HARD;

    public Difficulty getClosestDifficulty() {
        switch (this) {
            case EASY:
            case HARD:
                return MEDIUM;
            case MEDIUM:
                return HARD;
            default:
                return null;
        }
    }

    public static Difficulty calculateNextDifficulty(Collection<Difficulty> difficulties) {
        if (difficulties == null || difficulties.isEmpty()) {
            return null;
        }
        if (difficulties.size() == 1) {
            return difficulties.iterator().next().getClosestDifficulty();
        }

        EnumSet<Difficulty> missingDifficulties = EnumSet.complementOf(EnumSet.copyOf(difficulties));

        return missingDifficulties.isEmpty() ? null : missingDifficulties.iterator().next();
    }
}
