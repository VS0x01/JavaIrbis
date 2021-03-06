// This is an open source non-commercial project. Dear PVS-Studio, please check it.
// PVS-Studio Static Code Analyzer for C, C++ and C#: http://www.viva64.com

package ru.arsmagna.aot;

import org.junit.Test;

import static org.junit.Assert.*;

public class RussianStemmerTest {

    @Test
    public void stem_1() {
        RussianStemmer stemmer = new RussianStemmer();

        assertEquals("красот", stemmer.stem("красота"));
        assertEquals("красот", stemmer.stem("красоту"));
        assertEquals("красот", stemmer.stem("красоте"));
        assertEquals("красот", stemmer.stem("красотой"));
    }
}