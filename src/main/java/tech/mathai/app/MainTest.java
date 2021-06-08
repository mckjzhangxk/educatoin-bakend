package tech.mathai.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mathai on 21-1-19.
 */
class Person{
    Person(String name) {
        this.name = name;
    }

    public String name;
}
public class MainTest {

    public static void main(String[] args) {
        List<String> immutableList = Collections.emptyList();
        immutableList.add("test");
//        List<Person> p=new ArrayList<>();
//
//        p.add(new Person("zxk"));
//        p=Collections.unmodifiableList(p);
//
//        p.get(0).name="七家";
//        p.add(new Person("zxk"));
    }
}
