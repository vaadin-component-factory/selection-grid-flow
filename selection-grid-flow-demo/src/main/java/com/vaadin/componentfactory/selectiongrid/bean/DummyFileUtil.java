package com.vaadin.componentfactory.selectiongrid.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author jcgueriaud
 */
public class DummyFileUtil {

    public static String iconFolder = "vaadin:folder-open-o";
    public static String iconFile = "vaadin:file-o";

    public static Person generateRandom(int id) {
        Person person = new Person();
        person.setId(id);
        person.setFirstName(generateRandomString());
        person.setLastName(generateRandomString());
        person.setAge(generateRandomInt(99));
        return person;
    }

    private static String generateRandomString(){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }


    private static int generateRandomInt(int bound){
        Random random = new Random();
        return random.nextInt(bound);
    }
}
