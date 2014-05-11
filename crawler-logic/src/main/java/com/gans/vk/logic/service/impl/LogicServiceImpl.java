package com.gans.vk.logic.service.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gans.vk.data.ArtistData;
import com.gans.vk.logic.dao.LogicDao;
import com.gans.vk.logic.dao.impl.LogicDaoImpl;
import com.gans.vk.logic.service.LogicService;

public class LogicServiceImpl implements LogicService {

    private static final Log LOG = LogFactory.getLog(LogicServiceImpl.class);
    private static LogicService _instance = new LogicServiceImpl();
    private LogicDao _logicDao;

    private LogicServiceImpl() {
        _logicDao = LogicDaoImpl.getInstance();
    }

    public static LogicService getInstance() {
        return _instance;
    }

    @Override
    public List<ArtistData> getWhiteList() {
        return _logicDao.getWhiteList();
    }

    @Override
    public List<ArtistData> getBlackList() {
        return _logicDao.getBlackList();
    }

}
