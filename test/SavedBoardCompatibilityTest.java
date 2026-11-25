import com.holub.life.LifeBoard;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The .life files in testdata/saved were written by the game before
 * the refactoring. They must still load, so Grid > Load keeps working
 * for old files.
 */
public class SavedBoardCompatibilityTest {

    private File[] savedFiles() {
        File[] files = new File("testdata/saved").listFiles((dir, name) -> name.endsWith(".life"));
        assertNotNull(files, "testdata/saved folder is missing");
        assertTrue(files.length > 0);
        return files;
    }

    // the original format is a serialized list of the live cell positions
    private int cellsStoredIn(File file) throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            // a file can list the same cell twice, so count each position once
            return new HashSet<>((Collection<?>) in.readObject()).size();
        }
    }

    @Test
    void oldSaveFilesStillLoad() throws Exception {
        int nonEmptyFiles = 0;

        for (File file : savedFiles()) {
            LifeBoard board = new LifeBoard();

            try (InputStream in = new FileInputStream(file)) {
                board.load(in);
            }

            assertEquals(cellsStoredIn(file), board.getAliveCellCount(), file.getName());
            assertEquals(0, board.getGeneration());

            if (board.getAliveCellCount() > 0) {
                nonEmptyFiles++;
            }
        }

        assertTrue(nonEmptyFiles >= 3, "some files should have cells in them");
    }

    @Test
    void loadedBoardIsSavedAgainUnchanged() throws IOException {
        for (File file : savedFiles()) {
            LifeBoard board = new LifeBoard();
            try (InputStream in = new FileInputStream(file)) {
                board.load(in);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            board.store(out);

            LifeBoard copy = new LifeBoard();
            copy.load(new ByteArrayInputStream(out.toByteArray()));

            assertEquals(new HashSet<>(board.getAliveCells()),
                         new HashSet<>(copy.getAliveCells()), file.getName());
        }
    }
}
