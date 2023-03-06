package database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Clasă ce implementează metode de filtrare a unor liste.
 * Clasa a conținut mai multe metode, dar în final am
 * păstrat doar metoda filterActorList(), utilizând în
 * rest filtrări pe stream-uri.
 */
public final class FilterMethods {

    private FilterMethods() {
    }

    /**
     * Metoda filtrează o colecție de actori după criteriul dacă aceștia
     * conțin în descriere toate cuvintele din lista primită ca parametru.
     * @param actorList Colecția de actori ce va fi filtrată.
     * @param wordList Lista de cuvinte ce sunt căutate în descrierea actorilor.
     * @return Listă de actori filtrată.
     */
    public static List<Actor> filterActorList(final Collection<Actor> actorList,
                                              final List<String> wordList) {
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
