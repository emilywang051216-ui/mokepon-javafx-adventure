package au.edu.unimelb.swen.oop.mokepon.model;

/**
 * A Mokepon encounter standing on one walkable tile.
 */
public record MapMarker(MarkerKind kind, int x, int y) {
}
