import com.holub.life.LifeBoard;
import com.holub.life.LifePattern;
import com.holub.life.PatternReader;
import com.holub.life.PatternReaders;
import com.holub.life.PatternRegistry;
import com.holub.life.PlaintextPatternReader;
import com.holub.life.RlePatternAdapter;
import com.holub.rle.RleData;
import com.holub.rle.RleDecoder;

import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class PatternImportTest {

    private static final String GLIDER_RLE =
            "#N Glider\n"
            + "x = 3, y = 3, rule = B3/S23\n"
            + "bob$2bo$3o!\n";

    // ---- the adaptee on its own

    @Test
    void decoderReadsHeaderAndCells() {
        RleData data = new RleDecoder().decode(GLIDER_RLE);

        assertEquals("Glider", data.getName());
        assertEquals("B3/S23", data.getRule());
        assertEquals(3, data.getWidth());
        assertEquals(3, data.getHeight());
        assertEquals(5, data.getLiveCells().size());
    }

    @Test
    void decoderHandlesRunCountsAndBlankLines() {
        // "3$" skips three rows at once
        RleData data = new RleDecoder().decode("x = 2, y = 4\no3$bo!");

        assertArrayEquals(new int[] {0, 0}, data.getLiveCells().get(0));
        assertArrayEquals(new int[] {1, 3}, data.getLiveCells().get(1));
    }

    @Test
    void decoderRejectsMissingHeader() {
        assertThrows(IllegalArgumentException.class,
                () -> new RleDecoder().decode("bob$2bo$3o!"));
    }

    // ---- the adapter

    @Test
    void adapterGivesTheSameGliderAsTheBuiltInOne() throws IOException {
        LifePattern imported =
                new RlePatternAdapter().read(new StringReader(GLIDER_RLE), "x");

        assertEquals("Glider", imported.getName());
        assertTrue(imported.sameShapeAs(PatternRegistry.getDefault().create("glider")));
    }

    @Test
    void adapterRemembersTheRule() throws IOException {
        RlePatternAdapter adapter = new RlePatternAdapter();
        adapter.read(new StringReader("x = 1, y = 1, rule = B36/S23\no!"), "dot");

        assertEquals("B36/S23", adapter.getLastRule());
    }

    @Test
    void adapterUsesDefaultNameWhenFileHasNone() throws IOException {
        LifePattern pattern =
                new RlePatternAdapter().read(new StringReader("x = 2, y = 1\n2o!"), "pair");

        assertEquals("pair", pattern.getName());
        assertEquals(2, pattern.getAliveCount());
    }

    @Test
    void adapterTurnsDecoderErrorsIntoIOException() {
        assertThrows(IOException.class,
                () -> new RlePatternAdapter().read(new StringReader("garbage"), "x"));
    }

    // ---- plaintext format and choosing a reader

    @Test
    void plaintextReaderReadsCellsFormat() throws IOException {
        String text = "!Name: Blinker\n!a comment\n...\nOOO\n...\n";
        LifePattern pattern = new PlaintextPatternReader().read(new StringReader(text), "x");

        assertEquals("Blinker", pattern.getName());
        assertTrue(pattern.sameShapeAs(PatternRegistry.getDefault().create("blinker")));
    }

    @Test
    void readerIsChosenByExtension() {
        assertTrue(PatternReaders.forFileName("gun.RLE") instanceof RlePatternAdapter);
        assertTrue(PatternReaders.forFileName("a.cells") instanceof PlaintextPatternReader);
        assertThrows(IllegalArgumentException.class,
                () -> PatternReaders.forFileName("picture.png"));
    }

    // ---- real files in testdata/

    private LifePattern readFile(String fileName) throws IOException {
        String path = "testdata/patterns/" + fileName;
        PatternReader reader = PatternReaders.forFileName(path);
        try (Reader in = new FileReader(path)) {
            return reader.read(in, PatternReaders.baseName(path));
        }
    }

    @Test
    void importedGosperGunMatchesTheBuiltInGun() throws IOException {
        LifePattern gun = readFile("gosperglidergun.rle");

        assertEquals(36, gun.getAliveCount());
        assertTrue(gun.sameShapeAs(PatternRegistry.getDefault().create("Gosper Glider Gun")));
    }

    @Test
    void importedDiehardDiesAfter130Generations() throws IOException {
        LifeBoard board = new LifeBoard(8, 8);
        board.placePattern(readFile("diehard.cells"), 30, 20);

        for (int i = 0; i < 129; i++) {
            board.tick();
        }
        assertTrue(board.getAliveCellCount() > 0);

        board.tick();
        assertEquals(0, board.getAliveCellCount());
    }

    @Test
    void importedPatternCanBeRegistered() throws IOException {
        PatternRegistry registry = new PatternRegistry();
        registry.register(readFile("acorn.rle"));

        assertEquals(7, registry.create("Acorn").getAliveCount());
    }
}
