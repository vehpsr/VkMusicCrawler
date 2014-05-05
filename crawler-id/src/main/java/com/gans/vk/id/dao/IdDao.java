package com.gans.vk.id.dao;

import java.util.List;

public interface IdDao {

    List<String> getAllIds(String path);

    List<String> getGroups(String path);
}
