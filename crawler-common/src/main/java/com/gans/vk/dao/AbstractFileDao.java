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

    protected List<String> readFile(File file, ReadMode readMode) {
        return readFile(file.getPath(), readMode);
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


    protected List<String> readFiles(String dir) {
        if (StringUtils.isEmpty(dir)) {
            return Collections.emptyList();
        }

        File directory = new File(dir);
        directory.getParentFile().mkdirs();

        List<String> result = new ArrayList<String>();
        for (File file : directory.listFiles()) {
            result.addAll(readFile(file, null));
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

    protected void save(String dir, String fileName, Collection<String> lines) {
        List<String> fileNames = getAllFileNamesInDirectory(dir);
        String newFileName = fileName + EXTENSION;
        if (fileNames.contains(newFileName)) {
            LOG.warn(MessageFormat.format("File wiht name {0} already exists in directory {1}", newFileName, dir));
            return;
        }
        appendToFile(dir + newFileName, lines);
    }

    protected void appendToFile(String path, Collection<String> lines) {
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

    protected List<String> getAllFileNamesInDirectory(String path) {
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
