package hu.nye.progtech.bl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class hiding the complexity of loading file content into Strings.
 */
public final class FileHelper {

    private static final Logger logger = LoggerFactory.getLogger(FileHelper.class);

    private FileHelper() {
        //
    }

    /**
     * Read the contents of the specified resource (in classpath) into a list of String. Line separators are removed.
     * If loading hits an error the method returns empty list.
     *
     * @param resourcePath - path inside classpath
     * @return List of String, never null.
     */
    public static List<String> readLines(String resourcePath) {
        List<String> ret = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)))) {
            String line = reader.readLine();
            while (line != null) {
                ret.add(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            logger.warn("Failed to load contents from file!", e);
        }

        return ret;
    }

}
