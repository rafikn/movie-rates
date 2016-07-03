package nz.rafikn.movierates.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nz.rafikn.movierates.model.Movie;
import nz.rafikn.movierates.model.RawSearch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by rafik on 3/07/16.
 */
public class IOUtil {

    private static final Log logger = LogFactory.getLog(IOUtil.class);

    private static final Gson gson = new GsonBuilder().create();


    /**
     * Write file map to tmp file on disk
     *
     * @param content
     * @param filename
     * @return
     */
    public static final File writeFile(Map<Movie, RawSearch> content, String filename) {
        String jsonContent = gson.toJson(content);

        String filePath = "/tmp/" + filename;

        File targetFile = new File(filePath);

        try {
            FileWriter fileWriter = new FileWriter(filePath);

            fileWriter.write(jsonContent);
            fileWriter.flush();
            fileWriter.close();

            logger.info("Wrote to file " + filePath);

            //this.file.clear();
        } catch (IOException e) {
            logger.error("Cannot write file to disk");
        }

        return targetFile;
    }
}
