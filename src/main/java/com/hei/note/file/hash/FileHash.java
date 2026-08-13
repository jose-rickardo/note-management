package com.hei.note.file.hash;

import com.hei.note.PojaGenerated;

@PojaGenerated
public record FileHash(FileHashAlgorithm algorithm, String value) {}
