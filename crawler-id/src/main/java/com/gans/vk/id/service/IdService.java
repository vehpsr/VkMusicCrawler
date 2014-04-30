package com.gans.vk.id.service;

import java.util.List;

import com.gans.vk.data.GroupInfo;

public interface IdService {

    List<String> getExistingIds();

    List<String> getGroups();

    List<GroupInfo> getGroupInfos(List<String> groups);

    void discoverNewIds(GroupInfo groupInfo);

    void addIds(List<String> newIds);
}
