package com.gans.vk.id.service;

import java.util.List;

import com.gans.vk.data.GroupInfo;

public interface IdService {

    List<String> getExistingIds();

    List<String> getGroups();

    List<GroupInfo> getGroupInfos(List<String> groups);

    List<String> discoverGroupMembersId(GroupInfo groupInfo);

    void saveNewIds(List<String> newIds);
}
