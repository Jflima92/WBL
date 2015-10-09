package jUnit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Auxiliar {
	public static void deleteTestFile() {
		Path fileToDeletePath = Paths.get("users_test.xml");
		try {
			Files.delete(fileToDeletePath);
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
