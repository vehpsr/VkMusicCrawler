package com.gans.vk.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class AbstractFileDao {

    protected List<String> readFile(String path) {
        if (StringUtils.isEmpty(path)) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            File file = createIfDontExist(path);
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    result.add(line.trim());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(MessageFormat.format("Exception while reading file: {0}", path), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new IllegalStateException(MessageFormat.format("Exception while closing file: {0}", path), e);
                }
            }
        }
        return result;
    }

    private File createIfDontExist(String path) throws IOException {
        File file = null;
        file = new File(path);
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }
}
