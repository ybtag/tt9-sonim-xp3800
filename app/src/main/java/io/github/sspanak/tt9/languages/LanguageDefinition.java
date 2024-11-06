package io.github.sspanak.tt9.languages;

import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import io.github.sspanak.tt9.BuildConfig;
import io.github.sspanak.tt9.util.Logger;

public class LanguageDefinition {
	private static final String languagesDir = "languages";
	private static final String definitionsDir = languagesDir + "/definitions";

	public String abcString = "";
	public String dictionaryFile = "";
	public boolean hasSpaceBetweenWords = true;
	public boolean hasUpperCase = true;
	public ArrayList<ArrayList<String>> layout = new ArrayList<>();
	public String locale = "";
	public String name = "";


	/**
	 * getAllFiles
	 * Returns a list of the paths of all language definition files in the assets folder or an empty list on error.
	 */
	public static ArrayList<String> getAllFiles(AssetManager assets) {
		ArrayList<String> files = new ArrayList<>();
		try {
			for (String file : assets.list(definitionsDir)) {
				files.add(definitionsDir + "/" + file);
			}

			Logger.d("LanguageDefinition", "Found: " + files.size() + " languages.");
		} catch (IOException | NullPointerException e) {
			Logger.e("tt9.LanguageDefinition", "Failed reading language definitions from: '" + definitionsDir + "'. " + e.getMessage());
		}

		return files;
	}


	/**
	 * fromFile
	 * Takes the path to a language definition in the assets folder and parses that file into a LanguageDefinition
	 * or throws an IOException on error.
	 */
	public static LanguageDefinition fromFile(AssetManager assetManager, String definitionFile) throws IOException {
		return parse(load(assetManager, definitionFile));
	}


	/**
	 * load
	 * Loads a language definition file from the assets folder into a String or throws an IOException on error.
	 */
	private static ArrayList<String> load(AssetManager assetManager, String definitionFile) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(definitionFile), StandardCharsets.UTF_8));
		ArrayList<String> fileContents = new ArrayList<>();
		String line;
		while ((line = reader.readLine()) != null) {
			fileContents.add(line);
		}

		return fileContents;
	}


	/**
	 * parse
	 * Converts "yaml" to a LanguageDefinition object. All properties in the YAML are considered optional,
	 * so the LanguageDefinition defaults will be used when some property is omitted.
	 *
	 * Had to write all this, because the only usable library, SnakeYAML, works fine on Android 10+,
	 * but causes crashes on older devices.
	 */
	@NonNull
	private static LanguageDefinition parse(ArrayList<String> yaml) {
		LanguageDefinition definition = new LanguageDefinition();

		definition.abcString = getPropertyFromYaml(yaml, "abcString", definition.abcString);
		definition.dictionaryFile = getPropertyFromYaml(yaml, "dictionaryFile", definition.dictionaryFile);
		definition.hasSpaceBetweenWords = getPropertyFromYaml(yaml, "hasSpaceBetweenWords", definition.hasSpaceBetweenWords);
		definition.hasUpperCase = getPropertyFromYaml(yaml, "hasUpperCase", definition.hasUpperCase);
		definition.layout = getLayoutFromYaml(yaml);
		definition.locale = getPropertyFromYaml(yaml, "locale", definition.locale);
		definition.name = getPropertyFromYaml(yaml, "name", definition.name);

		if (definition.dictionaryFile != null) {
			definition.dictionaryFile = definition.dictionaryFile.replaceFirst("\\.\\w+$", "." + BuildConfig.DICTIONARY_EXTENSION);
		}
		return definition;
	}


	/**
	 * getPropertyFromYaml
	 * Finds "property" in the "yaml" and returns its value.
	 * Optional properties are allowed. If the property is not found, "defaultValue" will be returned.
	 */
	@Nullable
	private static String getPropertyFromYaml(ArrayList<String> yaml, String property, String defaultValue) {
		for (String line : yaml) {
			line = line.replaceAll("#.+$", "").trim();
			String[] parts = line.split(":");
			if (parts.length < 2) {
				continue;
			}

			if (property.equals(parts[0].trim())) {
				return parts[1].trim();
			}
		}

		return defaultValue;
	}


	/**
	 * The boolean variant of getPropertyFromYaml. It returns true if the property is found and is:
	 * "true", "on", "yes" or "y".
	 */
	private static boolean getPropertyFromYaml(ArrayList<String> yaml, String property, boolean defaultValue) {
		String value = getPropertyFromYaml(yaml, property, null);
		if (value == null) {
			return defaultValue;
		}

		value = value.toLowerCase();
		return value.equals("true") || value.equals("on") || value.equals("yes") || value.equals("y");
	}


	/**
	 * getLayoutFromYaml
	 * Finds and extracts the keypad layout. Less than 10 keys are accepted allowed leaving the ones up to 9-key empty.
	 */
	@NonNull
	private static ArrayList<ArrayList<String>> getLayoutFromYaml(ArrayList<String> yaml) {
		ArrayList<ArrayList<String>> layout = new ArrayList<>();

		boolean inLayout = false;
		for (int i = 0; i < yaml.size(); i++) {
			if (yaml.get(i).contains("layout")) {
				inLayout = true;
				continue;
			}

			if (inLayout) {
				ArrayList<String> lineChars = getLayoutEntryFromYamlLine(yaml.get(i));
				if (lineChars != null) {
					layout.add(lineChars);
				} else {
					break;
				}
			}
		}

		return layout;
	}


	/**
	 * getLayoutEntryFromYamlLine
	 * Validates a YAML line as an array and returns the character list to be assigned to a given key (a layout entry).
	 * If the YAML line is invalid, NULL will be returned.
	 */
	@Nullable
	private static ArrayList<String> getLayoutEntryFromYamlLine(String yamlLine) {
		if (!yamlLine.contains("[") || !yamlLine.contains("]")) {
			return null;
		}

		String line = yamlLine
			.replaceAll("#.+$", "")
			.replace('-', ' ')
			.replace('[', ' ')
			.replace(']', ' ')
			.replace(" ", "");

		return new ArrayList<>(Arrays.asList(line.split(",")));
	}


	public String getDictionaryFile() {
		return languagesDir + "/dictionaries/" + dictionaryFile;
	}
}
