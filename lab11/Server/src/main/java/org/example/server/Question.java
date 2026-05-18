package org.example.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Question {

    private final String id;
    private final String text;
    private final List<String> options;
    private final int correctIndex;

    public boolean isCorrect(int choice) {
        return choice == correctIndex;
    }

    public String correctOptionText() {
        return options.get(correctIndex - 1);
    }
}
