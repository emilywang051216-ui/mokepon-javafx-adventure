package au.edu.unimelb.swen.oop.mokepon.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Loads Mokepon definitions from a Java properties file.
 */
public final class MokeponLoader {
  private MokeponLoader() {
  }

  public static List<Mokepon> load(Properties properties) {
    List<Mokepon> mokeponList = new ArrayList<>();
    int index = 0;
    while (properties.containsKey(key(index, "name"))) {
      mokeponList.add(loadOne(properties, index));
      index++;
    }
    return mokeponList;
  }

  private static Mokepon loadOne(Properties properties, int index) {
    return new Mokepon(
        properties.getProperty(key(index, "name")),
        properties.getProperty(key(index, "image")),
        MokeponType.valueOf(properties.getProperty(key(index, "type"))),
        Integer.parseInt(properties.getProperty(key(index, "hp"))),
        Integer.parseInt(properties.getProperty(key(index, "energy"))),
        Integer.parseInt(properties.getProperty(key(index, "strength"))),
        Integer.parseInt(properties.getProperty(key(index, "accuracy"))),
        Integer.parseInt(properties.getProperty(key(index, "defence"))),
        Integer.parseInt(properties.getProperty(key(index, "dodge"))),
        SkillRegistry.parseSkills(properties.getProperty(key(index, "skills"))));
  }

  private static String key(int index, String field) {
    return "mokepon." + index + "." + field;
  }
}
