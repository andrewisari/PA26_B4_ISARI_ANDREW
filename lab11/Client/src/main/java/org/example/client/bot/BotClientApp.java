package org.example.client.bot;

import lombok.extern.java.Log;

@Log
public class BotClientApp {

    private static final String USAGE =
            "Usage: <type> <name> [host] [port] [games]\n" +
            "  type: random | kb | llm-easy | llm-medium | llm-hard\n" +
            "  games: number of consecutive games (default 1)";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println(USAGE);
            System.exit(2);
        }
        String type = args[0];
        String name = args[1];
        String host = args.length > 2 ? args[2] : "localhost";
        int port    = args.length > 3 ? Integer.parseInt(args[3]) : 5000;
        int games   = args.length > 4 ? Integer.parseInt(args[4]) : 1;

        BotStrategy strategy = create(type);
        BotClient client = new BotClient(name, strategy);
        log.info("Bot '" + name + "' (" + strategy.label() + ") -> " + host + ":" + port
                + " for " + games + " game(s)");

        for (int i = 1; i <= games; i++) {
            try {
                BotClient.Result r = client.play(host, port);
                System.out.println("Game " + i + ": " + r + " (strategy=" + strategy.label() + ")");
            } catch (Exception e) {
                System.err.println("Game " + i + " failed: " + e.getMessage());
            }
        }
    }

    private static BotStrategy create(String type) {
        return switch (type.toLowerCase()) {
            case "random"     -> new RandomBot();
            case "kb"         -> new KnowledgeBaseBot();
            case "llm-easy"   -> new LLMBot(LLMBot.Difficulty.EASY);
            case "llm-medium" -> new LLMBot(LLMBot.Difficulty.MEDIUM);
            case "llm-hard"   -> new LLMBot(LLMBot.Difficulty.HARD);
            default -> throw new IllegalArgumentException("Unknown bot type: " + type + "\n" + USAGE);
        };
    }
}
