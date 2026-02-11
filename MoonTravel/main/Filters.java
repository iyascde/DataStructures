package MoonTravel.main;

import java.awt.Color;

import edu.ftdev.Map.MoonMap;

public class Filters {
    // #region: [Private & Internal] Contrast fields and methods

    // The size of the area to apply the contrast filter to.
    static int CONTRAST_AREA_SIZE = 160;
    // The threshold for color distance to determine if a pixel is black or white.
    static double COLOR_DISTANCE_THRESHOLD = 60.0;

    // Applies a contrast filter to the given MoonMap.
    // Each pixels is compared to its neighbors, and if the color distance is greater than
    // a threshold, it is set to black, otherwise to white.
    static void contrast(MoonMap mm) {
        Color[][] origColors = mm.getArea(0, 0, mm.getWidth(), mm.getHeight());
        int nRows = mm.getHeight() / CONTRAST_AREA_SIZE
                    + (mm.getHeight() % CONTRAST_AREA_SIZE != 0 ? 1 : 0);
        int nCols = mm.getWidth() / CONTRAST_AREA_SIZE
                    + (mm.getWidth() % CONTRAST_AREA_SIZE != 0 ? 1 : 0);
        for (int r = 0; r < nRows; r++) {
            for (int c = 0; c < nCols; c++) {
                int areaX = (c * CONTRAST_AREA_SIZE);
                int areaY = (r * CONTRAST_AREA_SIZE);
                int areaW = Math.min(mm.getWidth() - areaX, CONTRAST_AREA_SIZE);
                int areaH = Math.min(mm.getHeight() - areaY, CONTRAST_AREA_SIZE);
                Color[][] areaColors = contrastArea(mm, areaX, areaY, areaW, areaH, origColors);
                mm.setArea(areaX, areaY, areaColors);
                mm.breakStep("(%d, %d): Contrast filter applied!", r, c);
            }
        }
    }

    // Applies a contrast filter to the given area of the MoonMap.
    private static Color[][] contrastArea(MoonMap mm, int x, int y, int width, int height, Color[][] colors) {
        Color[][] areaPixels = new Color[height][width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                double dist = maxColorDistance(mm, x + c, y + r, colors);
                areaPixels[r][c] = (dist > COLOR_DISTANCE_THRESHOLD) ? Color.BLACK : Color.WHITE;
            }
        }
        return areaPixels;
    }

     // Calculates the maximum color distance between the pixel at (x, y) and its neighbors.
    private static double maxColorDistance(MoonMap mm, int x, int y, Color[][] colors) {
        double maxDistance = Double.MIN_VALUE;
        for (int dX = -1; dX <= 1; dX++) {
            for (int dY = -1; dY <= 1; dY++) {
                if ((dX == 0 && dY == 0) 
                    || (x + dX < 0 || x + dX >= mm.getWidth())
                    || (y + dY < 0 || y + dY >= mm.getHeight())) {
                    continue;
                }
                maxDistance = Math.max(maxDistance, colorDistance(colors[y][x], colors[y + dY][x + dX]));
            }
        }
        return maxDistance;
    }

    // Calculates the color distance between two colors.
    private static double colorDistance(Color col1, Color col2) {
        return Math.sqrt(
            (Math.pow((col2.getRed() - col1.getRed()), 2))
            + (Math.pow((col2.getGreen() - col1.getGreen()), 2))
            + (Math.pow((col2.getBlue() - col1.getBlue()), 2)));
    }
    // #endregion: [Private & Internal] Contrast fields and methods

    // #region: [Private & Internal] Pixelate fields and methods
    // The size of the area to apply the pixelate filter to.
    static int PIXELATE_AREA_SIZE = 6; // 10;
    // The percentage of dark pixels within the area to determine if the area is dark or light.
    static double PIXELATE_DARK_PCT = 40.0; // 22.0;

    // Applies a pixelate filter to the given MoonMap.
    // Each area is checked for the percentage of dark pixels, and if it is greater than a threshold,
    // the area is set to dark, otherwise it is set to light.
    // Returns a boolean map of blocked (true) or open (false) areas on the map, each area a square of PIXELATE_AREA_SIZE side.
    static boolean[][] pixelate(MoonMap mm) {
        int nRows = mm.getHeight() / PIXELATE_AREA_SIZE;
        int nCols = mm.getWidth() / PIXELATE_AREA_SIZE;
        boolean[][] terrain = new boolean[nRows][nCols];
        for (int r = 0; r < nRows; r++) {
            for (int c = 0; c < nCols; c++) {
                int areaX = (c * PIXELATE_AREA_SIZE);
                int areaY = (r * PIXELATE_AREA_SIZE);
                Color[][] areaColors = mm.getArea(areaX, areaY, PIXELATE_AREA_SIZE, PIXELATE_AREA_SIZE);
                terrain[r][c] = pixelateArea(areaColors);
                mm.setArea(areaX, areaY, areaColors);
                mm.breakStep("(%d, %d): Pixelation complete!", r, c);
            }
        }
        return terrain;
    }

    // Applies the pixelate filter to the given area of the MoonMap.
    private static boolean pixelateArea(Color[][] areaColors) {
        int darkCount = 0;
        int height = areaColors.length;
        int width = areaColors[0].length;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (areaColors[r][c].equals(Color.BLACK)) {
                    darkCount++;
                }
            }
        }
        boolean isBlocked = (darkCount >= (PIXELATE_DARK_PCT * (height * width) / 100));
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                areaColors[r][c] = isBlocked
                    ? (areaColors[r][c].equals(Color.BLACK) ? Color.GRAY : Color.LIGHT_GRAY)
                    : Color.WHITE;
            }
        }
        return isBlocked;
    }
    // #endregion: [Private & Internal] Pixelate fields and methods
}