package com.gans.vk.dao;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
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

    public void appendToFile(String path, Collection<String> lines) {
        if (CollectionUtils.isEmpty(lines) || StringUtils.isEmpty(path)) {
            return;
        }

        PrintWriter pw = null;
        try {
            File file = createIfDontExist(path);
            pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));

            for (String line : lines) {
                pw.println(line);
            }
        } catch (IOException e) {
            throw new IllegalStateException(MessageFormat.format("Exception while writing to file: {0}", path), e);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }
}
