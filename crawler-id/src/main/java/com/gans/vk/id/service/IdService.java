package com.gans.vk.id.service;

import java.util.List;

public interface IdService {

    List<String> getExistingIds();

    List<String> getGroups();

    List<String> discoverNewIds(List<String> groups, List<String> existingIds);
}
