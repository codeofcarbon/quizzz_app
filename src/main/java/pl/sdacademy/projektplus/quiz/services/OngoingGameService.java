package pl.sdacademy.projektplus.quiz.services;

import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import pl.sdacademy.projektplus.quiz.dto.CategoriesDto;
import pl.sdacademy.projektplus.quiz.dto.QuestionsDto;
import pl.sdacademy.projektplus.quiz.frontend.Difficulty;
import pl.sdacademy.projektplus.quiz.frontend.GameOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Log
@SessionScope
public class OngoingGameService {
    private GameOptions gameOptions;
    private int currentQuestionIndex;
    private List<QuestionsDto.QuestionDto> questions;
    private final QuizDataService quizDataService;
    @Getter
    private int points;

    @Autowired
    public OngoingGameService(QuizDataService quizDataService) {
        this.quizDataService = quizDataService;
    }

    public void init(GameOptions gameOptions) {
        this.gameOptions = gameOptions;
        this.currentQuestionIndex = 0;
        this.points = 0;
        this.questions = quizDataService.getQuizQuestions(gameOptions);
    }

    public int getCurrentQuestionNumber() {
        return currentQuestionIndex+1;
    }

    public int getTotalQuestionNumber() {
        return questions.size();
    }

    public String getCurrentQuestion() {
        return questions.get(currentQuestionIndex).getQuestion();
    }

    public List<String> getCurrentQuestionAnswersInRandomOrder() {
        QuestionsDto.QuestionDto dto = questions.get(currentQuestionIndex);

        List<String> answers = new ArrayList<>();
        answers.add(dto.getCorrectAnswer());
        answers.addAll(dto.getIncorrectAnswers());

        Collections.shuffle(answers);
        return answers;
    }

    public void checkAnswerForCurrentQuestionAndUpdatePoints(String userAnswer) {
        if (questions.get(currentQuestionIndex).getCorrectAnswer().equals(userAnswer))
            points++;
    }

    public boolean proceedToNextQuestion() {
        currentQuestionIndex++;
        return currentQuestionIndex < questions.size();
    }

    public Difficulty getDifficulty() {
        return gameOptions.getDifficulty();
    }

    public String getCategoryName() {
        Optional<String> category = quizDataService.getQuizCategories().stream()
                .filter(categoryDto -> categoryDto.getId() == gameOptions.getCategoryId())
                .map(CategoriesDto.CategoryDto::getName)
                .findAny();
        return category.orElse(null);
    }
}