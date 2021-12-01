package pl.sdacademy.projektplus.quiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuizApplication {
	public static void main(String[] args) {
		SpringApplication.run(QuizApplication.class, args);
	}
}

/*
TODO ================================================ Error: Error handling from API ===============================
    ----->>>>>
    We should handle the situation where we fail to get the questions from the Open Trivia DB API -
    due to a connection error or a failure on their side.
    If a reply is received, check the error codes and HTTP response codes there.
    It is a good idea to display an appropriate error message to the user.

TODO ================================================ Improvement: Cache for QuizDataService =======================
    ----->>>>>
    https://github.com/google/guava/wiki/CachesExplained
    Prepare a mechanism for storing tasks downloaded from Open Trivia DB - to avoid the necessity
    repeatedly polling the website. You can use a simple Map, more sophisticated
    cache from the Guava library, or a database.

TODO ================ Improvement: Separation of the common part of the pages, correction of the appearance ========
    ----->>>>>
    The appearance of our website is far from perfect, and so is its code.
    We copied large blocks of HTML code many times. By using Thymeleaf, we can avoid this.
    Familiarize yourself with Thymeleaf's layouts and fragments (you can start with this article)
        https://www.thymeleaf.org/doc/articles/layouts.html
    and then apply them in your project to significantly reduce HTML duplication as well as to organize it.
    In the next step - transform the layout of the website according to your preferences, so that it is more
    visually attractive.

TODO ================================================ Improvement: Saving the best scores of players ===============
    ----->>>>>
    Add to the application a mechanism for recording the best results of players in individual categories.
    After the game is over, on the result display screen - ask the user for their name,
    if his result qualifies for TOP3 in a given category.
    Store the best results in a database so that they are available between application starts.
    Allow you to view the all-time highscore table from your home page.

TODO ================================================ Further development ==========================================
    ----->>>>>
    The project has great potential for further expansion. Examples of things that can be enriched with it are:
    -> Time limits in answering
    -> "Millionaires" module enriching the game with lifebuoys and raising money
    -> Administration module - adding / removing / editing tasks on your own
    -> Picture questions
    -> Multiplayer game simultaneously (via Internet)
*/