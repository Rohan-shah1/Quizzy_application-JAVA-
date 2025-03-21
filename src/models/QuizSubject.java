package models;

import java.util.ArrayList;
import java.util.List;

public class QuizSubject {
    private List<QuizObserver> observers = new ArrayList<>();
    private int score = 0;

    public void addObserver(QuizObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(QuizObserver observer) {
        observers.remove(observer);
    }

    public void setScore(int newScore) {
        this.score = newScore;
        notifyObservers();
    }

    public void notifyObservers() {
        for (QuizObserver observer : observers) {
            observer.updateScore(score);
        }
    }

    public int getScore() {
        return score;
    }
}
