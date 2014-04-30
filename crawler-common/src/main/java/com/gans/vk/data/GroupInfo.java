package com.gans.vk.data;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;

public class GroupInfo {

     private String _groupId;
     private int _membersCount;

     public boolean isValid() {
         return StringUtils.isNotEmpty(_groupId) && _membersCount > 0;
     }

     public String getGroupId() {
        return _groupId;
    }

    public void setGroupId(String groupId) {
        _groupId = groupId;
    }

    public int getMembersCount() {
        return _membersCount;
    }

    public void setMembersCount(int membersCount) {
        _membersCount = membersCount;
    }

    @Override
     public String toString() {
         return MessageFormat.format("[{0},{1}]", _groupId, _membersCount);
     }
}
