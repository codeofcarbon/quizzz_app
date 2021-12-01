package pl.sdacademy.projektplus.quiz;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.sdacademy.projektplus.quiz.database.entities.PlayerEntity;
import pl.sdacademy.projektplus.quiz.database.repositories.PlayerRepository;
import pl.sdacademy.projektplus.quiz.services.QuizDataService;

import java.util.List;

@Component
@Log
public class StartupRunner implements CommandLineRunner {
    private final PlayerRepository playerRepository;
    private final QuizDataService quizDataService;

    @Autowired
    public StartupRunner(PlayerRepository playerRepository,
                         QuizDataService quizDataService) {
        this.playerRepository = playerRepository;
        this.quizDataService = quizDataService;
    }

    @Override
    public void run(String...args) {
        log.info("Executing startup actions...");
        playerRepository.save(new PlayerEntity("John"));
        playerRepository.save(new PlayerEntity("Harry"));
        playerRepository.save(new PlayerEntity("George"));

        log.info("List of players from database:");
        List<PlayerEntity> playersFromDatabase = playerRepository.findAll();
        for (PlayerEntity player : playersFromDatabase) {
            log.info("Retrieved player: " + player);
        }

        quizDataService.getQuizCategories();
//        quizDataService.getQuizQuestions();
    }
}