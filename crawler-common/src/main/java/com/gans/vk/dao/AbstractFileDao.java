package com.gans.vk.dao;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AbstractFileDao {

    private static final Log LOG = LogFactory.getLog(AbstractFileDao.class);
    private static final String EXTENSION = ".txt";

    public enum ReadMode {
        UNIQUE
    }

    protected List<String> readFile(String path, ReadMode readMode) {
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

        if (ReadMode.UNIQUE.equals(readMode)) {
            return getUniqueEntries(result);
        }
        return result;
    }

    private File createIfDontExist(String path) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    private List<String> getUniqueEntries(List<String> list) {
        Set<String> set = new HashSet<String>(list);
        if (set.size() != list.size()) {
            LOG.warn(MessageFormat.format("Stash contains duplicate entry. Reduce from {0} to {1}",list.size(), set.size()));
        }
        return new LinkedList<String>(set);
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

    public List<String> getAllFileNamesInDirectory(String path) {
        File directory = new File(path);
        directory.mkdirs();
        String[] files = directory.list();
        List<String> result = new LinkedList<String>();
        for (String file : files) {
            String fileName = file.substring(0, file.lastIndexOf(EXTENSION));
            if (StringUtils.isNotEmpty(fileName)) {
                result.add(fileName);
            }
        }
        return result;
    }

}
