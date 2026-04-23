package com.xettuyen.service.imports;

import java.io.File;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface ImportTask {
    ImportResult execute(File file, BiConsumer<Integer, String> onProgress) throws Exception;
}