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
    private static final String COMMENT = "#";

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
        try (BufferedReader reader = new BufferedReader(new FileReader(getFile(path)))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (StringUtils.isBlank(line) || line.startsWith(COMMENT)) {
                    continue;
                }
                result.add(line.trim());
            }
        } catch (IOException e) {
            throw new IllegalStateException(MessageFormat.format("Exception while reading file: {0}", path), e);
        }

        if (ReadMode.UNIQUE.equals(readMode)) {
            return getUniqueEntries(result);
        }
        return result;
    }

    protected List<String> readFiles(String dir) {
        List<String> result = new ArrayList<String>();
        for (File file : filterFiles(dir)) {
            result.addAll(readFile(file, null));
        }
        return result;
    }

    protected Map<String, List<String>> readAllFilesInDirectory(String dir) {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        for (File file : filterFiles(dir)) {
            String name = getFileNameWithoutExtension(file);
            result.put(name, readFile(file, null));
        }
        return result;
    }

    protected List<String> readFileFromDirectory(String dir, String fileName) {
        return readFile(dir + fileName + EXTENSION, null);
    }

    private File getFile(String path) throws IOException {
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

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(getFile(path), true)))) {
            for (String line : lines) {
                pw.println(line);
            }
        } catch (IOException e) {
            throw new IllegalStateException(MessageFormat.format("Exception while writing to file: {0}", path), e);
        }
    }

    protected List<String> getAllFileNamesInDirectory(String dir) {
        List<String> result = new LinkedList<String>();
        for (File file : filterFiles(dir)) {
            String name = getFileNameWithoutExtension(file);
            result.add(name);
        }
        return result;
    }

    private List<File> filterFiles(String dir) {
        if (StringUtils.isEmpty(dir)) {
            return Collections.emptyList();
        }
        File directory = new File(dir);
        directory.mkdirs();

        File[] files = directory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(EXTENSION);
            }
        });

        if (files == null) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(files);
        }
    }

    private String getFileNameWithoutExtension(File file) {
        String fileName = file.getName();
        return fileName.substring(0, fileName.lastIndexOf(EXTENSION));
    }
}
