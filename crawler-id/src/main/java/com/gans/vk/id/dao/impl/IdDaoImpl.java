package com.gans.vk.id.dao.impl;

import java.util.List;

import com.gans.vk.dao.AbstractFileDao;
import com.gans.vk.id.dao.IdDao;

public class IdDaoImpl extends AbstractFileDao implements IdDao {

    private static IdDao _idDao = new IdDaoImpl();

    public static IdDao getInstance() {
        return _idDao;
    }

    @Override
    public List<String> getAllIds(String path) {
        return readFile(path);
    }

    @Override
    public List<String> getGroups(String path) {
        return readFile(path);
    }

}
