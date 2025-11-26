package com.campusorg.patterns;

import com.campusorg.models.Member;
import java.util.ArrayList;
import java.util.List;

public class NewsPublisher {
    private List<Member> observers = new ArrayList<>();

    public void subscribe(Member member) {
        observers.add(member);
    }

    public void unsubscribe(Member member) {
        observers.remove(member);
    }

    public void notifyAll(String news) {
        for (Member member : observers) {
            member.update(news);
        }
    }
}