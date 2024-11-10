package parameters;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Parameters {
    public static final long SEED = 11;
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 1000;
    public static final int TARGET_NUMBER_OF_POINTS = 1500;
    public static final float STARTING_TRIANGLE_RADIUS = 300;
    public static final float STARTING_TRIANGLE_RADIUS_GAUSSIAN_FACTOR = 10f;
    public static final float MESH_DISTANCE = 4f;
    public static final float REPULSION_RADIUS = 50f;
    public static final float U_TREE_CELL_WIDTH = 10f;
    public static final float U_TREE_CELL_HEIGHT = 10f;
    public static final float LATTICE_FORCE_MAGNITUDE_MULTIPLIER = 50;
    public static final float LATTICE_FORCE_LIMIT = 1f;
    public static final int LATTICE_MARGIN = -100;
    public static final float NOISE_SCALE = .01f;
    public static final float NOISE_MULTIPLIER = 3;
    public static final float WEIGHT_TOWARDS_CENTER = 30;
    public static final Color BACKGROUND_COLOR = new Color(15, 5, 25);
    public static final Color LATTICE_STROKE_COLOR = new Color(255, 5.1f);

    /**
     * Helper method to extract the constants in order to save them to a json file
     *
     * @return a Map of the constants (name -> value)
     */
    public static Map<String, ?> toJsonMap() throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();

        Field[] declaredFields = Parameters.class.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(Parameters.class));
        }

        return Collections.singletonMap(Parameters.class.getSimpleName(), map);
    }

    public record Color(float red, float green, float blue, float alpha) {
        public Color(float red, float green, float blue) {
            this(red, green, blue, 255);
        }

        public Color(float grayscale, float alpha) {
            this(grayscale, grayscale, grayscale, alpha);
        }

        public Color(float grayscale) {
            this(grayscale, 255);
        }

        public Color(String hexCode) {
            this(decode(hexCode));
        }

        public Color(Color color) {
            this(color.red, color.green, color.blue, color.alpha);
        }

        public static Color decode(String hexCode) {
            return switch (hexCode.length()) {
                case 2 -> new Color(Integer.valueOf(hexCode, 16));
                case 4 -> new Color(Integer.valueOf(hexCode.substring(0, 2), 16),
                        Integer.valueOf(hexCode.substring(2, 4), 16));
                case 6 -> new Color(Integer.valueOf(hexCode.substring(0, 2), 16),
                        Integer.valueOf(hexCode.substring(2, 4), 16),
                        Integer.valueOf(hexCode.substring(4, 6), 16));
                case 8 -> new Color(Integer.valueOf(hexCode.substring(0, 2), 16),
                        Integer.valueOf(hexCode.substring(2, 4), 16),
                        Integer.valueOf(hexCode.substring(4, 6), 16),
                        Integer.valueOf(hexCode.substring(6, 8), 16));
                default -> throw new IllegalArgumentException();
            };
        }
    }
}
