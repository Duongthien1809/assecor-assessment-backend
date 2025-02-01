package assecor.assessment.backend.model;

import assecor.assessment.backend.exception.ColorNotFoundException;

public enum Color {
    blau(1),
    gruen(2),
    violet(3),
    rot(4),
    gelb(5),
    tuerkis(6),
    weiss(7);
    private final int colorID;

    Color(int colorID) {
        this.colorID = colorID;
    }

    public static Color getColor(int colorID) {
        for (Color color : Color.values()) {
            if (color.colorID == colorID) {
                return color;
            }
        }
        throw new ColorNotFoundException("Color ID " + colorID + " not found");
    }
}
