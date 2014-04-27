package com.gans.vk.id.service.impl;

import java.util.List;

import com.gans.vk.context.SystemProperties;
import static com.gans.vk.context.SystemProperties.Property.*;
import com.gans.vk.id.data.IdDao;
import com.gans.vk.id.data.impl.IdDaoImpl;
import com.gans.vk.id.service.IdService;

public class IdServiceImpl implements IdService {

    private IdDao _idDao;

    private static IdService _idService = new IdServiceImpl();

    private IdServiceImpl() { /* singleton */
        _idDao = IdDaoImpl.getInstance();
    }

    public static IdService getInstance() {
        return _idService;
    }

    @Override
    public List<String> getExistingIds() {
        String path = SystemProperties.get(CRAWLER_ID_STASH);
        return _idDao.getAllIds(path);
    }
}
