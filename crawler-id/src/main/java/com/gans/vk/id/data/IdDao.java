package com.gans.vk.id.data;

import java.util.List;

public interface IdDao {

    List<String> getAllIds(String path);

    List<String> getGroups(String path);
}
