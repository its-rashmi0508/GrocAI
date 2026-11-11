package com.example.grocerymate.ai;

/** One line of AI feedback. */
public class Insight {
    public enum Level { GOOD, WARN, ALERT }

    public final Level level;
    public final String emoji;
    public final String message;

    public Insight(Level level, String emoji, String message) {
        this.level = level;
        this.emoji = emoji;
        this.message = message;
    }
}
