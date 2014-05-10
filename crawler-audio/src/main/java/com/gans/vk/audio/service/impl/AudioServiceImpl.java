package com.gans.vk.audio.service.impl;

import static com.gans.vk.context.SystemProperties.Property.VK_AUDIO_ENTITY_PATTERN;
import static com.gans.vk.context.SystemProperties.Property.VK_AUDIO_URL;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gans.vk.audio.dao.AudioDao;
import com.gans.vk.audio.dao.impl.AudioDaoImpl;
import com.gans.vk.audio.parser.AudioLibrary;
import com.gans.vk.audio.parser.AudioParser;
import com.gans.vk.audio.service.AudioService;
import com.gans.vk.context.SystemProperties;
import com.gans.vk.httpclient.HttpVkConnector;
import com.gans.vk.utils.HtmlUtils;
import com.gans.vk.utils.RestUtils;

public class AudioServiceImpl implements AudioService {

    private static final Log LOG = LogFactory.getLog(AudioServiceImpl.class);

    private AudioDao _audioDao;
    private HttpVkConnector _httpVkConnector;
    private boolean _debug = SystemProperties.debug();

    private static AudioService _instance = new AudioServiceImpl();

    private AudioServiceImpl() {
        _audioDao = AudioDaoImpl.getInstance();
        _httpVkConnector = HttpVkConnector.getInstance();
    }

    public static AudioService getInstance() {
        return _instance;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<String> getIdsForAudioDiscovery() {
        List<String> stashIds = _audioDao.getAllIdsFromStash();
        List<String> alreadyProcessedIds = _audioDao.getAlreadyProcessedIds();
        return CollectionUtils.subtract(stashIds, alreadyProcessedIds);
    }

    @Override
    public void collectAudioInfo(Collection<String> ids) {
        String url = SystemProperties.get(VK_AUDIO_URL);
        String entityPattern = SystemProperties.get(VK_AUDIO_ENTITY_PATTERN);
        for (String id : ids) {
            LOG.debug(MessageFormat.format("Discover audio library by id: {0}", id));

            String response = _httpVkConnector.post(url, MessageFormat.format(entityPattern, id)); //TODO check for invalid auth
            String[] jsonCollection = HtmlUtils.sanitizeJson(response);
            if (jsonCollection.length == 0) {
                LOG.warn(MessageFormat.format("Fail to parse response from VK for id: {0}", id));
                LOG.debug(MessageFormat.format("VK response:\n{0}", response));
                RestUtils.sleep("2x");
                if (_debug) break;

                continue;
            }

            String json = jsonCollection[0];
            AudioLibrary lib = AudioParser.parse(json);

            if (_debug) break;
        }

    }

}
