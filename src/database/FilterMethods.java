package database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class FilterMethods {
    public static List<Actor> filterActorList(Collection<Actor> actorList, List<String> wordList) {
        List<Actor> newActorList = new ArrayList<>();
        for (Actor currentActor : actorList) {
            boolean actorHasAllWords = true;
            for (String currentWord : wordList) {
                if (!currentActor.descriptionContainsWord(currentWord)) {
                    actorHasAllWords = false;
                    break;
                }
            }
            if (actorHasAllWords) {
                newActorList.add(currentActor);
            }
        }
        return newActorList;
    }
}
