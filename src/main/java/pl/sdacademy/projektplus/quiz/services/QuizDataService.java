package pl.sdacademy.projektplus.quiz.services;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pl.sdacademy.projektplus.quiz.dto.CategoriesDto;
import pl.sdacademy.projektplus.quiz.dto.CategoryQuestionCountInfoDto;
import pl.sdacademy.projektplus.quiz.dto.QuestionsDto;
import pl.sdacademy.projektplus.quiz.frontend.Difficulty;
import pl.sdacademy.projektplus.quiz.frontend.GameOptions;

import java.net.URI;
import java.util.*;

@Service
@Log
public class QuizDataService {

    private final RestTemplate restTemplate;

    @Autowired
    public QuizDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<CategoriesDto.CategoryDto> getQuizCategories() {
        CategoriesDto result = restTemplate.getForObject("https://opentdb.com/api_category.php", CategoriesDto.class);
        log.info("Quiz categories: " + result.getCategories());
        return result.getCategories();
    }

    public List<QuestionsDto.QuestionDto> getQuizQuestions(GameOptions gameOptions) {
        var categoryQuestionCount = getCategoryQuestionCount(gameOptions.getCategoryId());
        int availableQuestionCount = categoryQuestionCount.getQuestionCountForDifficulty(gameOptions.getDifficulty());

        if (availableQuestionCount >= gameOptions.getNumberOfQuestions()) {
            return getQuizQuestions(
                    gameOptions.getNumberOfQuestions(),
                    gameOptions.getCategoryId(),
                    gameOptions.getDifficulty());
        } else {
            List<QuestionsDto.QuestionDto> questions = new ArrayList<>();
            Map<Difficulty, Integer> eachDifficultyQuestionCount =
                    calculateEachDifficultyQuestionCount(
                            gameOptions.getNumberOfQuestions(),
                            gameOptions.getDifficulty(),
                            categoryQuestionCount);

            for (var entry : eachDifficultyQuestionCount.entrySet()) {
                var originalDifficultyQuestions = getQuizQuestions(
                        entry.getValue(), gameOptions.getCategoryId(), entry.getKey());

                questions.addAll(originalDifficultyQuestions);
            }
            Collections.shuffle(questions);

            return questions;
        }
    }

    private List<QuestionsDto.QuestionDto> getQuizQuestions(int numberOfQuestions,
                                                            int categoryId,
                                                            Difficulty difficulty) {
        if (numberOfQuestions <= 0) return Collections.emptyList();

        URI uri = UriComponentsBuilder.fromHttpUrl("https://opentdb.com/api.php")
                .queryParam("amount", numberOfQuestions)
                .queryParam("category", categoryId)
                .queryParam("difficulty", difficulty.name().toLowerCase())
                .build().toUri();
        log.info("Quiz question retrieve URL: " + uri);

        QuestionsDto result = restTemplate.getForObject(uri, QuestionsDto.class);
        log.info("Quiz questions: Open Trivia DB response code = " + result.getResponseCode()
                + ". Content: " + result.getResults());

        return result.getResults();
    }

    private CategoryQuestionCountInfoDto getCategoryQuestionCount(int categoryId) {
        URI uri = UriComponentsBuilder.fromHttpUrl("https://opentdb.com/api_count.php")
                .queryParam("category", categoryId)
                .build().toUri();
        log.info("Quiz category question count retrieve URL: " + uri);

        CategoryQuestionCountInfoDto result = restTemplate
                .getForObject(uri, CategoryQuestionCountInfoDto.class);
        log.info("Quiz category question count content: " + result);

        return result;
    }

    static Map<Difficulty, Integer> calculateEachDifficultyQuestionCount(int numberOfQuestions,
                                                                         Difficulty difficulty,
                                                                         CategoryQuestionCountInfoDto categoryQuestionCount) {
        Map<Difficulty, Integer> eachDifficultyQuestionCount = new EnumMap<>(Difficulty.class);
        eachDifficultyQuestionCount.put(
                difficulty,
                Math.min(numberOfQuestions, categoryQuestionCount.getQuestionCountForDifficulty(difficulty))
        );
        int missingQuestions = numberOfQuestions - eachDifficultyQuestionCount.values().stream().mapToInt(i -> i).sum();

        while (missingQuestions > 0) {
            Difficulty closestDifficulty = Difficulty.calculateNextDifficulty(eachDifficultyQuestionCount.keySet());
            if (closestDifficulty == null) {
                log.warning("Not enough question in given category!");
                break;
            }
            eachDifficultyQuestionCount.put(
                    closestDifficulty,
                    Math.min(missingQuestions, categoryQuestionCount.getQuestionCountForDifficulty(closestDifficulty))
            );
            missingQuestions = numberOfQuestions - eachDifficultyQuestionCount.values().stream().mapToInt(i -> i).sum();
        }

        if (difficulty == Difficulty.MEDIUM) {
            Difficulty otherDifficulty = Difficulty.calculateNextDifficulty(eachDifficultyQuestionCount.keySet());
            if (otherDifficulty != null) {
                Difficulty filledDifficulty = difficulty.getClosestDifficulty();
                final int numberToTransfer = Math.min(
                        eachDifficultyQuestionCount.get(filledDifficulty) / 2,
                        categoryQuestionCount.getQuestionCountForDifficulty(otherDifficulty)
                );
                eachDifficultyQuestionCount.computeIfPresent(filledDifficulty, (d, count) -> count - numberToTransfer);
                eachDifficultyQuestionCount.put(otherDifficulty, numberToTransfer);
            } else {
// TODO =============================================================================================================
//                we have 20 questions, all from the MEDIUM category, supplemented with those from EASY and HARD.
//      ----->>>  Anyway, it's not perfect - we wanted to complete the EASY and HARD questions "halfway"
//                in that case (getting 5 of each). Meanwhile, we have 8 HARD questions and only 2 EASY.
// TODO =============================================================================================================
//                in the situation we are considering, the second presented condition (the internal one)
//                is false (otherDifficulty is null) - because we had to select 2 questions from the EASY category.
//      ----->>>  add else here and handle this situation properly (10 MEDIUM questions and 5 EASY and HARD each)
//      ----->>>  implement tests for that.
            }
        }
        return eachDifficultyQuestionCount;
    }
}