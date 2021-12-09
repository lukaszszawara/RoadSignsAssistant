package com.extend.roadsignassistant;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The most of those information can be found in GTSRB_TensorFlow_MobileNet.ipynb
 */
public class GtsrbModelConfig {
    public static String MODEL_FILENAME = "gtsrb_model.lite";

    public static final int INPUT_IMG_SIZE_WIDTH = 224;
    public static final int INPUT_IMG_SIZE_HEIGHT = 224;
    public static final int FLOAT_TYPE_SIZE = 4;
    public static final int PIXEL_SIZE = 3;
    public static final int MODEL_INPUT_SIZE = FLOAT_TYPE_SIZE * INPUT_IMG_SIZE_WIDTH * INPUT_IMG_SIZE_HEIGHT * PIXEL_SIZE;
    public static final int IMAGE_MEAN = 0;
    public static final float IMAGE_STD = 255.0f;

    //This list can be taken from notebooks/output/labels_readable.txt 
    public static final List<String> OUTPUT_LABELS = Collections.unmodifiableList(
            Arrays.asList(
                    "Ograniczenie prędkości do dwudziestu kilometrów na godzinę",
                    "Ograniczenie prędkości do trzydziestu kilometrów na godzinę",
                    "Ograniczenie prędkości do piędziesięciu kilometrów na godzinę",
                    "Ograniczenie prędkości do sześćdziesięciu kilometrów na godzinę",
                    "Ograniczenie prędkości do siedemdziesięciu kilometrów na godzinę",
                    "Ograniczenie prędkości do osiemdziesięciu kilometrów na godzinę",
                    "Odwołanie ograniczenia prędkości do osiemdziesięciu kilometrów na godzinę",
                    "Ograniczenie prędkości do stu kilometrów na godzinę",
                    "Ograniczenie prędkości do stu dwudziestu kilometrów na godzinę",
                    "Zakaz wyprzedzania",
                    "Zakaz wyprzedzania dla ciężarówek",
                    "Droga z pierwszeństwem przejazdu",
                    "Pierwszeństwo przejazdu",
                    "Ustąp pierszeństwa",
                    "Stop",
                    "Zakaz wjadu",
                    "Zakaz wjazdu dla ciężarówek",
                    "Zakaz wjazdu droga jednokierunkowa",
                    "Zachowaj szczególną ostrożność",
                    "Uwaga zakręt w lewo",
                    "Uwaga zakręt w prawo",
                    "Ostre zakręty",
                    "Wyboje",
                    "Śliska nawierzchnia",
                    "Zwężenie drogi",
                    "Wyjazd z budowy",
                    "Światła",
                    "Przechodnie",
                    "Dzieci na drodze",
                    "Droga dla rowerów",
                    "Możliwa śliska nawierzchnia",
                    "Uwaga na zwierzęta",
                    "Odwołanie ograniczeń prędkości",
                    "Nakaz jazdy w prawo",
                    "Nakaz jazdy w lewo",
                    "turn_straight",
                    "turn_straight_right",
                    "turn_straight_left",
                    "turn_right_down",
                    "turn_left_down",
                    "Droga z ruchem okrężnym",
                    "Koniec zakazu wyprzedzania",
                    "Koniec zakazu wyprzedzania dla ciężarówek"
            ));

    public static final int MAX_CLASSIFICATION_RESULTS = 5;
    public static final float CLASSIFICATION_THRESHOLD = 0.2f;
}
