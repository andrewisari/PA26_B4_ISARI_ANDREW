package org.example.server.persistence;

import org.example.server.persistence.entity.GameEntity;
import org.example.server.persistence.entity.PlayerEntity;
import org.example.server.persistence.entity.QuestionEntity;
import org.example.server.persistence.entity.ResultEntity;
import org.example.server.persistence.repository.GameRepository;
import org.example.server.persistence.repository.PlayerRepository;
import org.example.server.persistence.repository.QuestionRepository;
import org.example.server.persistence.repository.ResultRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersistenceIntegrationTest {

    private final PlayerRepository playerRepo = new PlayerRepository();
    private final QuestionRepository questionRepo = new QuestionRepository();
    private final GameRepository gameRepo = new GameRepository();
    private final ResultRepository resultRepo = new ResultRepository();

    @BeforeAll
    void setUp() {
        PersistenceManager.init(PersistenceManager.TEST_PU);
    }

    @AfterAll
    void tearDown() {
        PersistenceManager.close();
    }

    @Test
    void persistsAndFindsPlayer() {
        PlayerEntity alice = playerRepo.findOrCreate("alice");
        assertNotNull(alice.getId(), "ID should be generated after persist");
        Optional<PlayerEntity> found = playerRepo.findByName("alice");
        assertTrue(found.isPresent());
        assertEquals(alice.getId(), found.get().getId());

        PlayerEntity sameAgain = playerRepo.findOrCreate("alice");
        assertEquals(alice.getId(), sameAgain.getId(),
                "findOrCreate must return the same row for an existing name");
    }

    @Test
    void persistsAndFindsQuestion() {
        QuestionEntity q = new QuestionEntity("T01", "2 + 2 = ?", "3", "4", "5", "22", 2);
        questionRepo.save(q);
        Optional<QuestionEntity> fetched = questionRepo.findByExternalId("T01");
        assertTrue(fetched.isPresent());
        assertEquals("2 + 2 = ?", fetched.get().getText());
        assertEquals(2, fetched.get().getCorrectIndex());
        assertEquals(List.of("3", "4", "5", "22"), fetched.get().options());
    }

    @Test
    void bulkInsertIfMissingIsIdempotent() {
        QuestionEntity a = new QuestionEntity("B01", "Q?", "A", "B", "C", "D", 1);
        QuestionEntity b = new QuestionEntity("B02", "R?", "A", "B", "C", "D", 2);
        int firstRun  = questionRepo.bulkInsertIfMissing(List.of(a, b));
        int secondRun = questionRepo.bulkInsertIfMissing(List.of(
                new QuestionEntity("B01", "Q?", "A", "B", "C", "D", 1),
                new QuestionEntity("B02", "R?", "A", "B", "C", "D", 2)));
        assertEquals(2, firstRun);
        assertEquals(0, secondRun);
    }

    @Test
    void recordsCompleteGameWithResults() {
        PlayerEntity bob   = playerRepo.findOrCreate("bob");
        PlayerEntity carol = playerRepo.findOrCreate("carol");

        GameRecorder recorder = new GameRecorder(playerRepo, gameRepo, resultRepo);
        GameEntity game = recorder.startGame(bob, carol, 5);
        assertNotNull(game.getId());
        assertNotNull(game.getStartedAt());

        recorder.recordResult(game,
                bob,   4, 8_000L,
                carol, 3, 9_000L,
                GameEntity.Outcome.P1_WIN);

        Optional<GameEntity> reloaded = gameRepo.findById(game.getId());
        assertTrue(reloaded.isPresent());
        assertEquals(GameEntity.Outcome.P1_WIN, reloaded.get().getOutcome());
        assertNotNull(reloaded.get().getEndedAt());

        List<ResultEntity> results = resultRepo.findByGame(game.getId());
        assertEquals(2, results.size());
        ResultEntity bobResult   = results.stream().filter(r -> r.getPlayer().getName().equals("bob")).findFirst().orElseThrow();
        ResultEntity carolResult = results.stream().filter(r -> r.getPlayer().getName().equals("carol")).findFirst().orElseThrow();
        assertTrue(bobResult.isWinner());
        assertFalse(carolResult.isWinner());
        assertEquals(4, bobResult.getCorrectCount());
        assertEquals(3, carolResult.getCorrectCount());
    }

    @Test
    void updatesAggregatePlayerStatsAfterGame() {
        PlayerEntity dave  = playerRepo.findOrCreate("dave");
        PlayerEntity erin  = playerRepo.findOrCreate("erin");

        int daveStartGames = dave.getGamesPlayed();
        int daveStartWins  = dave.getWins();
        int erinStartLoss  = erin.getLosses();

        GameRecorder recorder = new GameRecorder(playerRepo, gameRepo, resultRepo);
        GameEntity game = recorder.startGame(dave, erin, 3);
        recorder.recordResult(game,
                dave, 3, 5_000L,
                erin, 1, 6_000L,
                GameEntity.Outcome.P1_WIN);

        PlayerEntity daveAfter = playerRepo.findById(dave.getId()).orElseThrow();
        PlayerEntity erinAfter = playerRepo.findById(erin.getId()).orElseThrow();
        assertEquals(daveStartGames + 1, daveAfter.getGamesPlayed());
        assertEquals(daveStartWins + 1, daveAfter.getWins());
        assertEquals(erinStartLoss + 1, erinAfter.getLosses());
    }

    @Test
    void seedsQuestionsFromClasspathResource() throws Exception {
        long before = questionRepo.count();
        QuestionLoader.seedFromClasspath("/questions.txt", questionRepo);
        long after = questionRepo.count();
        assertTrue(after > before, "Seeding should add questions from the classpath file");
    }
}
