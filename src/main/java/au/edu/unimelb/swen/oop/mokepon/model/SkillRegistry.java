package au.edu.unimelb.swen.oop.mokepon.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides predefined skills referenced by name from the data file.
 */
public final class SkillRegistry {
  private static final Map<String, Skill> SKILLS = new HashMap<>();

  static {
    register(new BiteSkill());
    register(new TackleSkill());
    register(new BunkerSkill());
    register(new WaitSkill());
    register(new AimSkill());
    register(new SwoleSkill());
    register(new NimbleSkill());
    register(new LanceSkill());
    register(new StunSkill());
    register(new PocketSandSkill());
  }

  private SkillRegistry() {
  }

  private static void register(Skill skill) {
    SKILLS.put(skill.getName(), skill);
  }

  public static Skill get(String name) {
    Skill skill = SKILLS.get(name);
    if (skill == null) {
      throw new IllegalArgumentException("Unknown skill: " + name);
    }
    return skill;
  }

  public static List<Skill> parseSkills(String pipeDelimited) {
    String[] names = pipeDelimited.split("\\|");
    return java.util.Arrays.stream(names).map(SkillRegistry::get).toList();
  }
}
