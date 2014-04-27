package com.gans.vk.id;

import java.util.Collections;
import java.util.List;

import com.gans.vk.id.service.IdService;
import com.gans.vk.id.service.impl.IdServiceImpl;

public class VkIdCrawler {

    public static void main(String[] args) {
        new VkIdCrawler().start();
    }

    private IdService _idService;

    private VkIdCrawler() {
        _idService = IdServiceImpl.getInstance();
    }

    private void start() {
        List<String> existingIds = _idService.getExistingIds();
        Collections.sort(existingIds);
    }
}
