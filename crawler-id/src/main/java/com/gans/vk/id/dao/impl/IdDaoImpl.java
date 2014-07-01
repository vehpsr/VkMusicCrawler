package com.gans.vk.id.dao.impl;

import static com.gans.vk.context.SystemProperties.Property.*;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;

import com.gans.vk.context.SystemProperties;
import com.gans.vk.dao.AbstractFileDao;
import com.gans.vk.id.dao.IdDao;

public class IdDaoImpl extends AbstractFileDao implements IdDao {

    private final String ID_STORAGE_PATH = SystemProperties.get(CRAWLER_ID_STASH);
    private final String GROUP_STORAGE_PATH = SystemProperties.get(CRAWLER_GROUP_STASH);
    private final String URL_STORAGE_PATH = SystemProperties.get(CRAWLER_URL_STASH);
    private static IdDao _idDao = new IdDaoImpl();

    public static IdDao getInstance() {
        return _idDao;
    }

    @Override
    public List<String> getAllIds() {
        return readFile(ID_STORAGE_PATH, ReadMode.UNIQUE);
    }

    @Override
    public List<String> getGroups() {
        return readFile(GROUP_STORAGE_PATH, ReadMode.UNIQUE);
    }



    @Override
    public void saveIds(Collection<String> idsToSave) {
        appendToFile(ID_STORAGE_PATH, idsToSave);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getUniqueUrls(List<String> urls) {
        List<String> allUrls = readFile(URL_STORAGE_PATH, ReadMode.UNIQUE);
        Collection<String> uniqueUrls = CollectionUtils.subtract(urls, allUrls);
        return new LinkedList<String>(uniqueUrls);
    }

    @Override
    public void saveUrls(Collection<String> urls) {
        appendToFile(URL_STORAGE_PATH, urls);
    }

}
