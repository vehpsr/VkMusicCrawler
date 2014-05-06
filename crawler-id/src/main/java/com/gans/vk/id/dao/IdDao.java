package com.gans.vk.id.dao;

import java.util.Collection;
import java.util.List;

public interface IdDao {

    List<String> getAllIds();

    List<String> getGroups();

    void saveIds(Collection<String> idsToSave);
}
