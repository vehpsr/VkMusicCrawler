package com.gans.vk.id.dao.impl;

import static com.gans.vk.context.SystemProperties.Property.CRAWLER_GROUP_STASH;
import static com.gans.vk.context.SystemProperties.Property.CRAWLER_ID_STASH;

import java.text.MessageFormat;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gans.vk.context.SystemProperties;
import com.gans.vk.dao.AbstractFileDao;
import com.gans.vk.id.dao.IdDao;

public class IdDaoImpl extends AbstractFileDao implements IdDao {

    private static final Log LOG = LogFactory.getLog(IdDaoImpl.class);
    private final String ID_STORAGE_PATH = SystemProperties.get(CRAWLER_ID_STASH);
    private final String GROUP_STORAGE_PATH = SystemProperties.get(CRAWLER_GROUP_STASH);
    private static IdDao _idDao = new IdDaoImpl();

    public static IdDao getInstance() {
        return _idDao;
    }

    @Override
    public List<String> getAllIds() {
        return getUniqueElements(ID_STORAGE_PATH);
    }

    @Override
    public List<String> getGroups() {
        return getUniqueElements(GROUP_STORAGE_PATH);
    }

    private List<String> getUniqueElements(String path) {
        List<String> list = readFile(path);
        Set<String> set = new HashSet<String>(list);
        if (set.size() != list.size()) {
            LOG.warn(MessageFormat.format("Stash {0} contains duplicate entry. Reduce from {1} to {2}", path, list.size(), set.size()));
        }
        return new LinkedList<String>(set);
    }

    @Override
    public void saveIds(Collection<String> idsToSave) {
        appendToFile(ID_STORAGE_PATH, idsToSave);
    }

}
