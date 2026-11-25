package com.holub.life;

import java.io.IOException;
import java.io.Reader;

/** Reads a LifePattern from some text format. */
public interface PatternReader {

    /**
     * @param defaultName used when the file itself has no name in it
     */
    LifePattern read(Reader in, String defaultName) throws IOException;
}
