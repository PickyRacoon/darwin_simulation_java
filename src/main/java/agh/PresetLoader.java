package agh;

import com.sun.jdi.event.StepEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PresetLoader {
    private static final String PRESETS_PATH = "sim_presets/sim_presets.csv";

    public List<String> readPresets() throws IOException {
        return Files.readAllLines(Paths.get(PRESETS_PATH))
                .stream()
                .skip(1)
                .map(line -> line.split(",")[0])
                .toList();
    }

    public HashMap<String, Integer> readPresetValues(String presetName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(PRESETS_PATH));
        List<String> presetKeys = new ArrayList<>(Arrays.asList(reader.readLine().split(",")));
        reader.close();
        presetKeys.removeFirst();

        List<Integer> presetValues = Files.readAllLines(Paths.get(PRESETS_PATH))
                .stream()
                .skip(1)
                .filter(line -> Objects.equals(line.split(",")[0], presetName))
                .flatMap(line -> Arrays.stream(line.split(",")).skip(1))
                .map(Integer::parseInt)
                .toList();

        HashMap<String, Integer> result = new HashMap<>();
        for (int i = 0; i < presetKeys.size(); i++) {
            result.put(presetKeys.get(i), presetValues.get(i));
        }
        return result;
    }
}
